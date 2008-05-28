package org.drools.osworkflow;

import java.util.List;

import org.drools.process.core.Process;
import org.drools.ruleflow.core.RuleFlowProcess;

import com.opensymphony.workflow.loader.StepDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.spi.Step;

public class OSWorkflowParser {
	
	public static Process parseOSWorkflow(WorkflowDescriptor descriptor) {
		RuleFlowProcess process = new RuleFlowProcess();
		process.setName(descriptor.getName());
		process.setId(descriptor.getName());
		process.setPackageName("org.drools.osworkflow");
		List<StepDescriptor> steps = descriptor.getSteps();
		for (StepDescriptor step: steps) {
			
		}
		return process;
	}

}
