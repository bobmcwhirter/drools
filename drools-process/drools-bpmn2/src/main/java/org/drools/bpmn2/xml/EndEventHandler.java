package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.FaultNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EndEventHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        EndNode node = new EndNode();
        node.setTerminate(false);
        return node;
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return EndNode.class;
    }

    public Object end(final String uri, final String localName,
            final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Node node = (Node) parser.getCurrent();
        // determine type of event definition, so the correct type of node
        // can be generated
        super.handleNode(node, element, uri, localName, parser);
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("terminateEventDefinition".equals(nodeName)) {
                // reuse already create EventNode
                handleTerminateNode(node, element, uri, localName, parser);
                break;
            } else if ("errorEventDefinition".equals(nodeName)) {
                // create new timerNode
                FaultNode faultNode = new FaultNode();
                faultNode.setId(node.getId());
                faultNode.setName(node.getName());
                node = faultNode;
                super.handleNode(node, element, uri, localName, parser);
                handleErrorNode(node, element, uri, localName, parser);
                break;
            }
            xmlNode = xmlNode.getNextSibling();
        }
        NodeContainer nodeContainer = (NodeContainer) parser.getParent();
        nodeContainer.addNode(node);
        return node;
    }
    
    public void handleTerminateNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        ((EndNode) node).setTerminate(true);
    }
    
    public void handleErrorNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        FaultNode faultNode = (FaultNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readDataOutputAssociation(xmlNode, faultNode);
            } else if ("errorEventDefinition".equals(nodeName)) {
                String faultName = ((Element) xmlNode).getAttribute("errorCode");
                if (faultName != null && faultName.trim().length() > 0) {
                    faultNode.setFaultName(faultName);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    protected void readDataOutputAssociation(org.w3c.dom.Node xmlNode, FaultNode faultNode) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        String faultVariable = subNode.getTextContent();
        faultNode.setFaultVariable(faultVariable);
    }

    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        throw new IllegalArgumentException("Writing out should be handled by specific handlers");
    }

}
