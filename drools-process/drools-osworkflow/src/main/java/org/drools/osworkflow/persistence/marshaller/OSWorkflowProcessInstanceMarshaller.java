package org.drools.osworkflow.persistence.marshaller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.marshalling.impl.AbstractProcessInstanceMarshaller;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.PersisterEnums;
import org.drools.osworkflow.instance.OSWorkflowProcessInstance;
import org.drools.osworkflow.instance.node.StepNodeInstance;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.workflow.instance.WorkflowProcessInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

public class OSWorkflowProcessInstanceMarshaller extends
		AbstractProcessInstanceMarshaller {

	public static OSWorkflowProcessInstanceMarshaller INSTANCE = new OSWorkflowProcessInstanceMarshaller();

	private OSWorkflowProcessInstanceMarshaller() {

	}

	@Override
	public ProcessInstance readProcessInstance(MarshallerReaderContext context)
			throws IOException {
		ObjectInputStream stream = context.stream;
		InternalRuleBase ruleBase = context.ruleBase;
		InternalWorkingMemory wm = context.wm;

		OSWorkflowProcessInstance processInstance = new OSWorkflowProcessInstance();
		processInstance.setId(stream.readLong());
		String processId = stream.readUTF();
		processInstance.setProcessId(processId);
		if (ruleBase != null) {
			processInstance.setProcess(ruleBase.getProcess(processId));
		}
		processInstance.setState(stream.readInt());
		long nodeInstanceCounter = stream.readLong();
		processInstance.setWorkingMemory(wm);

		int nbVariables = stream.readInt();
		if (nbVariables > 0) {
			VariableScopeInstance variableScopeInstance = (VariableScopeInstance) processInstance
					.getContextInstance(VariableScope.VARIABLE_SCOPE);
			for (int i = 0; i < nbVariables; i++) {
				String name = stream.readUTF();
				try {
					Object value = stream.readObject();
					variableScopeInstance.setVariable(name, value);
				} catch (ClassNotFoundException e) {
					throw new IllegalArgumentException(
							"Could not reload variable " + name);
				}
			}
		}

		while (stream.readShort() == PersisterEnums.NODE_INSTANCE) {
			readNodeInstance(context, processInstance, processInstance);
		}

		processInstance.internalSetNodeInstanceCounter(nodeInstanceCounter);
		if (wm != null) {
			processInstance.reconnect();
		}
		return processInstance;
	}

	protected NodeInstanceImpl readNodeInstanceContent(int nodeType,
			ObjectInputStream stream, MarshallerReaderContext context,
			WorkflowProcessInstance processInstance) throws IOException {
		NodeInstanceImpl nodeInstance = null;
		if (nodeType == OSWorkFlowPersisterEnums.STEP_NODE_INSTANCE) {
			nodeInstance = new StepNodeInstance();
			((StepNodeInstance) nodeInstance).setNodeId(stream.readLong());
			if (stream.readBoolean()) {
				((StepNodeInstance) nodeInstance).setStatus(stream.readUTF());
			}
			if (stream.readBoolean()) {
				((StepNodeInstance) nodeInstance).setOwner(stream.readUTF());
			}

			return nodeInstance;
		} else
			return super.readNodeInstanceContent(nodeType, stream, context,
					processInstance);
	}

	protected void writeNodeInstanceContent(ObjectOutputStream stream,
			NodeInstance nodeInstance, MarshallerWriteContext context)
			throws IOException {
		if (nodeInstance instanceof StepNodeInstance) {
			stream.writeShort(OSWorkFlowPersisterEnums.STEP_NODE_INSTANCE);
			stream.writeLong(((StepNodeInstance) nodeInstance).getNodeId());
			String status = ((StepNodeInstance) nodeInstance).getStatus();
			if (status == null || "".equals(status)) {
				stream.writeBoolean(false);
			} else {
				stream.writeBoolean(true);
				stream.writeUTF(((StepNodeInstance) nodeInstance).getStatus());
			}
			String owner = ((StepNodeInstance) nodeInstance).getOwner();
			if (owner == null || "".equals(owner)) {
				stream.writeBoolean(false);
			} else {
				stream.writeBoolean(true);
				stream.writeUTF(((StepNodeInstance) nodeInstance).getOwner());
			}

		} else
			super.writeNodeInstanceContent(stream, nodeInstance, context);
	}

}
