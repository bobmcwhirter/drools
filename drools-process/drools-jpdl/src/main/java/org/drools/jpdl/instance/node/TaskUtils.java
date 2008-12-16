package org.drools.jpdl.instance.node;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.drools.jpdl.instance.node.JpdlNodeInstance.JpdlExecutionContext;
import org.drools.process.core.context.swimlane.SwimlaneContext;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.context.swimlane.SwimlaneContextInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.process.instance.impl.WorkItemImpl;
import org.jbpm.JbpmException;
import org.jbpm.calendar.BusinessCalendar;
import org.jbpm.calendar.Duration;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Event;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;

public final class TaskUtils {
	
	private TaskUtils() {
	}
	
	public static WorkItem createWorkItem(Task task, JpdlNodeInstance nodeInstance) {
		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setName("JpdlTask");
		workItem.setProcessInstanceId(nodeInstance.getProcessInstance().getId());
		workItem.setParameter("name", task.getName());
		String description = task.getDescription();
		workItem.setParameter("signalling", task.isSignalling());
		workItem.setParameter("blocking", task.isBlocking());
		if ((description != null) && (description.indexOf("#{") != -1)) {
			Object result = JbpmExpressionEvaluator.evaluate(
				description, nodeInstance.new JpdlExecutionContext());
			if (result != null) {
				description = result.toString();
			}
		}
		workItem.setParameter("Description", description);
		initializeVariables(workItem, task, nodeInstance);
		if (task.getDueDate() != null) {
		    BusinessCalendar businessCalendar = new BusinessCalendar();
		    workItem.setParameter("dueDate", 
	            businessCalendar.add(new Date(), new Duration(task.getDueDate())));
	    }
		if (task.getSwimlane() != null) {
		    String swimlaneName = task.getSwimlane().getName();
		    SwimlaneContextInstance swimlaneContextInstance = (SwimlaneContextInstance)
		        nodeInstance.resolveContextInstance(SwimlaneContext.SWIMLANE_SCOPE, swimlaneName);
		    String actorId = swimlaneContextInstance.getActorId(swimlaneName);
		    if (actorId == null) {
		        actorId = assignTask(task, nodeInstance);
		    }
		    workItem.setParameter("ActorId", actorId);
		}
		Event event = task.getEvent(Event.EVENTTYPE_TASK_CREATE);
        if (event != null) {
            // TODO this doesn't take event handlers of task itself
            // into account
        	JpdlExecutionContext context = nodeInstance.new JpdlExecutionContext();
        	context.setTask(task);
        	nodeInstance.executeActions(event.getActions(), context);
        }
		return workItem;
	}
	
	private static void initializeVariables(WorkItemImpl workItem, Task task, JpdlNodeInstance nodeInstance) {
		TaskController taskController = task.getTaskController();
		if (taskController != null) {
			Delegation taskControllerDelegation = taskController.getTaskControllerDelegation();
		    if (taskControllerDelegation != null) {
		    	// TODO: delegation (API mismatch!)
		    } else {
		    	List<VariableAccess> variableAccesses = taskController.getVariableAccesses();
		    	if (variableAccesses != null) {
		    		for (VariableAccess variableAccess: variableAccesses) {
		    			String mappedName = variableAccess.getMappedName();
		    			if (variableAccess.isReadable()) {
		    				String variableName = variableAccess.getVariableName();
		    				VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
		    					nodeInstance.resolveContextInstance(VariableScope.VARIABLE_SCOPE, variableName);
		    				Object value = variableScopeInstance.getVariable(variableName);
		    				workItem.setParameter(mappedName, value);
		    			}
			        }
		    	}
		    }
		}
	}
	
	private static String assignTask(Task task, JpdlNodeInstance nodeInstance) {
	    Event event = task.getEvent(Event.EVENTTYPE_TASK_ASSIGN);
        if (event != null) {
        	JpdlExecutionContext context = nodeInstance.new JpdlExecutionContext();
        	context.setTask(task);
            nodeInstance.executeActions(event.getActions(), context);
        }
	    if (task.getActorIdExpression() != null) {
            return resolveActor(task.getActorIdExpression(), nodeInstance);
	    } else if (task.getSwimlane().getActorIdExpression() != null) {
	        return resolveActor(task.getSwimlane().getActorIdExpression(), nodeInstance);
	    }
	    // TODO support other assignment types
	    return null;
	}
	
	private static String resolveActor(String expression, JpdlNodeInstance nodeInstance) {
	    Object result = JbpmExpressionEvaluator.evaluate(expression, nodeInstance.new JpdlExecutionContext());
        if (result == null) {
            throw new JbpmException("actor-id expression '" + expression + "' returned null");
        }
        if (result instanceof String) {
            return (String) result;
        } else {
            throw new JbpmException(
                "actor-id expression '" + expression + 
                "' didn't resolve to a java.lang.String: '" + result + 
                "' (" + result.getClass().getName() + ")");
        }
	}
	
	public static void restoreVariables(WorkItemImpl workItem, Task task, JpdlNodeInstance nodeInstance) {
		TaskController taskController = task.getTaskController();
		if (taskController != null) {
			Delegation taskControllerDelegation = taskController.getTaskControllerDelegation();
			if (taskControllerDelegation != null) {
				// TODO: delegation (API mismatch!)
		    } else {
		    	List<VariableAccess> variableAccesses = taskController.getVariableAccesses();
		    	if (variableAccesses != null) {
			        String missingTaskVariables = null;
		    		for (VariableAccess variableAccess: variableAccesses) {
		    			String mappedName = variableAccess.getMappedName();
		    			Object value = workItem.getParameter(mappedName);
		    			if (variableAccess.isRequired() && (value != null)) {
		    				if (missingTaskVariables == null) {
		    					missingTaskVariables = mappedName;
		    				} else {
		    					missingTaskVariables += ", "+mappedName;
		    				}
		    			}
		    		}
			        if (missingTaskVariables != null) {
			        	throw new IllegalArgumentException(
		        			"missing task variables: " + missingTaskVariables);
			        }

			        for (VariableAccess variableAccess: variableAccesses) {
			        	String mappedName = variableAccess.getMappedName();
			        	String variableName = variableAccess.getVariableName();
			        	if (variableAccess.isWritable()) {
			        		Object value = workItem.getParameter(mappedName);
			        		if (value != null) {
			        			VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
		    						nodeInstance.resolveContextInstance(VariableScope.VARIABLE_SCOPE, variableName);
			        			variableScopeInstance.setVariable(variableName, value);
			        		}
			        	}
			        }
		    	}
		    }
		} else {
			// the default behaviour is that all task-local variables are flushed to the process
			VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
				nodeInstance.getProcessInstance().getContextInstance(VariableScope.VARIABLE_SCOPE);
			for (Map.Entry<String, Object> entry: workItem.getResults().entrySet()) {
				variableScopeInstance.setVariable(entry.getKey(), entry.getValue());
			}
		}
	}

}
