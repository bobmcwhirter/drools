package org.drools.integration.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.SystemEventListenerFactory;
import org.drools.process.workitem.wsht.BlockingGetTaskResponseHandler;
import org.drools.task.AccessType;
import org.drools.task.Status;
import org.drools.task.Task;
import org.drools.task.query.TaskSummary;
import org.drools.task.service.ContentData;
import org.drools.task.service.MinaTaskClient;
import org.drools.task.service.TaskClientHandler;
import org.drools.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.drools.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jboss.bpm.console.client.model.TaskRef;
import org.jboss.bpm.console.server.integration.TaskManagement;

public class DroolsFlowTaskManagement implements TaskManagement {
	
	// TODO: make this configurable
	private String ipAddress = "127.0.0.1";
	private int port = 9123;
	private MinaTaskClient client;

	public void setConnection(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
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
	
	public TaskRef getTaskById(long taskId) {
		connect();
		BlockingGetTaskResponseHandler responseHandler = new BlockingGetTaskResponseHandler();
		client.getTask(taskId, responseHandler);
        Task task = responseHandler.getTask();
        return DroolsFlowTransform.task(task);
	}

	public void assignTask(long taskId, String idRef, String userId) {
		connect();
		BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
		if (idRef == null) {
			client.release(taskId, userId, responseHandler);
		} else if (idRef.equals(userId)) {
			client.claim(taskId, idRef, responseHandler);
		} else {
			client.delegate(taskId, userId, idRef, responseHandler);
		}
		responseHandler.waitTillDone(5000);
	}

	@SuppressWarnings("unchecked")
	public void completeTask(long taskId, Map data, String userId) {
		connect();
		BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
		client.start(taskId, userId, responseHandler);
		responseHandler.waitTillDone(5000);
		responseHandler = new BlockingTaskOperationResponseHandler();
		ContentData contentData = null;
		if (data != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out;
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(data);
				out.close();
				contentData = new ContentData();
				contentData.setContent(bos.toByteArray());
				contentData.setAccessType(AccessType.Inline);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		client.complete(taskId, userId, contentData, responseHandler);
		responseHandler.waitTillDone(5000);
	}

	@SuppressWarnings("unchecked")
	public void completeTask(long taskId, String outcome, Map data, String userId) {
		data.put("outcome", outcome);
		completeTask(taskId, data, userId);
	}

	public void releaseTask(long taskId, String userId) {
		// TODO: this method is not being invoked, it's using
		// assignTask with null parameter instead
		connect();
		BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
		client.release(taskId, userId, responseHandler);
		responseHandler.waitTillDone(5000);
	}

	public List<TaskRef> getAssignedTasks(String idRef) {
		connect();
        List<TaskRef> result = new ArrayList<TaskRef>();
		try {
			BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
			client.getTasksOwned(idRef, "en-UK", responseHandler);
	        List<TaskSummary> tasks = responseHandler.getResults();
	        for (TaskSummary task: tasks) {
	        	if (task.getStatus() == Status.Reserved) {
	        		result.add(DroolsFlowTransform.task(task));
	        	}
	        }
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return result;
	}

	public List<TaskRef> getUnassignedTasks(String idRef, String participationType) {
		// TODO participationType ?
		connect();
        List<TaskRef> result = new ArrayList<TaskRef>();
		try {
			BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
			client.getTasksAssignedAsPotentialOwner(idRef, "en-UK", responseHandler);
	        List<TaskSummary> tasks = responseHandler.getResults();
	        for (TaskSummary task: tasks) {
	        	result.add(DroolsFlowTransform.task(task));
	        }
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return result;
	}

}
