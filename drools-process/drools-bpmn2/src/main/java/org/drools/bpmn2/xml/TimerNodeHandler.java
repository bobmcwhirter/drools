package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.TimerNode;
import org.xml.sax.Attributes;

public class TimerNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        throw new IllegalArgumentException("Reading in should be handled by intermediate catch event handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return TimerNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
	    TimerNode timerNode = (TimerNode) node;
		writeNode("intermediateCatchEvent", timerNode, xmlDump, includeMeta);
		xmlDump.append(">" + EOL);
		xmlDump.append("      <timerEventDefinition>" + EOL);
		if (timerNode.getTimer() != null && timerNode.getTimer().getDelay() != null) {
		    xmlDump.append("        <timeCycle>" + timerNode.getTimer().getDelay() + "</timeCycle>" + EOL);
		}
		xmlDump.append("      </timerEventDefinition>" + EOL);
		endNode("intermediateCatchEvent", xmlDump);
	}

}
