package org.drools.bpmn2.xml;

import java.util.ArrayList;
import java.util.List;

import org.drools.bpmn2.core.SequenceFlow;
import org.drools.definition.process.Connection;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.CompositeContextNode;
import org.drools.workflow.core.node.CompositeNode;
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

    @SuppressWarnings("unchecked")
	protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	CompositeContextNode compositeNode = (CompositeContextNode) node;
    	List<SequenceFlow> connections = (List<SequenceFlow>)
			compositeNode.getMetaData(ProcessHandler.CONNECTIONS);
    	ProcessHandler.linkConnections(compositeNode, connections);
    }
    
    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
    	CompositeContextNode compositeNode = (CompositeContextNode) node;
		writeNode("subProcess", compositeNode, xmlDump, includeMeta);
		xmlDump.append(" >" + EOL);
		// nodes
		List<Node> subNodes = getSubNodes(compositeNode);
    	xmlDump.append("    <!-- nodes -->" + EOL);
        for (Node subNode: subNodes) {
    		XmlBPMNProcessDumper.INSTANCE.visitNode(subNode, xmlDump, includeMeta);
        }
        // connections
        List<Connection> connections = getSubConnections(compositeNode);
    	xmlDump.append("    <!-- connections -->" + EOL);
        for (Connection connection: connections) {
        	XmlBPMNProcessDumper.INSTANCE.visitConnection(connection, xmlDump, includeMeta);
        }
		endNode("subProcess", xmlDump);
	}
	
	protected List<Node> getSubNodes(CompositeNode compositeNode) {
    	List<Node> subNodes =
    		new ArrayList<Node>();
        for (org.drools.definition.process.Node subNode: compositeNode.getNodes()) {
        	// filter out composite start and end nodes as they can be regenerated
        	if ((!(subNode instanceof CompositeNode.CompositeNodeStart)) &&
    			(!(subNode instanceof CompositeNode.CompositeNodeEnd))) {
        		subNodes.add((Node) subNode);
        	}
        }
        return subNodes;
    }
    
    protected List<Connection> getSubConnections(CompositeNode compositeNode) {
    	List<Connection> connections = new ArrayList<Connection>();
        for (org.drools.definition.process.Node subNode: compositeNode.getNodes()) {
        	// filter out composite start and end nodes as they can be regenerated
            if (!(subNode instanceof CompositeNode.CompositeNodeEnd)) {
                for (Connection connection: subNode.getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE)) {
                    if (!(connection.getFrom() instanceof CompositeNode.CompositeNodeStart)) {
                        connections.add(connection);
                    }
                }
            }
        }
        return connections;
    }

}