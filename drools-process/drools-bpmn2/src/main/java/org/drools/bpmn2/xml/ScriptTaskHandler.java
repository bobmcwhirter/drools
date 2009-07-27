package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.XmlDumper;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ScriptTaskHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        ActionNode result = new ActionNode();
        result.setAction(new DroolsConsequenceAction());
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return ActionNode.class;
    }

    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
        ActionNode actionNode = (ActionNode) node;
        DroolsConsequenceAction action = (DroolsConsequenceAction) actionNode.getAction();
		String language = element.getAttribute("scriptLanguage");
		if (language != null) {
			action.setDialect(language);
		}
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        if (xmlNode instanceof Element) {
    		action.setConsequence(xmlNode.getTextContent());
        }
	}

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		ActionNode actionNode = (ActionNode) node;
		writeNode("scriptTask", actionNode, xmlDump);
		DroolsConsequenceAction action = (DroolsConsequenceAction) actionNode.getAction();
		xmlDump.append("scriptLanguage=\"" + action.getDialect() + "\" >\n");
		xmlDump.append("      <script>" + XmlDumper.replaceIllegalChars(action.getConsequence()) + "</script>\n");
		endNode("scriptTask", xmlDump);
	}

}
