package org.drools.process.workitem.wsht;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.eventmessaging.EventKey;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemHandler;
import org.drools.process.instance.WorkItemManager;
import org.drools.task.I18NText;
import org.drools.task.OrganizationalEntity;
import org.drools.task.PeopleAssignments;
import org.drools.task.Task;
import org.drools.task.TaskData;
import org.drools.task.User;
import org.drools.task.event.TaskCompletedEvent;
import org.drools.task.event.TaskEventKey;
import org.drools.task.service.MinaTaskClient;
import org.drools.task.service.TaskClientHandler;

public class WSHumanTaskHandler implements WorkItemHandler {

	private String ipAddress = "127.0.0.1";
	private int port = 9123;
	private MinaTaskClient client;
	private Map<Long, WorkItemManager> managers = new HashMap<Long, WorkItemManager>();

	public void setConnection(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	private void createClient() {
		client = new MinaTaskClient(
			"org.drools.process.workitem.wsht.WSHumanTaskHandler", new TaskClientHandler());
		NioSocketConnector connector = new NioSocketConnector();
		SocketAddress address = new InetSocketAddress(ipAddress, port);
		client.connect(connector, address);
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		if (client == null) {
			createClient();
		}
		Task task = new Task();
		String taskName = (String) workItem.getParameter("TaskName");
		if (taskName != null) {
			List<I18NText> names = new ArrayList<I18NText>();
			names.add(new I18NText("en-UK", taskName));
			task.setNames(names);
		}
		String comment = (String) workItem.getParameter("Comment");
		if (comment != null) {
			List<I18NText> descriptions = new ArrayList<I18NText>();
			descriptions.add(new I18NText("en-UK", comment));
			task.setDescriptions(descriptions);
			List<I18NText> subjects = new ArrayList<I18NText>();
			subjects.add(new I18NText("en-UK", comment));
			task.setSubjects(subjects);
		}
		String priority = (String) workItem.getParameter("Priority");
		if (priority != null) {
			try {
				task.setPriority(new Integer(priority));
			} catch (NumberFormatException e) {
				// do nothing
			}
		}
		TaskData taskData = new TaskData();
		taskData.setWorkItemId(workItem.getId());
		task.setTaskData(taskData);
		String actorId = (String) workItem.getParameter("ActorId");
		if (actorId != null) {
			PeopleAssignments assignments = new PeopleAssignments();
			List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
			User user = new User();
			user.setId(actorId);
			potentialOwners.add(user);
			assignments.setPotentialOwners(potentialOwners);
			task.setPeopleAssignments(assignments);
		}
		
		BlockingAddTaskResponseHandler taskResponseHandler = new BlockingAddTaskResponseHandler();
		client.addTask(task, taskResponseHandler);
		long taskId = taskResponseHandler.getTaskId();
		managers.put(taskId, manager);
		System.out.println("Created task " + taskId + " for work item " + workItem.getId());
		
		new Thread(new WaitForEvent(taskId)).run();
	}
	
	public void dispose() {
		if (client != null) {
			client.disconnect();
		}
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO
	}
	
	private class WaitForEvent implements Runnable {
		private long taskId;
		public WaitForEvent(long taskId) {
			this.taskId = taskId;
		}
		public void run() {
			EventKey key = new TaskEventKey(TaskCompletedEvent.class, taskId );           
	        BlockingEventResponseHandler eventResponseHandler = new BlockingEventResponseHandler(); 
	        client.registerForEvent( key, true, eventResponseHandler );
	        eventResponseHandler.getPayload();
	        
	        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
	        client.getTask(taskId, getTaskResponseHandler);
	        Task task = getTaskResponseHandler.getTask();
	        long workItemId = task.getTaskData().getWorkItemId();
        	System.out.println("Completing work item " + workItemId);
	        
	        managers.get(taskId).completeWorkItem(workItemId, null);
		}
	}

}
