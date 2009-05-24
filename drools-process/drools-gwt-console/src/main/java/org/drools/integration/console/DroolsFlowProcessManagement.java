package org.drools.integration.console;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.drools.process.audit.ProcessInstanceLog;
import org.drools.definition.process.Process;
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
		List<ProcessInstanceLog> processInstances = delegate.getProcessInstanceLogsByProcessId(definitionId);
		List<ProcessInstanceRef> result = new ArrayList<ProcessInstanceRef>();
		for (ProcessInstanceLog processInstance: processInstances) {
			result.add(DroolsFlowTransform.processInstance(processInstance));
		}
		return result;
	}

	public ProcessInstanceRef newInstance(String instanceId) {
		ProcessInstanceLog processInstance = delegate.startProcess(instanceId);
		return DroolsFlowTransform.processInstance(processInstance);
	}

	public void setProcessState(String instanceId, STATE nextState) {
		if (nextState == STATE.ENDED) {
			delegate.abortProcessInstance(instanceId);
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public void signalExecution(String executionId, String signal) {
		delegate.signalExecution(executionId, signal);
	}

	public void deploy(String fileName, String contentType, InputStream deployment) {
		// Do nothing
	}

	public void deleteInstance(String instanceId) {
		delegate.abortProcessInstance(instanceId);
	}

	public void endInstance(String instanceId, RESULT result) {
		delegate.abortProcessInstance(instanceId);
	}

}