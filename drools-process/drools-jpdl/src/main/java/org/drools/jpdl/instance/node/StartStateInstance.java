package org.drools.jpdl.instance.node;

import org.drools.jpdl.core.node.StartState;
import org.drools.process.core.context.swimlane.SwimlaneContext;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.context.swimlane.SwimlaneContextInstance;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.process.NodeInstance;
import org.jbpm.graph.def.Event;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.taskmgmt.def.Task;

public class StartStateInstance extends StateInstance {

    private static final long serialVersionUID = 1L;
    
    private long workItemId = -1;
    private transient WorkItem workItem;
    
    public StartState getStartState() {
    	return (StartState) getJpdlNode();
    }
    
    public void execute(NodeInstance from, String type) {
		super.execute(from, type);
		Task task = getStartState().getTask(); 
		if (task != null) {
			if (evaluateTaskCondition(task.getCondition())) {
				addWorkItemListener();
				workItem = (WorkItemImpl) TaskUtils.createWorkItem(task, this);
		        getProcessInstance().getWorkingMemory()
		        	.getWorkItemManager().internalExecuteWorkItem(workItem);
			}
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
	
    public WorkItem getWorkItem() {
    	if (workItem == null && workItemId >= 0) {
    		workItem = ((WorkItemManager) ((ProcessInstance) getProcessInstance())
				.getWorkingMemory().getWorkItemManager()).getWorkItem(workItemId);
    	}
        return workItem;
    }
    
    public void addEventListeners() {
        super.addEventListeners();
        addWorkItemListener();
    }
    
    private void addWorkItemListener() {
    	getProcessInstance().addEventListener("workItemCompleted", this, false);
    	getProcessInstance().addEventListener("workItemAborted", this, false);
    }
    
    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener("workItemCompleted", this, false);
        getProcessInstance().removeEventListener("workItemAborted", this, false);
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
        if ( getWorkItem().getId() == workItem.getId() ) {
            removeEventListeners();
            leave();
        }
    }

    public void workItemCompleted(WorkItem workItem) {
        if ( getWorkItem().getId() == workItem.getId() ) {
            removeEventListeners();
            Task task = getStartState().getTask();
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
            String result = (String) workItem.getResult("Result");
            if (result != null) {
                leave(result);
            } else {
                leave();
            }
        }
    }
    
}
