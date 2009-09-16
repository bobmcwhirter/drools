package org.drools.bpmn2.xml;

import java.util.ArrayList;
import java.util.List;

import org.drools.process.core.event.EventFilter;
import org.drools.process.core.event.EventTypeFilter;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.EventNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class IntermediateCatchEventHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        return new EventNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return EventNode.class;
    }

    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	EventNode eventNode = (EventNode) node;
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
        	String nodeName = xmlNode.getNodeName();
        	if ("dataOutputAssociation".equals(nodeName)) {
        		readDataOutputAssociation(xmlNode, eventNode);
        	} else if ("signalEventDefinition".equals(nodeName)) {
        		String type = ((Element) xmlNode).getAttribute("signalRef");
        		if (type != null && type.trim().length() > 0) {
        			List<EventFilter> eventFilters = new ArrayList<EventFilter>();
                	EventTypeFilter eventFilter = new EventTypeFilter();
                	eventFilter.setType(type);
                	eventFilters.add(eventFilter);
                    eventNode.setEventFilters(eventFilters);
        		}
        	}
    		xmlNode = xmlNode.getNextSibling();
        }
	}
    
    protected void readDataOutputAssociation(org.w3c.dom.Node xmlNode, EventNode eventNode) {
		// sourceRef
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		// targetRef
		subNode = subNode.getNextSibling();
		String to = subNode.getTextContent();
		eventNode.setVariableName(to);
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
