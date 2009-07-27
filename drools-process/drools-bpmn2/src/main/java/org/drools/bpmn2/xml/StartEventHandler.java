package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.StartNode;
import org.xml.sax.Attributes;

public class StartEventHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        return new StartNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return StartNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		StartNode startNode = (StartNode) node;
		writeNode("startEvent", startNode, xmlDump);
		endNode(xmlDump);
	}

}
