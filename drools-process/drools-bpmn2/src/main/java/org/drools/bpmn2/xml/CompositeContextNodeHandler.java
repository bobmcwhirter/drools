package org.drools.bpmn2.xml;

import java.util.ArrayList;
import java.util.List;

import org.drools.definition.process.Connection;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.CompositeContextNode;
import org.drools.workflow.core.node.CompositeNode;
import org.xml.sax.Attributes;

public class CompositeContextNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
    	throw new IllegalArgumentException("Reading in should be handled by end event handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return CompositeContextNode.class;
    }

    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
    	CompositeContextNode compositeNode = (CompositeContextNode) node;
    	String nodeType = "subProcess";
    	if (node.getMetaData("Transaction") != null) {
    		nodeType = "transaction";
    	}
		writeNode(nodeType, compositeNode, xmlDump, includeMeta);
		xmlDump.append(" >" + EOL);
        // variables
		VariableScope variableScope = (VariableScope) 
            compositeNode.getDefaultContext(VariableScope.VARIABLE_SCOPE);
		if (variableScope != null && !variableScope.getVariables().isEmpty()) {
            xmlDump.append("    <!-- variables -->" + EOL);
            for (Variable variable: variableScope.getVariables()) {
                xmlDump.append("    <property id=\"" + variable.getName() + "\" ");
                if (variable.getType() != null) {
                    xmlDump.append("itemSubjectRef=\"_" + XmlBPMNProcessDumper.getUniqueNodeId(compositeNode) + "-" + variable.getName() + "Item\"" );
                }
                // TODO: value
                xmlDump.append("/>" + EOL);
            }
		}
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
		endNode(nodeType, xmlDump);
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