package org.drools.bpmn2.xml;

import org.drools.process.core.event.EventTypeFilter;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.EventNode;
import org.xml.sax.Attributes;

public class EventNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        throw new IllegalArgumentException("Reading in should be handled by intermediate catch event handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return EventNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		EventNode eventNode = (EventNode) node;
		writeNode("intermediateCatchEvent", eventNode, xmlDump, includeMeta);
		xmlDump.append(">" + EOL);
		if (eventNode.getVariableName() != null) {
			xmlDump.append("        <dataOutput id=\"_" + XmlBPMNProcessDumper.getUniqueNodeId(eventNode) + "_Output\" name=\"event\" />" + EOL);
			xmlDump.append("      <dataOutputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(eventNode) + "_Output</sourceRef>" + EOL +
				"        <targetRef>" + eventNode.getVariableName() + "</targetRef>" + EOL);
			xmlDump.append("      </dataOutputAssociation>" + EOL);
			xmlDump.append("        <outputSet>" + EOL);
			xmlDump.append("          <dataOutputRefs>_" + XmlBPMNProcessDumper.getUniqueNodeId(eventNode) + "_Output</dataOutputRefs>" + EOL);
			xmlDump.append("        </outputSet>" + EOL);
		}
		if (eventNode.getEventFilters().size() > 0) {
			String type = ((EventTypeFilter) eventNode.getEventFilters().get(0)).getType();
			xmlDump.append("        <signalEventDefinition signalRef=\"" + type + "\"/>" + EOL);
			
		}
		endNode("intermediateCatchEvent", xmlDump);
	}

}
