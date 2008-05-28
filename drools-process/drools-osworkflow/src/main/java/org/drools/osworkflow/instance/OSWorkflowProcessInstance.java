package org.drools.osworkflow.instance;

import java.util.Map;

import org.drools.osworkflow.core.OSWorkflowProcess;
import org.drools.osworkflow.core.node.StepNode;
import org.drools.osworkflow.instance.node.StepNodeInstance;
import org.drools.workflow.instance.impl.WorkflowProcessInstanceImpl;

import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ResultDescriptor;

public class OSWorkflowProcessInstance extends WorkflowProcessInstanceImpl {

    private static final long serialVersionUID = 1L;

    public OSWorkflowProcess getOSWorkflowProcess() {
        return (OSWorkflowProcess) getProcess();
    }
    
    protected void internalStart() {
        // Do nothing
    }

    public void doInitialAction(int actionId, Map inputs) {
        ActionDescriptor initialAction = getOSWorkflowProcess().getInitialAction(actionId);
        if (initialAction == null) {
            throw new IllegalArgumentException(
                "Unknown initial action id " + actionId);
        }
        ResultDescriptor result = initialAction.getUnconditionalResult();
        int step = result.getStep();
        String status = result.getStatus();
        StepNode stepNode = (StepNode) getNodeContainer().getNode(step);
        if (stepNode == null) {
            throw new IllegalArgumentException(
                "Unknown step id " + step);
        }
        StepNodeInstance stepNodeInstance = (StepNodeInstance) getNodeInstance(stepNode);
        stepNodeInstance.trigger(null, status);
    }
}
