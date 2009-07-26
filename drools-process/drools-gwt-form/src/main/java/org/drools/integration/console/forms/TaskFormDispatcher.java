package org.drools.integration.console.forms;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.SystemEventListenerFactory;
import org.drools.task.I18NText;
import org.drools.task.Task;
import org.drools.task.service.MinaTaskClient;
import org.drools.task.service.TaskClientHandler;
import org.drools.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jboss.bpm.console.server.plugin.FormAuthorityRef;

/**
 * @author Kris Verlaenen
 */
public class TaskFormDispatcher extends AbstractFormDispatcher {

	// TODO make this configurable
	private String ipAddress = "127.0.0.1";
	private int port = 9123;
	private MinaTaskClient client;

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
	
	public DataHandler provideForm(FormAuthorityRef ref) {
		connect();
		BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
    	client.getTask(new Long(ref.getReferenceId()), getTaskResponseHandler);
		Task task = getTaskResponseHandler.getTask();

		// check if a template exists
		String name = null;
		List<I18NText> names = task.getNames();
		for (I18NText text: names) {
			if ("en-UK".equals(text.getLanguage())) {
				name = text.getText();
			}
		}
		InputStream template = getTemplate(name);
		if (template == null) {
			template = TaskFormDispatcher.class.getResourceAsStream("/DefaultTask.ftl");
		}

		// merge template with process variables
		Map<String, Object> renderContext = new HashMap<String, Object>();
		renderContext.put("task", task);	
		return processTemplate(name, template, renderContext);
	}

}
