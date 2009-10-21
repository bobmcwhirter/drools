package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.WorkItemNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class ManualTaskHandler extends TaskHandler {
    
    protected Node createNode(Attributes attrs) {
        return new WorkItemNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Node.class;
    }
    
    protected String getTaskName(final Element element) {
        return "Manual Task";
    }
    
    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        throw new IllegalArgumentException("Writing out should be handled by TaskHandler");
    }
}
