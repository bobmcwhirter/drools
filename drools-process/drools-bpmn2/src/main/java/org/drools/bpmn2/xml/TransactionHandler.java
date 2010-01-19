package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class TransactionHandler extends SubProcessHandler {

	protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	node.setMetaData("Transaction", true);
    }
    
    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
        throw new IllegalArgumentException("Writing out should be handled by specific handlers");
    }

}