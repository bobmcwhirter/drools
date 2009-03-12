package org.drools.osworkflow.core.command;

import java.util.HashMap;
import java.util.Map;

import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.osworkflow.instance.OSWorkflowProcessInstance;
import org.drools.process.command.Command;
import org.drools.process.instance.ProcessInstance;
import org.drools.reteoo.ReteooWorkingMemory;

/*Author: salaboy, mfossati */

public class DoActionCommand implements Command {
	
	private int actionId;
	private long processInstanceId;
	private Map<String, Object> results = new HashMap<String, Object>();
	
	public void setActionId(int actionId) {
		this.actionId = actionId;
	}

	public int getActionId() {
		return actionId;
	}

	public Map<String, Object> getResults() {
		return results;
	}

	public void setResults(Map<String, Object> results) {
		this.results = results;
	}
	
	public void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public long getProcessInstanceId() {
		return processInstanceId;
	}
	
	public Object execute(ReteooWorkingMemory workingMemory) {
		ProcessInstance processInstance = ( ProcessInstance ) workingMemory.getProcessInstance(getProcessInstanceId());
		if (processInstance != null) {
			((OSWorkflowProcessInstance)processInstance).doAction(actionId, new HashMap());
			
		}
		return null;
	}

	public Object execute(StatefulSession session) {
		return this.execute((ReteooWorkingMemory)session);
	}

	

	

}