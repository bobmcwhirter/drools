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
        String name = element.getAttribute("taskName");
        Work work = new WorkImpl();
        work.setName(name);
    	workItemNode.setWork(work);
    	Map<String, String> dataInputs = new HashMap<String, String>();
    	Map<String, String> dataOutputs = new HashMap<String, String>();
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
        	String nodeName = xmlNode.getNodeName();
        	if ("ioSpecification".equals(nodeName)) {
        		readIoSpecification(xmlNode, dataInputs, dataOutputs);
        	} else if ("dataInputAssociation".equals(nodeName)) {
        		readDataInputAssociation(xmlNode, workItemNode, dataInputs);
        	} else if ("dataOutputAssociation".equals(nodeName)) {
        		readDataOutputAssociation(xmlNode, workItemNode, dataOutputs);
        	}
    		xmlNode = xmlNode.getNextSibling();
        }
	}
    
    protected void readIoSpecification(org.w3c.dom.Node xmlNode, Map<String, String> dataInputs, Map<String, String> dataOutputs) {
    	org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		while (subNode instanceof Element) {
			String subNodeName = subNode.getNodeName();
        	if ("dataInput".equals(subNodeName)) {
        		String id = ((Element) subNode).getAttribute("id");
        		String inputName = ((Element) subNode).getAttribute("name");
        		dataInputs.put(id, inputName);
        	}
        	if ("dataOutput".equals(subNodeName)) {
        		String id = ((Element) subNode).getAttribute("id");
        		String outputName = ((Element) subNode).getAttribute("name");
        		dataOutputs.put(id, outputName);
        	}
        	subNode = subNode.getNextSibling();
		}
    }
    
    protected void readDataInputAssociation(org.w3c.dom.Node xmlNode, WorkItemNode workItemNode, Map<String, String> dataInputs) {
		// sourceRef
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		if ("assignment".equals(subNode.getNodeName())) {
			org.w3c.dom.Node subSubNode = subNode.getFirstChild();
			String from = subSubNode.getTextContent();
			subNode = subNode.getNextSibling();
    		String to = subNode.getTextContent();
    		workItemNode.getWork().setParameter(to.substring(XmlBPMNProcessDumper.getUniqueNodeId(workItemNode).length() + 2), from);
		} else {
    		String from = subNode.getTextContent();
    		// targetRef
    		subNode = subNode.getNextSibling();
    		String to = subNode.getTextContent();
    		workItemNode.addInMapping(
				dataInputs.get(to),
				from);
		}
    }
    
    protected void readDataOutputAssociation(org.w3c.dom.Node xmlNode, WorkItemNode workItemNode, Map<String, String> dataOutputs) {
		// sourceRef
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		String from = subNode.getTextContent();
		// targetRef
		subNode = subNode.getNextSibling();
		String to = subNode.getTextContent();
		workItemNode.addOutMapping(
			from.substring(XmlBPMNProcessDumper.getUniqueNodeId(workItemNode).length() + 2),
			dataOutputs.get(to).substring(XmlBPMNProcessDumper.getUniqueNodeId(workItemNode).length() + 2));
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		WorkItemNode workItemNode = (WorkItemNode) node;
		writeNode("task", workItemNode, xmlDump, includeMeta);
		xmlDump.append("tns:taskName=\"" + workItemNode.getWork().getName() + "\" >" + EOL);
		writeIO(workItemNode, xmlDump);
		endNode("task", xmlDump);
	}
	
	protected void writeIO(WorkItemNode workItemNode, StringBuilder xmlDump) {
		xmlDump.append("      <ioSpecification>" + EOL);
		for (Map.Entry<String, String> entry: workItemNode.getInMappings().entrySet()) {
			xmlDump.append("        <dataInput id=\"_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "Input\" name=\"" + entry.getKey() + "\" />" + EOL);
		}
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (entry.getValue() != null) {
				xmlDump.append("        <dataInput id=\"_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "Input\" name=\"" + entry.getKey() + "\" />" + EOL);
			}
		}
		for (Map.Entry<String, String> entry: workItemNode.getOutMappings().entrySet()) {
			xmlDump.append("        <dataOutput id=\"_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "Output\" name=\"" + entry.getKey() + "\" />" + EOL);
		}
		xmlDump.append("        <inputSet>" + EOL);
		for (Map.Entry<String, String> entry: workItemNode.getInMappings().entrySet()) {
			xmlDump.append("          <dataInputRefs>_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "Input</dataInputRefs>" + EOL);
		}
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (entry.getValue() != null) {
				xmlDump.append("          <dataInputRefs>_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "Input</dataInputRefs>" + EOL);
			}
		}
		xmlDump.append(
			"        </inputSet>" + EOL);
		xmlDump.append("        <outputSet>" + EOL);
		for (Map.Entry<String, String> entry: workItemNode.getOutMappings().entrySet()) {
			xmlDump.append("          <dataOutputRefs>_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "Output</dataOutputRefs>" + EOL);
		}
		xmlDump.append(
			"        </outputSet>" + EOL);
		xmlDump.append(
			"      </ioSpecification>" + EOL);
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (entry.getValue() != null) {
				xmlDump.append(
					"      <property id=\"_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "\" />" + EOL);
			}
		}
		for (Map.Entry<String, String> entry: workItemNode.getInMappings().entrySet()) {
			xmlDump.append("      <dataInputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getValue() + "</sourceRef>" + EOL +
				"        <targetRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "</targetRef>" + EOL);
			xmlDump.append("      </dataInputAssociation>" + EOL);
		}
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (entry.getValue() != null) {
				xmlDump.append("      <dataInputAssociation>" + EOL);
				xmlDump.append(
					"        <assignment>" + EOL +
					"          <from xs:type=\"tFormalExpression\">" + entry.getValue().toString() + "</from>" + EOL +
					"          <to xs:type=\"tFormalExpression\">_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "Input</to>" + EOL +
					"        </assignment>" + EOL +
					"        <sourceRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "</sourceRef>" + EOL +
					"        <targetRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "Input</targetRef>" + EOL);
				xmlDump.append("      </dataInputAssociation>" + EOL);
			}
		}
		for (Map.Entry<String, String> entry: workItemNode.getOutMappings().entrySet()) {
			xmlDump.append("      <dataOutputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "</sourceRef>" + EOL +
				"        <targetRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getValue() + "</targetRef>" + EOL);
			xmlDump.append("      </dataOutputAssociation>" + EOL);
		}
	}

}
