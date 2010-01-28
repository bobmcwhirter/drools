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
		String attachedTo = (String) eventNode.getMetaData("AttachedTo");
		if (attachedTo == null) {
    		writeNode("intermediateCatchEvent", eventNode, xmlDump, includeMeta);
    		xmlDump.append(">" + EOL);
    		if (eventNode.getVariableName() != null) {
    			xmlDump.append("      <dataOutput id=\"_" + XmlBPMNProcessDumper.getUniqueNodeId(eventNode) + "_Output\" name=\"event\" />" + EOL);
    			xmlDump.append("      <dataOutputAssociation>" + EOL);
    			xmlDump.append(
    				"      <sourceRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(eventNode) + "_Output</sourceRef>" + EOL +
    				"      <targetRef>" + eventNode.getVariableName() + "</targetRef>" + EOL);
    			xmlDump.append("      </dataOutputAssociation>" + EOL);
    			xmlDump.append("      <outputSet>" + EOL);
    			xmlDump.append("        <dataOutputRefs>_" + XmlBPMNProcessDumper.getUniqueNodeId(eventNode) + "_Output</dataOutputRefs>" + EOL);
    			xmlDump.append("      </outputSet>" + EOL);
    		}
    		if (eventNode.getEventFilters().size() > 0) {
    			String type = ((EventTypeFilter) eventNode.getEventFilters().get(0)).getType();
    			if (type.startsWith("Message-")) {
    			    type = type.substring(8);
    			    xmlDump.append("      <messageEventDefinition messageRef=\"" + type + "\"/>" + EOL);
                } else {
                    xmlDump.append("      <signalEventDefinition signalRef=\"" + type + "\"/>" + EOL);
                }
    		}
    		endNode("intermediateCatchEvent", xmlDump);
		} else {
		    String type = ((EventTypeFilter) eventNode.getEventFilters().get(0)).getType();
		    if (type.startsWith("Escalation-")) {
    		    type = type.substring(attachedTo.length() + 12);
    		    boolean cancelActivity = (Boolean) eventNode.getMetaData("CancelActivity");
                writeNode("boundaryEvent", eventNode, xmlDump, includeMeta);
    		    xmlDump.append("attachedToRef=\"" + attachedTo + "\" ");
    		    if (!cancelActivity) {
    		        xmlDump.append("cancelActivity=\"false\" ");
    		    }
    		    xmlDump.append(">" + EOL);
    		    xmlDump.append("      <escalationEventDefinition escalationCode=\"" + type + "\" />" + EOL);
    		    endNode("boundaryEvent", xmlDump);
		    } else if (type.startsWith("Error-")) {
                type = type.substring(attachedTo.length() + 7);
                writeNode("boundaryEvent", eventNode, xmlDump, includeMeta);
                xmlDump.append("attachedToRef=\"" + attachedTo + "\" ");
                xmlDump.append(">" + EOL);
                xmlDump.append("      <errorEventDefinition errorCode=\"" + type + "\" />" + EOL);
                endNode("boundaryEvent", xmlDump);
            } else if (type.startsWith("Timer-")) {
                type = type.substring(attachedTo.length() + 7);
                boolean cancelActivity = (Boolean) eventNode.getMetaData("CancelActivity");
                writeNode("boundaryEvent", eventNode, xmlDump, includeMeta);
                xmlDump.append("attachedToRef=\"" + attachedTo + "\" ");
                if (!cancelActivity) {
                    xmlDump.append("cancelActivity=\"false\" ");
                }
                xmlDump.append(">" + EOL);
                xmlDump.append(
                    "      <timerEventDefinition>" + EOL +
                    "        <timeCycle xs:type=\"tFormalExpression\">" + eventNode.getMetaData("TimeCycle") + "</timeCycle>" + EOL +
                    "      </timerEventDefinition>" + EOL);
                endNode("boundaryEvent", xmlDump);
            } else if (type.startsWith("Compensate-")) {
                type = type.substring(attachedTo.length() + 7);
                writeNode("boundaryEvent", eventNode, xmlDump, includeMeta);
                xmlDump.append("attachedToRef=\"" + attachedTo + "\" ");
                xmlDump.append(">" + EOL);
                xmlDump.append("      <compensateEventDefinition/>" + EOL);
                endNode("boundaryEvent", xmlDump);
            } 
		}
	}

}
