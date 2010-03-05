package org.drools.bpmn2.xml;

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
