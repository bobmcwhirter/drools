package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.Join;
import org.xml.sax.Attributes;

public class JoinHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
    	throw new IllegalArgumentException("Reading in should be handled by gateway handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Join.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		Join join = (Join) node;
		switch (join.getType()) {
			case Join.TYPE_AND:
				writeNode("parallelGateway", node, xmlDump, includeMeta);
				break;
			case Join.TYPE_XOR:
				writeNode("exclusiveGateway", node, xmlDump, includeMeta);
				break;
			default:
				throw new IllegalArgumentException("Unsupported join type: " + join.getType());
		}
		xmlDump.append("gatewayDirection=\"converging\" ");
		endNode(xmlDump);
	}

}
