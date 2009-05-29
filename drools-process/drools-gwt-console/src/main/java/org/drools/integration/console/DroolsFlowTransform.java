package org.drools.integration.console;

import org.drools.definition.process.Process;
import org.drools.process.audit.ProcessInstanceLog;
import org.drools.task.I18NText;
import org.drools.task.Task;
import org.drools.task.query.TaskSummary;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jboss.bpm.console.client.model.TaskRef;
import org.jboss.bpm.console.client.model.TokenReference;

public class DroolsFlowTransform {
	
	public static ProcessDefinitionRef processDefinition(Process process) {
		long version = 0;
		try {
			version = new Long(process.getVersion());
		} catch (NumberFormatException e) {
			// Do nothing, keep version 0
		}
		ProcessDefinitionRef result = new ProcessDefinitionRef(
			process.getId(), process.getName(), version );
		result.setPackageName(process.getPackageName());
		return result;
	}
	
	public static ProcessInstanceRef processInstance(ProcessInstanceLog processInstance) {
		ProcessInstanceRef result = new ProcessInstanceRef(
			processInstance.getProcessInstanceId() + "",
			processInstance.getProcessId(),
			processInstance.getStart(),
			processInstance.getEnd(),
			false);
		TokenReference token = new TokenReference(
			processInstance.getProcessInstanceId() + "", null, "");
		result.setRootToken(token);
		return result;
	}
	
	public static TaskRef task(TaskSummary task) {
		return new TaskRef(
			task.getId(),
			// TODO: fixme
			"-1",
			"-1",
			task.getName(),
			task.getActualOwner() == null ? null : task.getActualOwner().getId(),
			false,
			false);
	}

	public static TaskRef task(Task task) {
		String name = "";
		for (I18NText text: task.getNames()) {
			if ("en-UK".equals(text.getLanguage())) {
				name = text.getText();
			}
		}
		return new TaskRef(
			task.getId(),
			// TODO: fixme
			"-1",
			"-1",
			name,
			task.getTaskData().getActualOwner() == null ? null : task.getTaskData().getActualOwner().getId(),
			false,
			false);
	}

}
