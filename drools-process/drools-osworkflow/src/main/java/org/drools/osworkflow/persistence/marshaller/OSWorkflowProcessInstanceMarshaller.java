package org.drools.osworkflow.persistence.marshaller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.drools.marshalling.impl.AbstractProcessInstanceMarshaller;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.osworkflow.instance.OSWorkflowProcessInstance;
import org.drools.osworkflow.instance.node.StepNodeInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.workflow.instance.WorkflowProcessInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;
import org.drools.workflow.instance.impl.WorkflowProcessInstanceImpl;

public class OSWorkflowProcessInstanceMarshaller extends
		AbstractProcessInstanceMarshaller {

	public static OSWorkflowProcessInstanceMarshaller INSTANCE = new OSWorkflowProcessInstanceMarshaller();

	private OSWorkflowProcessInstanceMarshaller() {

	}

	public WorkflowProcessInstanceImpl createProcessInstance() {
		return new OSWorkflowProcessInstance();
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
