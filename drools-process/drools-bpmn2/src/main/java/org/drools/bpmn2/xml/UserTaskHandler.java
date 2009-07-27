package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.HumanTaskNode;
import org.xml.sax.Attributes;

public class UserTaskHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        return new HumanTaskNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return HumanTaskNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		HumanTaskNode humanTaskNode = (HumanTaskNode) node;
		writeNode("userTask", humanTaskNode, xmlDump);
		endNode(xmlDump);
	}

}
