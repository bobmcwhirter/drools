package org.drools.jpdl.core.node;

import org.drools.workflow.core.Connection;
import org.jbpm.taskmgmt.def.Task;

public class StartState extends State {

	private static final long serialVersionUID = 1L;
	
	private Task task;

    public void setTask(Task task) {
    	this.task = task;
    }
    
    public Task getTask() {
    	return task;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
            "A start state does not have an incoming connection!");
    }

    public void validateRemoveIncomingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
            "A start state does not have an incoming connection!");
    }
    
}
