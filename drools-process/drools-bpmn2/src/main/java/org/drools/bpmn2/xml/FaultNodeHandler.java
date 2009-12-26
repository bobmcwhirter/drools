package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.FaultNode;
import org.xml.sax.Attributes;

public class FaultNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        throw new IllegalArgumentException("Reading in should be handled by end event handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return FaultNode.class;
    }

    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        FaultNode faultNode = (FaultNode) node;
		writeNode("endEvent", faultNode, xmlDump, includeMeta);
		xmlDump.append(">" + EOL);
        if (faultNode.getFaultVariable() != null) {
            xmlDump.append("      <dataInput id=\"_" + XmlBPMNProcessDumper.getUniqueNodeId(faultNode) + "_Input\" name=\"error\" />" + EOL);
            xmlDump.append("      <dataInputAssociation>" + EOL);
            xmlDump.append(
                "        <sourceRef>" + faultNode.getFaultVariable() + "</sourceRef>" + EOL +
                "        <targetRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(faultNode) + "_Input</targetRef>" + EOL);
            xmlDump.append("      </dataInputAssociation>" + EOL);
            xmlDump.append("      <inputSet>" + EOL);
            xmlDump.append("        <dataInputRefs>_" + XmlBPMNProcessDumper.getUniqueNodeId(faultNode) + "_Input</dataInputRefs>" + EOL);
            xmlDump.append("      </inputSet>" + EOL);
        }
        if (faultNode.isTerminateParent()) {
            xmlDump.append("      <errorEventDefinition errorCode=\"" + faultNode.getFaultName() + "\" />" + EOL);
        } else {
            xmlDump.append("      <escalationEventDefinition escalationCode=\"" + faultNode.getFaultName() + "\" />" + EOL);
        }
		endNode("endEvent", xmlDump);
	}

}
