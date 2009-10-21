package org.drools.bpmn2.xml;

import java.util.List;

import org.drools.definition.process.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.DynamicNode;
import org.xml.sax.Attributes;

public class AdHocSubProcessHandler extends SubProcessHandler {
    
    protected Node createNode(Attributes attrs) {
        return new DynamicNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return DynamicNode.class;
    }
    
    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        DynamicNode dynamicNode = (DynamicNode) node;
		writeNode("adHocSubProcess", dynamicNode, xmlDump, includeMeta);
		xmlDump.append(" ordering=\"parallel\" >" + EOL);
		// nodes
		List<Node> subNodes = getSubNodes(dynamicNode);
    	xmlDump.append("    <!-- nodes -->" + EOL);
        for (Node subNode: subNodes) {
    		XmlBPMNProcessDumper.INSTANCE.visitNode(subNode, xmlDump, includeMeta);
        }
        // connections
        List<Connection> connections = getSubConnections(dynamicNode);
    	xmlDump.append("    <!-- connections -->" + EOL);
        for (Connection connection: connections) {
        	XmlBPMNProcessDumper.INSTANCE.visitConnection(connection, xmlDump, includeMeta);
        }
		endNode("adHocSubProcess", xmlDump);
	}

}