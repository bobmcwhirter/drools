package org.drools.bpmn2.xml;

import java.util.ArrayList;
import java.util.List;

import org.drools.definition.process.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.CompositeNode;
import org.drools.workflow.core.node.ForEachNode;
import org.xml.sax.Attributes;

public class ForEachNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
    	throw new IllegalArgumentException("Reading in should be handled by end event handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return ForEachNode.class;
    }

    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
    	ForEachNode forEachNode = (ForEachNode) node;
    	writeNode("subProcess", forEachNode, xmlDump, includeMeta);
		xmlDump.append(" >" + EOL);
		// ioSpecification and dataInputAssociation 
        xmlDump.append(
            "      <ioSpecification>" + EOL +
            "        <dataInput id=\"_" + XmlBPMNProcessDumper.getUniqueNodeId(forEachNode) + "_input\" name=\"MultiInstanceInput\" />" + EOL +
            "        <inputSet>" + EOL +
            "          <dataInputRefs>_" + XmlBPMNProcessDumper.getUniqueNodeId(forEachNode) + "_input</dataInputRefs>" + EOL +
            "        </inputSet>" + EOL +
            "        <outputSet/>" + EOL +
            "      </ioSpecification>" + EOL);
        String collectionExpression = forEachNode.getCollectionExpression();
        if (collectionExpression != null) {
            xmlDump.append(
                "      <dataInputAssociation>" + EOL +
                "        <sourceRef>" + collectionExpression + "</sourceRef>" + EOL +
                "        <targetRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(forEachNode) + "_input</targetRef>" + EOL +
                "      </dataInputAssociation>" + EOL);
        }
        // multiInstanceLoopCharacteristics
        xmlDump.append(
    		"      <multiInstanceLoopCharacteristics>" + EOL +
            "        <loopDataInputRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(forEachNode) + "_input</loopDataInputRef>" + EOL);
        String parameterName = forEachNode.getVariableName();
        if (parameterName != null) {
        	xmlDump.append("        <inputDataItem id=\"" + parameterName + "\" itemSubjectRef=\"_" + XmlBPMNProcessDumper.getUniqueNodeId(forEachNode) + "_multiInstanceItemType\"/>" + EOL);
        }
        xmlDump.append("      </multiInstanceLoopCharacteristics>" + EOL);
		// nodes
		List<Node> subNodes = getSubNodes(forEachNode);
    	xmlDump.append("    <!-- nodes -->" + EOL);
        for (Node subNode: subNodes) {
    		XmlBPMNProcessDumper.INSTANCE.visitNode(subNode, xmlDump, includeMeta);
        }
        // connections
        List<Connection> connections = getSubConnections(forEachNode);
    	xmlDump.append("    <!-- connections -->" + EOL);
        for (Connection connection: connections) {
        	XmlBPMNProcessDumper.INSTANCE.visitConnection(connection, xmlDump, includeMeta);
        }
		endNode("subProcess", xmlDump);
	}
	
	protected List<Node> getSubNodes(ForEachNode forEachNode) {
    	List<Node> subNodes =
    		new ArrayList<Node>();
        for (org.drools.definition.process.Node subNode: forEachNode.getNodes()) {
        	// filter out composite start and end nodes as they can be regenerated
        	if ((!(subNode instanceof CompositeNode.CompositeNodeStart)) &&
    			(!(subNode instanceof CompositeNode.CompositeNodeEnd))) {
        		subNodes.add((Node) subNode);
        	}
        }
        return subNodes;
    }
    
    protected List<Connection> getSubConnections(ForEachNode forEachNode) {
    	List<Connection> connections = new ArrayList<Connection>();
        for (org.drools.definition.process.Node subNode: forEachNode.getNodes()) {
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