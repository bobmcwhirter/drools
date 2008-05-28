package org.drools.osworkflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.osworkflow.core.OSWorkflowProcess;
import org.drools.osworkflow.core.node.StepNode;
import org.drools.process.core.Process;
import org.drools.workflow.core.impl.ConnectionImpl;

import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ResultDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

public class OSWorkflowParser {
	
	public static Process parseOSWorkflow(WorkflowDescriptor descriptor) {
		OSWorkflowProcess process = new OSWorkflowProcess();
		process.setName(descriptor.getName());
		process.setId(descriptor.getName());
		process.setPackageName("org.drools.osworkflow");
		process.setInitialActions(descriptor.getInitialActions());

        Map<StepDescriptor, StepNode> stepsMap = new HashMap<StepDescriptor, StepNode>();
		List<StepDescriptor> steps = descriptor.getSteps();
		for (StepDescriptor step: steps) {
			StepNode stepNode = new StepNode();
			stepNode.setId(step.getId());
			stepNode.setName(step.getName());
			stepNode.setActions(step.getActions());
			process.addNode(stepNode);
			stepsMap.put(step, stepNode);
		}
		
		for (Map.Entry<StepDescriptor, StepNode> entry: stepsMap.entrySet()) {
		    StepDescriptor step = entry.getKey();
		    List<ActionDescriptor> actions = step.getActions();
		    for (ActionDescriptor action: actions) {
		        ResultDescriptor result = action.getUnconditionalResult();
		        if (result != null) {
		            new ConnectionImpl(entry.getValue(), action.getId() + "",
	                    process.getNode(result.getStep()), result.getStatus());
		        }
		    }
		}
		return process;
	}

}
