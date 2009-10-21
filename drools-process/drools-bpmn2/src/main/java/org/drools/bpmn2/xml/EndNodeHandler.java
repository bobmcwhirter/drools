package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.EndNode;
import org.xml.sax.Attributes;

public class EndNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        throw new IllegalArgumentException("Reading in should be handled by end event handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return EndNode.class;
    }

    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		EndNode endNode = (EndNode) node;
		writeNode("endEvent", endNode, xmlDump, includeMeta);
		if (endNode.isTerminate()) {
    		xmlDump.append(">" + EOL);
            xmlDump.append("        <terminateEventDefinition/>" + EOL);
    		endNode("endEvent", xmlDump);
		} else {
		    endNode(xmlDump);
		}
	}

}
