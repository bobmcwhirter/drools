package org.drools.integration.console.forms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import org.drools.SystemEventListenerFactory;
import org.drools.task.Content;
import org.drools.task.I18NText;
import org.drools.task.Task;
import org.drools.task.service.TaskClient;
import org.drools.task.service.mina.MinaTaskClientConnector;
import org.drools.task.service.mina.MinaTaskClientHandler;
import org.drools.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.drools.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jboss.bpm.console.server.plugin.FormAuthorityRef;

/**
 * @author Kris Verlaenen
 */
public class TaskFormDispatcher extends AbstractFormDispatcher {

	// TODO make this configurable
	private String ipAddress = "127.0.0.1";
	private int port = 9123;
	private TaskClient client;

	public void connect() {
		if (client == null) {
			client = new TaskClient(new MinaTaskClientConnector("org.drools.process.workitem.wsht.WSHumanTaskHandler",
									new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
			boolean connected = client.connect(ipAddress, port);
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
		Object input = null;
		long contentId = task.getTaskData().getDocumentContentId();
		if (contentId != -1) {
			BlockingGetContentResponseHandler getContentResponseHandler = new BlockingGetContentResponseHandler();
	    	client.getContent(contentId, getContentResponseHandler);
	    	Content content = getContentResponseHandler.getContent();
	    	ByteArrayInputStream bis = new ByteArrayInputStream(content.getContent());
			ObjectInputStream in;
			try {
				in = new ObjectInputStream(bis);
				input = in.readObject();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

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
		renderContext.put("content", input);
		if (input instanceof Map) {
			Map<?, ?> map = (Map) input;
			for (Map.Entry<?, ?> entry: map.entrySet()) {
				if (entry.getKey() instanceof String) {
					renderContext.put((String) entry.getKey(), entry.getValue());
				}
			}
		}
		return processTemplate(name, template, renderContext);
	}

}
