/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
