package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class IntermediateThrowEventHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        return new ActionNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Node.class;
    }

    public Object end(final String uri, final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        ActionNode node = (ActionNode) parser.getCurrent();
        // determine type of event definition, so the correct type of node
        // can be generated
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("signalEventDefinition".equals(nodeName)) {
                // reuse already created ActionNode
                handleSignalNode(node, element, uri, localName, parser);
                break;
            }
            xmlNode = xmlNode.getNextSibling();
        }
        // none event definition
        if (node.getAction() == null) {
            node.setAction(new DroolsConsequenceAction("mvel", ""));
        }
        NodeContainer nodeContainer = (NodeContainer) parser.getParent();
        nodeContainer.addNode(node);
        return node;
    }
    
    public void handleSignalNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        ActionNode actionNode = (ActionNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readDataInputAssociation(xmlNode, actionNode);
            } else if ("signalEventDefinition".equals(nodeName)) {
                String signalName = ((Element) xmlNode).getAttribute("signalRef");
                String variable = (String) actionNode.getMetaData("MappingVariable");
                actionNode.setAction(new DroolsConsequenceAction("mvel",
                    "kcontext.getKnowledgeRuntime().signalEvent(\""
                        + signalName + "\", " + (variable == null ? "null" : variable) + ")"));
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    protected void readDataInputAssociation(org.w3c.dom.Node xmlNode, ActionNode actionNode) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        String eventVariable = subNode.getTextContent();
        if (eventVariable != null && eventVariable.trim().length() > 0) {
            actionNode.setMetaData("MappingVariable", eventVariable);
        }
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        throw new IllegalArgumentException("Writing out should be handled by action node handler");
    }

}
