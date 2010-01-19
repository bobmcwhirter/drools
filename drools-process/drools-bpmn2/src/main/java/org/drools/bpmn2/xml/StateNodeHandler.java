package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.StateNode;
import org.xml.sax.Attributes;

public class StateNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        throw new IllegalArgumentException("Reading in should be handled by intermediate catch event handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return StateNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
	    StateNode stateNode = (StateNode) node;
		String condition = (String) stateNode.getMetaData("Condition");
		writeNode("intermediateCatchEvent", stateNode, xmlDump, includeMeta);
		xmlDump.append(">" + EOL);
        xmlDump.append("      <conditionalEventDefinition>" + EOL);
        xmlDump.append("        <condition xs:type=\"tFormalExpression\" language=\"" + XmlBPMNProcessDumper.RULE_LANGUAGE + "\">" + condition + "</condition>" + EOL);
        xmlDump.append("      </conditionalEventDefinition>" + EOL);
		endNode("intermediateCatchEvent", xmlDump);
	}

}
