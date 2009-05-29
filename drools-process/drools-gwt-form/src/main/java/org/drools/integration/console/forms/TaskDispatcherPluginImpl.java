package org.drools.integration.console.forms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.SystemEventListenerFactory;
import org.drools.task.I18NText;
import org.drools.task.Task;
import org.drools.task.service.MinaTaskClient;
import org.drools.task.service.TaskClientHandler;
import org.drools.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jboss.bpm.console.server.plugin.TaskDispatcherPlugin;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * @author Kris Verlaenen
 */
public class TaskDispatcherPluginImpl implements TaskDispatcherPlugin {

	// TODO make this configurable
	private String ipAddress = "127.0.0.1";
	private int port = 9123;
	private MinaTaskClient client;

	public TaskDispatcherPluginImpl() {
		System.out.println("Created TaskDispatcherPlugin");
	}

	public void connect() {
		if (client == null) {
			client = new MinaTaskClient(
				"org.drools.process.workitem.wsht.WSHumanTaskHandler",
                    new TaskClientHandler(SystemEventListenerFactory.getSystemEventListener()));
			NioSocketConnector connector = new NioSocketConnector();
			SocketAddress address = new InetSocketAddress(ipAddress, port);
			boolean connected = client.connect(connector, address);
			if (!connected) {
				throw new IllegalArgumentException(
					"Could not connect task client");
			}
		}
	}
	
	public URL getDispatchUrl(long taskId) {
		StringBuffer sb = new StringBuffer();
		// TODO: fix this for non localhost:8080
		sb.append("http://");
		sb.append("localhost");
		sb.append(":").append(8080);
		sb.append("/gwt-console-server/rs/task/");
		sb.append(taskId);
		sb.append("/render");

		try {
			return new URL(sb.toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException("Failed to resolve task dispatch url", e);
		}
	}
	
	public DataHandler provideTaskUI(long taskId) {
		connect();
		BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
    	client.getTask(taskId, getTaskResponseHandler);
		Task task = getTaskResponseHandler.getTask();

		// check if a template exists
		String name = null;
		List<I18NText> names = task.getNames();
		for (I18NText text: names) {
			if ("en-UK".equals(text.getLanguage())) {
				name = "/" + text.getText() + ".ftl";
			}
		}
		InputStream template = TaskDispatcherPluginImpl.class.getResourceAsStream(name);
		if (template == null) {
			System.out.println("Could not find template " + name);
			template = TaskDispatcherPluginImpl.class.getResourceAsStream("/DefaultTask.ftl");
		}
		System.out.println("Using template " + name);

		// merge template with process variables
		// TODO: fix this for non localhost:8080
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append("localhost");
		sb.append(":").append(8080);
		sb.append("/gwt-console-server/rs/task/");
		sb.append(taskId);
		sb.append("/process");
		Map<String, Object> renderContext = new HashMap<String, Object>();
		renderContext.put("task", task);	
		return processTemplate(name, template, renderContext);
	}

	private DataHandler processTemplate(final String name, InputStream src,	Map<String, Object> renderContext) {
		DataHandler merged = null;
		try {
			freemarker.template.Configuration cfg = new freemarker.template.Configuration();
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setTemplateUpdateDelay(0);
			Template temp = new Template(name, new InputStreamReader(src), cfg);
			final ByteArrayOutputStream bout = new ByteArrayOutputStream();
			Writer out = new OutputStreamWriter(bout);
			temp.process(renderContext, out);
			out.flush();
			merged = new DataHandler(new DataSource() {
				public InputStream getInputStream() throws IOException {
					return new ByteArrayInputStream(bout.toByteArray());
				}
				public OutputStream getOutputStream() throws IOException {
					return bout;
				}
				public String getContentType() {
					return "*/*";
				}
				public String getName() {
					return name + "_DataSource";
				}
			});
		} catch (Exception e) {
			throw new RuntimeException("Failed to process task template", e);
		}
		return merged;
	}

	public void processCompletion(long taskId, String outcome, InputStream payload) {
		// Do nothing
	}
	
}
