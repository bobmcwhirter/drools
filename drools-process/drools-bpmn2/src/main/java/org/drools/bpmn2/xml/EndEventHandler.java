package org.drools.bpmn2.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.bpmn2.core.Message;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.FaultNode;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.ProcessBuildData;
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
                // reuse already created EndNode
                handleTerminateNode(node, element, uri, localName, parser);
                break;
            } else if ("signalEventDefinition".equals(nodeName)) {
                handleSignalNode(node, element, uri, localName, parser);
            } else if ("messageEventDefinition".equals(nodeName)) {
                handleMessageNode(node, element, uri, localName, parser);
            } else if ("errorEventDefinition".equals(nodeName)) {
                // create new faultNode
                FaultNode faultNode = new FaultNode();
                faultNode.setId(node.getId());
                faultNode.setName(node.getName());
                faultNode.setTerminateParent(true);
                node = faultNode;
                super.handleNode(node, element, uri, localName, parser);
                handleErrorNode(node, element, uri, localName, parser);
                break;
            } else if ("escalationEventDefinition".equals(nodeName)) {
                // create new faultNode
                FaultNode faultNode = new FaultNode();
                faultNode.setId(node.getId());
                faultNode.setName(node.getName());
                node = faultNode;
                super.handleNode(node, element, uri, localName, parser);
                handleEscalationNode(node, element, uri, localName, parser);
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
    
    public void handleSignalNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        EndNode endNode = (EndNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readEndDataInputAssociation(xmlNode, endNode);
            } else if ("signalEventDefinition".equals(nodeName)) {
                String signalName = ((Element) xmlNode).getAttribute("signalRef");
                String variable = (String) endNode.getMetaData("MappingVariable");
                List<DroolsAction> actions = new ArrayList<DroolsAction>();
                actions.add(new DroolsConsequenceAction("mvel",
                    "kcontext.getKnowledgeRuntime().signalEvent(\""
                        + signalName + "\", " + (variable == null ? "null" : variable) + ")"));
                endNode.setActions(EndNode.EVENT_NODE_ENTER, actions);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void handleMessageNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        EndNode endNode = (EndNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readEndDataInputAssociation(xmlNode, endNode);
            } else if ("messageEventDefinition".equals(nodeName)) {
                String messageRef = ((Element) xmlNode).getAttribute("messageRef");
                Map<String, Message> messages = (Map<String, Message>)
                    ((ProcessBuildData) parser.getData()).getMetaData("Messages");
                if (messages == null) {
                    throw new IllegalArgumentException("No messages found");
                }
                Message message = messages.get(messageRef);
                if (message == null) {
                    throw new IllegalArgumentException("Could not find message " + messageRef);
                }
                String variable = (String) endNode.getMetaData("MappingVariable");
                endNode.setMetaData("MessageType", message.getType());
                List<DroolsAction> actions = new ArrayList<DroolsAction>();
                
                actions.add(new DroolsConsequenceAction("java",
                    "org.drools.process.instance.impl.WorkItemImpl workItem = new org.drools.process.instance.impl.WorkItemImpl();" + EOL + 
                    "workItem.setName(\"Send Task\");" + EOL + 
                    "workItem.setParameter(\"MessageType\", \"" + message.getType() + "\");" + EOL + 
                    (variable == null ? "" : "workItem.setParameter(\"Message\", " + variable + ");" + EOL) +
                    "((org.drools.process.instance.WorkItemManager) kcontext.getKnowledgeRuntime().getWorkItemManager()).internalExecuteWorkItem(workItem);"));
                endNode.setActions(EndNode.EVENT_NODE_ENTER, actions);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    protected void readEndDataInputAssociation(org.w3c.dom.Node xmlNode, EndNode endNode) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        String eventVariable = subNode.getTextContent();
        if (eventVariable != null && eventVariable.trim().length() > 0) {
            endNode.setMetaData("MappingVariable", eventVariable);
        }
    }

    public void handleErrorNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        FaultNode faultNode = (FaultNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readFaultDataInputAssociation(xmlNode, faultNode);
            } else if ("errorEventDefinition".equals(nodeName)) {
                String faultName = ((Element) xmlNode).getAttribute("errorCode");
                if (faultName != null && faultName.trim().length() > 0) {
                    faultNode.setFaultName(faultName);
                }
                faultNode.setTerminateParent(true);
            } else if ("escalationEventDefinition".equals(nodeName)) {
                String faultName = ((Element) xmlNode).getAttribute("escalationCode");
                if (faultName != null && faultName.trim().length() > 0) {
                    faultNode.setFaultName(faultName);
                }
            } 
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    public void handleEscalationNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
        FaultNode faultNode = (FaultNode) node;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readFaultDataInputAssociation(xmlNode, faultNode);
            } else if ("escalationEventDefinition".equals(nodeName)) {
                String faultName = ((Element) xmlNode).getAttribute("escalationCode");
                if (faultName != null && faultName.trim().length() > 0) {
                    faultNode.setFaultName(faultName);
                }
            } 
            xmlNode = xmlNode.getNextSibling();
        }
    }
    
    protected void readFaultDataInputAssociation(org.w3c.dom.Node xmlNode, FaultNode faultNode) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        String faultVariable = subNode.getTextContent();
        faultNode.setFaultVariable(faultVariable);
    }

    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        throw new IllegalArgumentException("Writing out should be handled by specific handlers");
    }

}
