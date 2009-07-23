package org.drools.osworkflow.core.command;

import java.util.HashMap;
import java.util.Map;

import org.drools.StatefulSession;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.osworkflow.instance.OSWorkflowProcessInstance;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

/*Author: salaboy, mfossati */

public class DoActionCommand implements GenericCommand {
	
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
	
	public Object execute(Context context) {
		StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        ProcessInstance processInstance = ksession.getProcessInstance(getProcessInstanceId());
		if (processInstance != null) {
			((OSWorkflowProcessInstance) processInstance).doAction(actionId, new HashMap());
			
		}
		return null;
	}

}