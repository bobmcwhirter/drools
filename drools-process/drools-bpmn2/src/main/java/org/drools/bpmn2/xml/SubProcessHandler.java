package org.drools.bpmn2.xml;

import java.util.List;
import java.util.Map;

import org.drools.bpmn2.core.ItemDefinition;
import org.drools.bpmn2.core.SequenceFlow;
import org.drools.compiler.xml.ProcessBuildData;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.node.CompositeContextNode;
import org.drools.workflow.core.node.ForEachNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SubProcessHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
    	CompositeContextNode result = new CompositeContextNode();
        VariableScope variableScope = new VariableScope();
        result.addContext(variableScope);
        result.setDefaultContext(variableScope);
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return CompositeContextNode.class;
    }

    public Object end(final String uri, final String localName,
            final ExtensibleXmlParser parser) throws SAXException {
		final Element element = parser.endElementBuilder();
		Node node = (Node) parser.getCurrent();
		// determine type of event definition, so the correct type of node
		// can be generated
		boolean found = false;
		org.w3c.dom.Node xmlNode = element.getFirstChild();
		while (xmlNode != null) {
			String nodeName = xmlNode.getNodeName();
			if ("multiInstanceLoopCharacteristics".equals(nodeName)) {
				// create new timerNode
				ForEachNode forEachNode = new ForEachNode();
				forEachNode.setId(node.getId());
				forEachNode.setName(node.getName());
				for (org.drools.definition.process.Node subNode: ((CompositeContextNode) node).getNodes()) {
					forEachNode.addNode(subNode);
				}
				forEachNode.setMetaData(ProcessHandler.CONNECTIONS, ((CompositeContextNode) node).getMetaData(ProcessHandler.CONNECTIONS));
				node = forEachNode;
				handleForEachNode(node, element, uri, localName, parser);
				found = true;
				break;
			}
			xmlNode = xmlNode.getNextSibling();
		}
		if (!found) {
			handleCompositeContextNode(node, element, uri, localName, parser);
		}
		NodeContainer nodeContainer = (NodeContainer) parser.getParent();
		nodeContainer.addNode(node);
		return node;
	}
    
    @SuppressWarnings("unchecked")
	protected void handleCompositeContextNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	CompositeContextNode compositeNode = (CompositeContextNode) node;
    	List<SequenceFlow> connections = (List<SequenceFlow>)
			compositeNode.getMetaData(ProcessHandler.CONNECTIONS);
    	ProcessHandler.linkConnections(compositeNode, connections);
    	ProcessHandler.linkBoundaryEvents(compositeNode);
    }
    
    @SuppressWarnings("unchecked")
	protected void handleForEachNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	ForEachNode forEachNode = (ForEachNode) node;
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("dataInputAssociation".equals(nodeName)) {
                readDataInputAssociation(xmlNode, forEachNode);
            } else if ("multiInstanceLoopCharacteristics".equals(nodeName)) {
            	readMultiInstanceLoopCharacteristics(xmlNode, forEachNode, parser);
            }
            xmlNode = xmlNode.getNextSibling();
        }
    	List<SequenceFlow> connections = (List<SequenceFlow>)
			forEachNode.getMetaData(ProcessHandler.CONNECTIONS);
    	ProcessHandler.linkConnections(forEachNode, connections);
    	ProcessHandler.linkBoundaryEvents(forEachNode);
    }
    
    protected void readDataInputAssociation(org.w3c.dom.Node xmlNode, ForEachNode forEachNode) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        String inputVariable = subNode.getTextContent();
        if (inputVariable != null && inputVariable.trim().length() > 0) {
        	forEachNode.setCollectionExpression(inputVariable);
        }
    }
    
    @SuppressWarnings("unchecked")
	protected void readMultiInstanceLoopCharacteristics(org.w3c.dom.Node xmlNode, ForEachNode forEachNode, ExtensibleXmlParser parser) {
        // sourceRef
        org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        while (subNode != null) {
            String nodeName = subNode.getNodeName();
            if ("inputDataItem".equals(nodeName)) {
            	String variableName = ((Element) subNode).getAttribute("id");
            	String itemSubjectRef = ((Element) subNode).getAttribute("itemSubjectRef");
            	DataType dataType = null;
            	Map<String, ItemDefinition> itemDefinitions = (Map<String, ItemDefinition>)
	            	((ProcessBuildData) parser.getData()).getMetaData("ItemDefinitions");
		        if (itemDefinitions != null) {
		        	ItemDefinition itemDefinition = itemDefinitions.get(itemSubjectRef);
		        	if (itemDefinition != null) {
		        		dataType = new ObjectDataType(itemDefinition.getStructureRef());
		        	}
		        }
                if (variableName != null && variableName.trim().length() > 0) {
                	forEachNode.setVariable(variableName, dataType);
                }
            }
            subNode = subNode.getNextSibling();
        }
    }
    
    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        throw new IllegalArgumentException("Writing out should be handled by specific handlers");
    }

}