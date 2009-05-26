package org.drools.process.workitem.wsht;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.SystemEventListenerFactory;
import org.drools.eventmessaging.EventKey;
import org.drools.eventmessaging.EventResponseHandler;
import org.drools.eventmessaging.Payload;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.task.AccessType;
import org.drools.task.Content;
import org.drools.task.Group;
import org.drools.task.I18NText;
import org.drools.task.OrganizationalEntity;
import org.drools.task.PeopleAssignments;
import org.drools.task.SubTasksStrategy;
import org.drools.task.SubTasksStrategyFactory;
import org.drools.task.Task;
import org.drools.task.TaskData;
import org.drools.task.User;
import org.drools.task.event.TaskCompletedEvent;
import org.drools.task.event.TaskEvent;
import org.drools.task.event.TaskEventKey;
import org.drools.task.event.TaskFailedEvent;
import org.drools.task.event.TaskSkippedEvent;
import org.drools.task.service.ContentData;
import org.drools.task.service.MinaTaskClient;
import org.drools.task.service.TaskClientHandler;
import org.drools.task.service.TaskClientHandler.AddTaskResponseHandler;
import org.drools.task.service.TaskClientHandler.GetContentResponseHandler;
import org.drools.task.service.TaskClientHandler.GetTaskResponseHandler;
import org.drools.task.service.responsehandlers.AbstractBaseResponseHandler;

public class CommandBasedWSHumanTaskHandler implements WorkItemHandler {

	private String ipAddress = "127.0.0.1";
	private int port = 9123;
	private MinaTaskClient client;
	private KnowledgeRuntime session;
	private Map<Long, Long> idMapping = new HashMap<Long, Long>();
	
	public CommandBasedWSHumanTaskHandler(KnowledgeRuntime session) {
		this.session = session;
	}

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

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		connect();
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
		String priorityString = (String) workItem.getParameter("Priority");
		int priority = 0;
		if (priorityString != null) {
			try {
				priority = new Integer(priorityString);
			} catch (NumberFormatException e) {
				// do nothing
			}
		}
		task.setPriority(priority);
		
		TaskData taskData = new TaskData();
		taskData.setWorkItemId(workItem.getId());
		taskData.setSkipable(!"false".equals(workItem.getParameter("Skippable")));
        //Sub Task Data
        Long parentId = (Long) workItem.getParameter("ParentId");
        if(parentId != null){
            taskData.setParentId(parentId);
        }

        String subTaskStrategiesCommaSeparated = (String) workItem.getParameter("SubTaskStrategies");
        if(subTaskStrategiesCommaSeparated!= null && !subTaskStrategiesCommaSeparated.equals("")){
            String[] subTaskStrategies =  subTaskStrategiesCommaSeparated.split(",");
            List<SubTasksStrategy> strategies = new ArrayList<SubTasksStrategy>();
            for(String subTaskStrategyString : subTaskStrategies){
                SubTasksStrategy subTaskStrategy = SubTasksStrategyFactory.newStrategy(subTaskStrategyString);
                strategies.add(subTaskStrategy);
            }
            task.setSubTaskStrategies(strategies);
        }

        PeopleAssignments assignments = new PeopleAssignments();
		List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();

        String actorId = (String) workItem.getParameter("ActorId");
		if (actorId != null) {
			
			String[] actorIds = actorId.split(",");
			for (String id: actorIds) {
				User user = new User();
				user.setId(id.trim());
				potentialOwners.add(user);
			}
            //Set the first user as creator ID??? hmmm might be wrong
            if (potentialOwners.size() > 0){
                taskData.setCreatedBy((User)potentialOwners.get(0));
            }
        }
        String groupId = (String) workItem.getParameter("GroupId");
		if (groupId != null) {
			
			String[] groupIds = groupId.split(",");
			for (String id: groupIds) {

				potentialOwners.add(new Group(id));
			}
			
		}

        assignments.setPotentialOwners(potentialOwners);
		List<OrganizationalEntity> businessAdministrators = new ArrayList<OrganizationalEntity>();
		businessAdministrators.add(new User("Administrator"));
		assignments.setBusinessAdministrators(businessAdministrators);
		task.setPeopleAssignments(assignments);
        
		task.setTaskData(taskData);

		ContentData content = null;
		Object contentObject = workItem.getParameter("Content");
		if (contentObject != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out;
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(contentObject);
				out.close();
				content = new ContentData();
				content.setContent(bos.toByteArray());
				content.setAccessType(AccessType.Inline);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		TaskWorkItemAddTaskResponseHandler taskResponseHandler =
			new TaskWorkItemAddTaskResponseHandler(this.client, workItem.getId());
		client.addTask(task, content, taskResponseHandler);
	}
	
	public void dispose() {
		if (client != null) {
			client.disconnect();
		}
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		Long taskId = idMapping.get(workItem.getId());
		if (taskId != null) {
			synchronized (idMapping) {
				idMapping.remove(taskId);
			}
			client.skip(taskId, "Administrator", null);
		}
	}
	
    public class TaskWorkItemAddTaskResponseHandler extends AbstractBaseResponseHandler implements AddTaskResponseHandler {

    	private long workItemId;
        
        public TaskWorkItemAddTaskResponseHandler(MinaTaskClient client, long workItemId) {
            this.workItemId = workItemId;
        }
        
        public void execute(long taskId) {
            synchronized ( idMapping ) {
                idMapping.put(workItemId, taskId);           
            }
//            System.out.println("Created task " + taskId + " for work item " + workItemId);
            
            EventKey key = new TaskEventKey(TaskCompletedEvent.class, taskId );           
            TaskCompletedHandler eventResponseHandler =
            	new TaskCompletedHandler(workItemId, taskId); 
            client.registerForEvent( key, true, eventResponseHandler );
            key = new TaskEventKey(TaskFailedEvent.class, taskId );           
            client.registerForEvent( key, true, eventResponseHandler );
            key = new TaskEventKey(TaskSkippedEvent.class, taskId );           
            client.registerForEvent( key, true, eventResponseHandler );
        }
    }
    
    private class TaskCompletedHandler extends AbstractBaseResponseHandler implements EventResponseHandler {
        private long workItemId;
        private long taskId;
        
        public TaskCompletedHandler(long workItemId, long taskId) {
            this.workItemId = workItemId;
            this.taskId = taskId;
        }

        public void execute(Payload payload) {
            TaskEvent event = ( TaskEvent ) payload.get();
        	if ( event.getTaskId() != taskId ) {
                // defensive check that should never happen, just here for testing                
                setError(new IllegalStateException("Expected task id and arrived task id do not march"));
                return;
            }
        	if (event instanceof TaskCompletedEvent) {
            	GetTaskResponseHandler getTaskResponseHandler = new GetCompletedTaskResponseHandler();
            	client.getTask(taskId, getTaskResponseHandler);   
        	} else {
            	session.getWorkItemManager().abortWorkItem(workItemId);
        	}
        }
    }
    
    private class GetCompletedTaskResponseHandler extends AbstractBaseResponseHandler implements GetTaskResponseHandler {

		public void execute(Task task) {
			long workItemId = task.getTaskData().getWorkItemId();
			String userId = task.getTaskData().getActualOwner().getId();
			Map<String, Object> results = new HashMap<String, Object>();
			results.put("ActorId", userId);
			long contentId = task.getTaskData().getOutputContentId();
			if (contentId != -1) {
				GetContentResponseHandler getContentResponseHandler =
					new GetResultContentResponseHandler(task, results);
				client.getContent(contentId, getContentResponseHandler);
			} else {
				session.getWorkItemManager().completeWorkItem(workItemId, results);
			}
		}
    }
    
    private class GetResultContentResponseHandler extends AbstractBaseResponseHandler implements GetContentResponseHandler {

    	private Task task;
    	private Map<String, Object> results;

    	public GetResultContentResponseHandler(Task task, Map<String, Object> results) {
    		this.task = task;
    		this.results = results;
    	}
    	
		@SuppressWarnings("unchecked")
		public void execute(Content content) {
			ByteArrayInputStream bis = new ByteArrayInputStream(content.getContent());
			ObjectInputStream in;
			try {
				in = new ObjectInputStream(bis);
				Object result = in.readObject();
				in.close();
				results.put("Result", result);
				if (result instanceof Map) {
					Map<?, ?> map = (Map) result;
					for (Map.Entry<?, ?> entry: map.entrySet()) {
						if (entry.getKey() instanceof String) {
							results.put((String) entry.getKey(), entry.getValue());
						}
					}
				}
				session.getWorkItemManager().completeWorkItem(task.getTaskData().getWorkItemId(), results);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
    }
}
