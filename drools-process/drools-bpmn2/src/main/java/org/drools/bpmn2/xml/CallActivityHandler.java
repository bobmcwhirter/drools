package org.drools.bpmn2.xml;

import java.util.HashMap;
import java.util.Map;

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
		String waitForCompletion = element.getAttribute("waitForCompletion");
		if (waitForCompletion != null && "false".equals(waitForCompletion)) {
			subProcessNode.setWaitForCompletion(false);
		}
		String independent = element.getAttribute("independent");
		if (independent != null && "false".equals(independent)) {
			subProcessNode.setIndependent(false);
		}
    	Map<String, String> dataInputs = new HashMap<String, String>();
    	Map<String, String> dataOutputs = new HashMap<String, String>();
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
        	String nodeName = xmlNode.getNodeName();
        	if ("ioSpecification".equals(nodeName)) {
        		readIoSpecification(xmlNode, dataInputs, dataOutputs);
        	} else if ("dataInputAssociation".equals(nodeName)) {
        		readDataInputAssociation(xmlNode, subProcessNode, dataInputs);
        	} else if ("dataOutputAssociation".equals(nodeName)) {
        		readDataOutputAssociation(xmlNode, subProcessNode, dataOutputs);
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
        	} else if ("dataOutput".equals(subNodeName)) {
        		String id = ((Element) subNode).getAttribute("id");
        		String outputName = ((Element) subNode).getAttribute("name");
        		dataOutputs.put(id, outputName);
        	}
        	subNode = subNode.getNextSibling();
		}
    }
    
    protected void readDataInputAssociation(org.w3c.dom.Node xmlNode, SubProcessNode subProcessNode, Map<String, String> dataInputs) {
		// sourceRef
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		String from = subNode.getTextContent();
		// targetRef
		subNode = subNode.getNextSibling();
		String to = subNode.getTextContent();
		subProcessNode.addInMapping(dataInputs.get(to), from);
    }
    
    protected void readDataOutputAssociation(org.w3c.dom.Node xmlNode, SubProcessNode subProcessNode, Map<String, String> dataOutputs) {
		// sourceRef
		org.w3c.dom.Node subNode = xmlNode.getFirstChild();
		String from = subNode.getTextContent();
		// targetRef
		subNode = subNode.getNextSibling();
		String to = subNode.getTextContent();
		subProcessNode.addOutMapping(dataOutputs.get(from), to);
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		SubProcessNode subProcessNode = (SubProcessNode) node;
		writeNode("callActivity", subProcessNode, xmlDump, includeMeta);
		if (subProcessNode.getProcessId() != null) {
			xmlDump.append("calledElement=\"" + subProcessNode.getProcessId() + "\" ");
		}
		if (!subProcessNode.isWaitForCompletion()) {
			xmlDump.append("tns:waitForCompletion=\"false\" ");
		}
		if (!subProcessNode.isIndependent()) {
			xmlDump.append("tns:independent=\"false\" ");
		}
		xmlDump.append(">" + EOL);
		writeIO(subProcessNode, xmlDump);
		endNode("callActivity", xmlDump);
	}

	protected void writeIO(SubProcessNode subProcessNode, StringBuilder xmlDump) {
		xmlDump.append("      <ioSpecification>" + EOL);
		for (Map.Entry<String, String> entry: subProcessNode.getInMappings().entrySet()) {
			xmlDump.append("        <dataInput id=\"_" + XmlBPMNProcessDumper.getUniqueNodeId(subProcessNode) + "_" + entry.getKey() + "Input\" name=\"" + entry.getKey() + "\" />" + EOL);
		}
		for (Map.Entry<String, String> entry: subProcessNode.getOutMappings().entrySet()) {
			xmlDump.append("        <dataOutput id=\"_" + XmlBPMNProcessDumper.getUniqueNodeId(subProcessNode) + "_" + entry.getKey() + "Output\" name=\"" + entry.getKey() + "\" />" + EOL);
		}
		xmlDump.append("        <inputSet>" + EOL);
		for (Map.Entry<String, String> entry: subProcessNode.getInMappings().entrySet()) {
			xmlDump.append("          <dataInputRefs>_" + XmlBPMNProcessDumper.getUniqueNodeId(subProcessNode) + "_" + entry.getKey() + "Input</dataInputRefs>" + EOL);
		}
		xmlDump.append("        </inputSet>" + EOL);
		xmlDump.append("        <outputSet>" + EOL);
		for (Map.Entry<String, String> entry: subProcessNode.getOutMappings().entrySet()) {
			xmlDump.append("          <dataOutputRefs>_" + XmlBPMNProcessDumper.getUniqueNodeId(subProcessNode) + "_" + entry.getKey() + "Output</dataOutputRefs>" + EOL);
		}
		xmlDump.append("        </outputSet>" + EOL);
		xmlDump.append("      </ioSpecification>" + EOL);
		for (Map.Entry<String, String> entry: subProcessNode.getInMappings().entrySet()) {
			xmlDump.append("      <dataInputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>" + entry.getValue() + "</sourceRef>" + EOL +
				"        <targetRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(subProcessNode) + "_" + entry.getKey() + "Input</targetRef>" + EOL);
			xmlDump.append("      </dataInputAssociation>" + EOL);
		}
		for (Map.Entry<String, String> entry: subProcessNode.getOutMappings().entrySet()) {
			xmlDump.append("      <dataOutputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(subProcessNode) + "_" + entry.getKey() + "Output</sourceRef>" + EOL +
				"        <targetRef>" + entry.getValue() + "</targetRef>" + EOL);
			xmlDump.append("      </dataOutputAssociation>" + EOL);
		}
	}

}