package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.EndNode;
import org.xml.sax.Attributes;

public class EndEventHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        return new EndNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return EndNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		EndNode endNode = (EndNode) node;
		writeNode("endEvent", endNode, xmlDump, includeMeta);
		endNode(xmlDump);
	}

}
