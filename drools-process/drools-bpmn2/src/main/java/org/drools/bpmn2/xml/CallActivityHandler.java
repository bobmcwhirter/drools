package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CallActivityHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        return new SubProcessNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return SubProcessNode.class;
    }

    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	SubProcessNode subProcessNode = (SubProcessNode) node;
		String processId = element.getAttribute("calledElement");
		if (processId != null) {
			subProcessNode.setProcessId(processId);
		}
	}

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		SubProcessNode subProcessNode = (SubProcessNode) node;
		writeNode("callActivity", subProcessNode, xmlDump, includeMeta);
		if (subProcessNode.getProcessId() != null) {
			xmlDump.append("calledElement=\"" + subProcessNode.getProcessId() + "\" ");
		}
		endNode(xmlDump);
	}

}