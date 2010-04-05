package org.drools.bpmn2.xml;

import java.util.List;

import org.drools.bpmn2.core.SequenceFlow;
import org.drools.definition.process.Connection;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.DynamicNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class AdHocSubProcessHandler extends CompositeContextNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        DynamicNode result = new DynamicNode();
        VariableScope variableScope = new VariableScope();
        result.addContext(variableScope);
        result.setDefaultContext(variableScope);
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return DynamicNode.class;
    }
    
    @SuppressWarnings("unchecked")
	protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	DynamicNode dynamicNode = (DynamicNode) node;
    	String cancelRemainingInstances = element.getAttribute("cancelRemainingInstances");
    	if ("false".equals(cancelRemainingInstances)) {
    		dynamicNode.setCancelRemainingInstances(false);
    	}
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
        	String nodeName = xmlNode.getNodeName();
        	if ("completionCondition".equals(nodeName)) {
        		String expression = xmlNode.getTextContent();
        		if ("getActivityInstanceAttribute(\"numberOfActiveInstances\") == 0".equals(expression)) {
        			dynamicNode.setAutoComplete(true);
        		}
        	}
        	xmlNode = xmlNode.getNextSibling();
        }
    	List<SequenceFlow> connections = (List<SequenceFlow>)
			dynamicNode.getMetaData(ProcessHandler.CONNECTIONS);
    	ProcessHandler.linkConnections(dynamicNode, connections);
    	ProcessHandler.linkBoundaryEvents(dynamicNode);
    }
    
    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        DynamicNode dynamicNode = (DynamicNode) node;
		writeNode("adHocSubProcess", dynamicNode, xmlDump, includeMeta);
		if (!dynamicNode.isCancelRemainingInstances()) {
			xmlDump.append(" cancelRemainingInstances=\"false\"");
		}
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
        if (dynamicNode.isAutoComplete()) {
        	xmlDump.append("    <completionCondition xs:type=\"tFormalExpression\">getActivityInstanceAttribute(\"numberOfActiveInstances\") == 0</completionCondition>" + EOL);
        }
		endNode("adHocSubProcess", xmlDump);
	}

}