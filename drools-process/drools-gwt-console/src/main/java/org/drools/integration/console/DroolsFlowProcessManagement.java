package org.drools.integration.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.definition.process.Process;
import org.drools.process.audit.ProcessInstanceLog;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef.RESULT;
import org.jboss.bpm.console.client.model.ProcessInstanceRef.STATE;
import org.jboss.bpm.console.server.integration.ProcessManagement;

public class DroolsFlowProcessManagement implements ProcessManagement {

	private DroolsFlowCommandDelegate delegate;
	
    public DroolsFlowProcessManagement() {
        delegate = new DroolsFlowCommandDelegate();
    }
    
	public List<ProcessDefinitionRef> getProcessDefinitions() {
		List<Process> processes = delegate.getProcesses();
		List<ProcessDefinitionRef> result = new ArrayList<ProcessDefinitionRef>();
		for (Process process: processes) {
			result.add(DroolsFlowTransform.processDefinition(process));
		}
		return result;
	}

	public ProcessDefinitionRef getProcessDefinition(String definitionId) {
		Process process = delegate.getProcess(definitionId);
		return DroolsFlowTransform.processDefinition(process);
	}

	public List<ProcessDefinitionRef> removeProcessDefinition(String definitionId) {
		delegate.removeProcess(definitionId);
	    return getProcessDefinitions();
	}

	public ProcessInstanceRef getProcessInstance(String instanceId) {
		ProcessInstanceLog processInstance = delegate.getProcessInstanceLog(instanceId);
		return DroolsFlowTransform.processInstance(processInstance);
	}

	public List<ProcessInstanceRef> getProcessInstances(String definitionId) {
		// TODO: query for active process instances only
		List<ProcessInstanceLog> processInstances = delegate.getProcessInstanceLogsByProcessId(definitionId);
		List<ProcessInstanceRef> result = new ArrayList<ProcessInstanceRef>();
		for (ProcessInstanceLog processInstance: processInstances) {
			if (processInstance.getEnd() == null) {
				result.add(DroolsFlowTransform.processInstance(processInstance));
			}
		}
		return result;
	}

	public ProcessInstanceRef newInstance(String definitionId) {
		return newInstance(definitionId, null);
	}
	
	public ProcessInstanceRef newInstance(String definitionId, Map<String, Object> processVars) {
		ProcessInstanceLog processInstance = delegate.startProcess(definitionId, processVars);
		return DroolsFlowTransform.processInstance(processInstance);
	}

	public void setProcessState(String instanceId, STATE nextState) {
		if (nextState == STATE.ENDED) {
			delegate.abortProcessInstance(instanceId);
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public Map<String, Object> getInstanceData(String instanceId) {
		return delegate.getProcessInstanceVariables(instanceId);
	}

	public void setInstanceData(String instanceId, Map<String, Object> data) {
		delegate.setProcessInstanceVariables(instanceId, data);
	}

	public void signalExecution(String executionId, String signal) {
		delegate.signalExecution(executionId, signal);
	}

	public void deleteInstance(String instanceId) {
		delegate.abortProcessInstance(instanceId);
	}

	public void endInstance(String instanceId, RESULT result) {
		delegate.abortProcessInstance(instanceId);
	}

}