package org.drools.process.workitem.wsht;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemHandler;
import org.drools.process.instance.WorkItemManager;
import org.drools.task.I18NText;
import org.drools.task.OrganizationalEntity;
import org.drools.task.PeopleAssignments;
import org.drools.task.Status;
import org.drools.task.Task;
import org.drools.task.TaskData;
import org.drools.task.User;
import org.drools.task.service.MinaTaskClient;
import org.drools.task.service.TaskClientHandler;
import org.drools.task.service.TaskClientHandler.AddTaskResponseHandler;

public class WSHumanTaskHandler implements WorkItemHandler {

	private String ipAddress = "127.0.0.1";
	private int port = 9123;

	public void setConnection(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO: cache client and keep client open ?
		MinaTaskClient client = new MinaTaskClient(
			"org.drools.process.workitem.wsht.WSHumanTaskHandler", new TaskClientHandler());
		NioSocketConnector connector = new NioSocketConnector();
		SocketAddress address = new InetSocketAddress(ipAddress, port);
		client.connect(connector, address);
		
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
		taskData.setStatus(Status.Created);
		task.setTaskData(taskData);
		String actorId = (String) workItem.getParameter("ActorId");
		if (actorId != null) {
			PeopleAssignments assignments = new PeopleAssignments();
			List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
			User user = new User();
			user.setId(new Long(actorId));
			potentialOwners.add(user);
			assignments.setPotentialOwners(potentialOwners);
			task.setPeopleAssignments(assignments);
			//taskData.setActualOwner(user);
			//taskData.setCreatedBy(user);
			//taskData.setCreatedOn(new Date());
			//taskData.setActivationTime(new Date());
		}
		client.addTask(task, new BlockingAddTaskResponseHandler());
		client.disconnect();
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO
	}
	
	private class BlockingAddTaskResponseHandler implements AddTaskResponseHandler {
        private volatile long taskId;
        private volatile boolean wait = true;

        public synchronized void execute(long taskId) {
            this.taskId = taskId;
            wait = false;
            notifyAll();                
        }
        
        public synchronized long getTaskId() {
            if ( wait ) {                  
                try {
                    wait( 3000 );
                } catch ( InterruptedException e ) {
                    // swallow as this is just a notifiation
                }
            }
            
            if ( wait ) {
                throw new RuntimeException("Timeout : unable to retrieve Task Id" );
            }
            
            return taskId;
        }       
    }

}
