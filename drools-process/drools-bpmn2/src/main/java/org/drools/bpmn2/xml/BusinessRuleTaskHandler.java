package org.drools.bpmn2.xml;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.RuleSetNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BusinessRuleTaskHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        return new RuleSetNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return RuleSetNode.class;
    }

    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
        RuleSetNode ruleSetNode = (RuleSetNode) node;
		String ruleFlowGroup = element.getAttribute("ruleFlowGroup");
		if (ruleFlowGroup != null) {
			ruleSetNode.setRuleFlowGroup(ruleFlowGroup);
		}
	}

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		RuleSetNode ruleSetNode = (RuleSetNode) node;
		writeNode("businessRuleTask", ruleSetNode, xmlDump, includeMeta);
		if (ruleSetNode.getRuleFlowGroup() != null) {
			xmlDump.append("g:ruleFlowGroup=\"" + ruleSetNode.getRuleFlowGroup() + "\" ");
		}
		endNode(xmlDump);
	}

}
