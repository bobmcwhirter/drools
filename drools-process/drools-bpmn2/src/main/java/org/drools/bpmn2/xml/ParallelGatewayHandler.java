package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.Split;
import org.xml.sax.Attributes;

public class ParallelGatewayHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        final String type = attrs.getValue("gatewayDirection");
        if ("converging".equals(type)) {
        	Join join = new Join();
        	join.setType(Join.TYPE_AND);
        	return join;
        } else if ("diverging".equals(type)) {
        	Split split = new Split();
        	split.setType(Split.TYPE_AND);
        	return split;
        } else {
        	throw new IllegalArgumentException(
    			"Unknown gateway direction: " + type);
        }
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Node.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		writeNode("exclusiveGateway", node, xmlDump);
		if (node instanceof Split) {
			xmlDump.append("gatewayDirection=\"diverging\" ");
		} else if (node instanceof Join) {
			xmlDump.append("gatewayDirection=\"converging\" ");
		}
		endNode(xmlDump);
	}

}
