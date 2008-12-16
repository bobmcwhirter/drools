package org.drools.jpdl.instance.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.drools.jpdl.core.node.TaskNode;
import org.drools.process.core.context.swimlane.SwimlaneContext;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.swimlane.SwimlaneContextInstance;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.WorkItem;
import org.jbpm.graph.def.Event;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.taskmgmt.def.Task;

public class TaskNodeInstance extends JpdlNodeInstance {

	private static final long serialVersionUID = 1L;
	
    private List<WorkItemImpl> workItems = new ArrayList<WorkItemImpl>();
    
	public TaskNode getTaskNode() {
		return (TaskNode) getNode();
	}

	public void execute(NodeInstance from, String type) {
		Set<Task> tasks = getTaskNode().getTasks();
		if ((getTaskNode().isCreateTasks()) && (tasks != null)) {
			addEventListeners();
			for (Task task: tasks) {
				if (evaluateTaskCondition(task.getCondition())) {
					WorkItemImpl workItem = (WorkItemImpl) TaskUtils.createWorkItem(task, this);
					workItems.add(workItem);
			        getProcessInstance().getWorkingMemory()
			        	.getWorkItemManager().internalExecuteWorkItem(workItem);
				}
			}
		}
		boolean continueExecution = false;
		switch (getTaskNode().getSignal()) {
			case org.jbpm.graph.node.TaskNode.SIGNAL_UNSYNCHRONIZED:
				continueExecution = true;
				break;
			case org.jbpm.graph.node.TaskNode.SIGNAL_FIRST_WAIT:
			case org.jbpm.graph.node.TaskNode.SIGNAL_LAST_WAIT:
			case org.jbpm.graph.node.TaskNode.SIGNAL_NEVER:
				continueExecution = false;
				break;
			case org.jbpm.graph.node.TaskNode.SIGNAL_FIRST:
			case org.jbpm.graph.node.TaskNode.SIGNAL_LAST:
				continueExecution = !hasSignallingWorkItems();
		}
		if (continueExecution) {
			leave();
		}
	}

	private boolean evaluateTaskCondition(String condition) {
		if (condition == null) {
			return true;
		}
		Object result = JbpmExpressionEvaluator.evaluate(
			condition, new JpdlExecutionContext());
		if (Boolean.TRUE.equals(result)) {
			return true;
		}
		return false;
	}
	
	private boolean hasSignallingWorkItems() {
	    for (WorkItem workItem: workItems) {
	        if ((Boolean) workItem.getParameter("signalling") == true) {
	            return true;
	        }
	    }
	    return false; 
	}

    private boolean hasBlockingWorkItems() {
        for (WorkItem workItem: workItems) {
            if ((Boolean) workItem.getParameter("blocking") == true) {
                return true;
            }
        }
        return false; 
    }

    public void addEventListeners() {
    	((ProcessInstance) getProcessInstance()).addEventListener("workItemCompleted", this, false);
    	((ProcessInstance) getProcessInstance()).addEventListener("workItemAborted", this, false);
    }
    
    public void removeEventListeners() {
        ((ProcessInstance) getProcessInstance()).removeEventListener("workItemCompleted", this, false);
        ((ProcessInstance) getProcessInstance()).removeEventListener("workItemAborted", this, false);
    }

    public void signalEvent(String type, Object event) {
    	if ("workItemCompleted".equals(type)) {
    		workItemCompleted((WorkItem) event);
    	} else if ("workItemAborted".equals(type)) {
    		workItemAborted((WorkItem) event);
    	} else {
    		super.signalEvent(type, event);
    	}
    }
    
    public void workItemAborted(WorkItem workItem) {
        if (workItems.remove(workItem)) {
            if (!hasBlockingWorkItems()) {
                removeEventListeners();
            	leave();
            }
        }
    }

    public void workItemCompleted(WorkItem workItem) {
    	if (workItems.remove(workItem)) {
    		String taskName = (String) workItem.getParameter("name");
    		Set<Task> tasks = getTaskNode().getTasks();
			for (Task task: tasks) {
				if (taskName.equals(task.getName())) {
		    		TaskUtils.restoreVariables((WorkItemImpl) workItem, task, this);
		    		if (task.getSwimlane() != null) {
		    		    String swimlaneName = task.getSwimlane().getName();
		    		    SwimlaneContextInstance swimlaneContextInstance = (SwimlaneContextInstance)
                            resolveContextInstance(SwimlaneContext.SWIMLANE_SCOPE, swimlaneName);
		    		    if (swimlaneContextInstance.getActorId(swimlaneName) == null) {
		    		        String actorId = (String) workItem.getResult("ActorId");
		    		        if (actorId != null) {
		    		            swimlaneContextInstance.setActorId(swimlaneName, 
	    		                    (String) workItem.getResult("ActorId"));
		    		        }
		    		    }
		    		}
	                Event event = task.getEvent(Event.EVENTTYPE_TASK_END);
	                if (event != null) {
	                	JpdlExecutionContext context = new JpdlExecutionContext();
	                	context.setTask(task);
	                    executeActions(event.getActions(), context);
	                }
		            break;
				}
    		}
            if (!hasBlockingWorkItems()) {
                removeEventListeners();
                String result = (String) workItem.getResult("Result");
                if (result != null) {
                    leave(result);
                } else {
                    leave();
                }
            }
        }
    }
    
    public WorkItem findWorkItem(Task task) {
        for (WorkItem workItem: workItems) {
            if (task.getName().equals(workItem.getName())) {
                return workItem;
            }
        }
        return null;
    }

	public void leave(String type) {
		if (hasBlockingWorkItems()) {
			throw new IllegalStateException("task-node '"
				+ getNode().getName() + "' still has blocking tasks");
		}
		if (getTaskNode().isEndTasks()) {
			for (WorkItem workItem: workItems) {
				getProcessInstance().getWorkingMemory().getWorkItemManager()
					.internalAbortWorkItem(workItem.getId());
			}
		}
		super.leave(type);
	}
	
}
