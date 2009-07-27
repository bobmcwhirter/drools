package org.drools.bpmn2.xml;

import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.Work;
import org.drools.process.core.impl.WorkImpl;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class TaskHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        return new WorkItemNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return WorkItemNode.class;
    }

    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	WorkItemNode workItemNode = (WorkItemNode) node;
        String name = element.getAttribute("name");
        Work work = new WorkImpl();
        work.setName(name);
    	workItemNode.setWork(work);
    	
    	Map<String, String> dataInputs = new HashMap<String, String>();
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
        	String nodeName = xmlNode.getNodeName();
        	if ("ioSpecification".equals(nodeName)) {
        		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        		while (subNode instanceof Element) {
        			String subNodeName = subNode.getNodeName();
                	if ("dataInput".equals(subNodeName)) {
                		String id = ((Element) subNode).getAttribute("id");
                		String inputName = ((Element) subNode).getAttribute("name");
                		dataInputs.put(id, inputName);
                	}
                	subNode = subNode.getNextSibling();
        		}
        	} else if ("dataInputAssociation".equals(nodeName)) {
        		// sourceRef
        		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
        		String from = subNode.getTextContent();
        		// targetRef
        		subNode = subNode.getNextSibling();
        		String to = subNode.getTextContent();
        		workItemNode.addInMapping(dataInputs.get(to), from);
        	}
    		xmlNode = xmlNode.getNextSibling();
        }
	}

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		WorkItemNode workItemNode = (WorkItemNode) node;
		writeNode("task", workItemNode, xmlDump);
		endNode(xmlDump);
	}

}
