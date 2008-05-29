package org.drools.osworkflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.osworkflow.core.OSWorkflowProcess;
import org.drools.osworkflow.core.node.StepNode;
import org.drools.process.core.Process;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.Split;

import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.JoinDescriptor;
import com.opensymphony.workflow.loader.ResultDescriptor;
import com.opensymphony.workflow.loader.SplitDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

public class OSWorkflowParser {
    
    private OSWorkflowProcess process;
    private Map<Integer, StepNode> stepsMap;
    private Map<Integer, Split> splitsMap;
    private Map<Integer, Join> joinsMap;
	
	public Process parseOSWorkflow(WorkflowDescriptor descriptor) {
		process = new OSWorkflowProcess();
		process.setName(descriptor.getName());
		process.setId(descriptor.getName());
		process.setPackageName("org.drools.osworkflow");
		process.setInitialActions(descriptor.getInitialActions());

        stepsMap = new HashMap<Integer, StepNode>();
        int nodeCounter = 0;
		List<StepDescriptor> steps = descriptor.getSteps();
		for (StepDescriptor step: steps) {
			StepNode stepNode = new StepNode();
			stepNode.setId(++nodeCounter);
			stepNode.setName(step.getName());
			stepNode.setActions(step.getActions());
			process.addNode(stepNode);
			stepsMap.put(step.getId(), stepNode);
		}
		splitsMap = new HashMap<Integer, Split>();
        List<SplitDescriptor> splits = descriptor.getSplits();
        for (SplitDescriptor split: splits) {
            Split splitNode = new Split();
            splitNode.setId(++nodeCounter);
            splitNode.setName("split");
            splitNode.setType(Split.TYPE_AND);
            process.addNode(splitNode);
            splitsMap.put(split.getId(), splitNode);
            List<ResultDescriptor> results = split.getResults();
            for (ResultDescriptor result: results) {
                createConnection(splitNode, Node.CONNECTION_DEFAULT_TYPE, result);
            }
        }
        joinsMap = new HashMap<Integer, Join>();
        List<JoinDescriptor> joins = descriptor.getJoins();
        for (JoinDescriptor join: joins) {
            Join joinNode = new Join();
            joinNode.setId(++nodeCounter);
            joinNode.setName("join");
            // TODO conditions
            List<ConditionDescriptor> conditions = join.getConditions();
            joinNode.setType(Join.TYPE_AND);
            process.addNode(joinNode);
            joinsMap.put(join.getId(), joinNode);
            ResultDescriptor result = join.getResult();
            createConnection(joinNode, Node.CONNECTION_DEFAULT_TYPE, result);
        }
		
        steps = descriptor.getSteps();
        for (StepDescriptor step: steps) {
		    List<ActionDescriptor> actions = step.getActions();
		    for (ActionDescriptor action: actions) {
                ResultDescriptor result = action.getUnconditionalResult();
		        createConnection(stepsMap.get(step.getId()), action.getId() + "", result);
		    }
		}
		return process;
	}
	
	private void createConnection(Node node, String type, ResultDescriptor result) {
	    if (result.getSplit() != 0) {
	        Split split = splitsMap.get(result.getSplit());
	        new ConnectionImpl(node, type, split, Node.CONNECTION_DEFAULT_TYPE);
        } else if (result.getJoin() != 0) {
            Join join = joinsMap.get(result.getJoin());
            new ConnectionImpl(node, type, join, Node.CONNECTION_DEFAULT_TYPE);
        } else if (result.getStep() == -1 || result.getStep() == node.getId()) {
            // Do nothing, this is an internal connection
        } else {
            new ConnectionImpl(
                node,
                type,
                process.getNode(result.getStep()),
                result.getStatus()
            );
        }
	}

}
