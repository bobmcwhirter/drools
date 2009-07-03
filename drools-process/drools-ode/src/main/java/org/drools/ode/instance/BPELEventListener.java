package org.drools.ode.instance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.ode.bpel.evt.ActivityExecEndEvent;
import org.apache.ode.bpel.evt.ActivityExecStartEvent;
import org.apache.ode.bpel.evt.BpelEvent;
import org.apache.ode.bpel.evt.ProcessCompletionEvent;
import org.apache.ode.bpel.evt.ProcessInstanceStartedEvent;
import org.apache.ode.bpel.iapi.BpelEventListener;
import org.drools.WorkingMemory;
import org.drools.common.EventSupport;
import org.drools.common.InternalWorkingMemory;
import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.drools.ode.core.BPELNode;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.workflow.core.impl.NodeContainerImpl;
import org.drools.workflow.instance.NodeInstanceContainer;

public class BPELEventListener implements BpelEventListener {

	private WorkingMemory workingMemory;
	private Map<Long, Long> processInstanceIdMapping = new HashMap<Long, Long>();
	
	public BPELEventListener(WorkingMemory workingMemory) {
		this.workingMemory = workingMemory;
	}
	
	public void onEvent(BpelEvent event) {
		try {
			if (event instanceof ProcessInstanceStartedEvent) {
				ProcessInstanceStartedEvent startEvent = (ProcessInstanceStartedEvent) event;
				BPELProcessInstance processInstance = new BPELProcessInstance();
				processInstance.setODEProcessInstanceIdODE(startEvent.getProcessInstanceId());
				processInstance.setProcessId(startEvent.getProcessId().toString());
				processInstance.internalSetState(ProcessInstance.STATE_ACTIVE);
				processInstance.setWorkingMemory((InternalWorkingMemory) workingMemory);
				((InternalWorkingMemory) workingMemory).getProcessInstanceManager().addProcessInstance(processInstance);
				processInstanceIdMapping.put(startEvent.getProcessInstanceId(), processInstance.getId());
				((EventSupport) workingMemory).getRuleFlowEventSupport().fireBeforeRuleFlowProcessStarted(processInstance, null);
				((EventSupport) workingMemory).getRuleFlowEventSupport().fireAfterRuleFlowProcessStarted(processInstance, null);
			} else if (event instanceof ProcessCompletionEvent) {
				ProcessCompletionEvent completionEvent = (ProcessCompletionEvent) event;
				BPELProcessInstance processInstance = findProcessInstance(completionEvent.getProcessInstanceId());
				processInstance.setState(ProcessInstance.STATE_COMPLETED);
			} else if (event instanceof ActivityExecStartEvent) {
				ActivityExecStartEvent execEvent = (ActivityExecStartEvent) event;
				if (execEvent.getActivityName() != null) {
    				BPELProcessInstance processInstance = findProcessInstance(execEvent.getProcessInstanceId());
    				BPELNodeInstance nodeInstance = createNodeInstance(
    					processInstance, execEvent.getActivityId(),
    					execEvent.getActivityDeclarationId(),
    					execEvent.getActivityName());
    				((EventSupport) workingMemory).getRuleFlowEventSupport().fireBeforeRuleFlowNodeTriggered(nodeInstance, null);
    				((EventSupport) workingMemory).getRuleFlowEventSupport().fireAfterRuleFlowNodeTriggered(nodeInstance, null);
				}
			} else if (event instanceof ActivityExecEndEvent) {
				ActivityExecEndEvent execEvent = (ActivityExecEndEvent) event;
                if (execEvent.getActivityName() != null) {
    				BPELProcessInstance processInstance = findProcessInstance(execEvent.getProcessInstanceId());
    				BPELNodeInstance nodeInstance = createNodeInstance(
    					processInstance, execEvent.getActivityId(),
    					execEvent.getActivityDeclarationId(),
    					execEvent.getActivityName());
    				((EventSupport) workingMemory).getRuleFlowEventSupport().fireBeforeRuleFlowNodeLeft(nodeInstance, null);
    				((EventSupport) workingMemory).getRuleFlowEventSupport().fireAfterRuleFlowNodeLeft(nodeInstance, null);
                }
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private BPELProcessInstance findProcessInstance(long id) {
		Long processInstanceId = processInstanceIdMapping.get(id);
		return (BPELProcessInstance) ((InternalWorkingMemory) workingMemory)
			.getProcessInstanceManager().getProcessInstance(processInstanceId);
	}
	
	private BPELNodeInstance createNodeInstance(BPELProcessInstance processInstance, long id, long nodeId, final String nodeName) {
		BPELNodeInstance result = new BPELNodeInstance();
		result.setId(id);
		result.setNodeId(nodeId);
		result.setProcessInstance(processInstance);
		result.setNodeInstanceContainer(new NodeInstanceContainer() {
			public NodeContainer getNodeContainer() {
				return new NodeContainerImpl() {
					private static final long serialVersionUID = 4L;
					public Node internalGetNode(long nodeId) {
						BPELNode node = new BPELNode();
						node.setId(nodeId);
						node.setName(nodeName);
						return node;
					}
				};
			}
			public Collection<NodeInstance> getNodeInstances() {
				return null;
			}
			public void addNodeInstance(org.drools.workflow.instance.NodeInstance nodeInstance) {
			}
			public org.drools.workflow.instance.NodeInstance getFirstNodeInstance(long nodeId) {
				return null;
			}
			public org.drools.workflow.instance.NodeInstance getNodeInstance(Node node) {
				return null;
			}
			public Collection<org.drools.workflow.instance.NodeInstance> getNodeInstances(boolean recursive) {
				return null;
			}
			public void removeNodeInstance(org.drools.workflow.instance.NodeInstance nodeInstance) {
			}
			public void nodeInstanceCompleted(org.drools.workflow.instance.NodeInstance nodeInstance, String outType) {
			}
		});
		return result;
	}

	public void shutdown() {
	}

	public void startup(Properties p) {
	}

}
