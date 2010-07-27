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

package org.drools.bpmn2.legacy.beta1;

import java.util.Map;

import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionRef;
import org.drools.workflow.core.node.Split;
import org.xml.sax.Attributes;

public class SplitHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
    	throw new IllegalArgumentException("Reading in should be handled by gateway handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Split.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		Split split = (Split) node;
		switch (split.getType()) {
			case Split.TYPE_AND:
				writeNode("parallelGateway", node, xmlDump, includeMeta);
				break;
			case Split.TYPE_XOR:
				writeNode("exclusiveGateway", node, xmlDump, includeMeta);
				for (Map.Entry<ConnectionRef, Constraint> entry: split.getConstraints().entrySet()) {
					if (entry.getValue().isDefault()) {
						xmlDump.append("default=\"" +
							XmlBPMNProcessDumper.getUniqueNodeId(split) + "-" +
							XmlBPMNProcessDumper.getUniqueNodeId(node.getNodeContainer().getNode(entry.getKey().getNodeId())) + 
							"\" ");
						break;
					}
				}
				break;
			case Split.TYPE_OR:
                writeNode("inclusiveGateway", node, xmlDump, includeMeta);
				for (Map.Entry<ConnectionRef, Constraint> entry: split.getConstraints().entrySet()) {
					if (entry.getValue().isDefault()) {
						xmlDump.append("default=\"" +
							XmlBPMNProcessDumper.getUniqueNodeId(split) + "-" +
							XmlBPMNProcessDumper.getUniqueNodeId(node.getNodeContainer().getNode(entry.getKey().getNodeId())) + 
							"\" ");
						break;
					}
				}
                break;
            default:
				writeNode("complexGateway", node, xmlDump, includeMeta);
		}
		xmlDump.append("gatewayDirection=\"diverging\" ");
		endNode(xmlDump);
	}

}
