package org.drools.bpmn2.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.process.core.Work;
import org.drools.process.core.impl.WorkImpl;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.HumanTaskNode;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class UserTaskHandler extends TaskHandler {
    
    protected Node createNode(Attributes attrs) {
        return new HumanTaskNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return HumanTaskNode.class;
    }

    protected void handleNode(final Node node, final Element element, final String uri, 
            final String localName, final ExtensibleXmlParser parser) throws SAXException {
    	super.handleNode(node, element, uri, localName, parser);
    	HumanTaskNode humanTaskNode = (HumanTaskNode) node;
        Work work = new WorkImpl();
        work.setName("Human Task");
    	humanTaskNode.setWork(work);
    	Map<String, String> dataInputs = new HashMap<String, String>();
    	Map<String, String> dataOutputs = new HashMap<String, String>();
    	List<String> owners = new ArrayList<String>();
    	org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
        	String nodeName = xmlNode.getNodeName();
        	if ("ioSpecification".equals(nodeName)) {
        		readIoSpecification(xmlNode, dataInputs, dataOutputs);
        	} else if ("dataInputAssociation".equals(nodeName)) {
        		readDataInputAssociation(xmlNode, humanTaskNode, dataInputs);
        	} else if ("dataOutputAssociation".equals(nodeName)) {
        		readDataOutputAssociation(xmlNode, humanTaskNode, dataOutputs);
        	} else if ("potentialOwner".equals(nodeName)) {
        		owners.add(readPotentialOwner(xmlNode, humanTaskNode));
        	}
    		xmlNode = xmlNode.getNextSibling();
        }
        if (owners.size() > 0) {
        	String owner = owners.get(0);
        	for (int i = 1; i < owners.size(); i++) {
        		owner += "," + owners.get(i);
        	}
        	humanTaskNode.getWork().setParameter("ActorId", owner);        	
        }
    }
    
    protected String readPotentialOwner(org.w3c.dom.Node xmlNode, HumanTaskNode humanTaskNode) {
		return xmlNode.getFirstChild().getFirstChild().getFirstChild().getTextContent();
    }
    
	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		HumanTaskNode humanTaskNode = (HumanTaskNode) node;
		writeNode("userTask", humanTaskNode, xmlDump, includeMeta);
		xmlDump.append(">" + EOL);
		writeIO(humanTaskNode, xmlDump);
		String ownerString = (String) humanTaskNode.getWork().getParameter("ActorId");
		if (ownerString != null) {
			String[] owners = ownerString.split(",");
			for (String owner: owners) {
				xmlDump.append(
					"      <potentialOwner resourceRef=\"tns:Actor\" >" + EOL +
					"        <resourceAssignmentExpression>" + EOL +
					"          <formalExpression>" + owner + "</formalExpression>" + EOL +
					"        </resourceAssignmentExpression>" + EOL +
					"      </potentialOwner>" + EOL);
			}
		}
		endNode("userTask", xmlDump);
	}

	protected void writeIO(WorkItemNode workItemNode, StringBuilder xmlDump) {
		xmlDump.append("      <ioSpecification>" + EOL);
		for (Map.Entry<String, String> entry: workItemNode.getInMappings().entrySet()) {
			xmlDump.append("        <dataInput id=\"" + workItemNode.getName() + "_" + entry.getKey() + "Input\" name=\"" + entry.getKey() + "\" />" + EOL);
		}
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (!"ActorId".equals(entry.getKey()) && entry.getValue() != null) {
				xmlDump.append("        <dataInput id=\"" + workItemNode.getName() + "_" + entry.getKey() + "Input\" name=\"" + entry.getKey() + "\" />" + EOL);
			}
		}
		for (Map.Entry<String, String> entry: workItemNode.getOutMappings().entrySet()) {
			xmlDump.append("        <dataOutput id=\"" + workItemNode.getName() + "_" + entry.getKey() + "Output\" name=\"" + entry.getKey() + "\" />" + EOL);
		}
		xmlDump.append("        <inputSet>" + EOL);
		for (Map.Entry<String, String> entry: workItemNode.getInMappings().entrySet()) {
			xmlDump.append("          <dataInputRefs>" + workItemNode.getName() + "_" + entry.getKey() + "Input</dataInputRefs>" + EOL);
		}
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (!"ActorId".equals(entry.getKey()) && entry.getValue() != null) {
				xmlDump.append("          <dataInputRefs>" + workItemNode.getName() + "_" + entry.getKey() + "Input</dataInputRefs>" + EOL);
			}
		}
		xmlDump.append(
			"        </inputSet>" + EOL);
		xmlDump.append("        <outputSet>" + EOL);
		for (Map.Entry<String, String> entry: workItemNode.getOutMappings().entrySet()) {
			xmlDump.append("          <dataOutputRefs>" + workItemNode.getName() + "_" + entry.getKey() + "Output</dataOutputRefs>" + EOL);
		}
		xmlDump.append(
			"        </outputSet>" + EOL);
		xmlDump.append(
			"      </ioSpecification>" + EOL);
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (!"ActorId".equals(entry.getKey()) && entry.getValue() != null) {
				xmlDump.append(
					"      <property id=\"" + workItemNode.getName() + "_" + entry.getKey() + "\" />" + EOL);
			}
		}
		for (Map.Entry<String, String> entry: workItemNode.getInMappings().entrySet()) {
			xmlDump.append("      <dataInputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>" + workItemNode.getName() + "_" + entry.getValue() + "</sourceRef>" + EOL +
				"        <targetRef>" + workItemNode.getName() + "_" + entry.getKey() + "</targetRef>" + EOL);
			xmlDump.append("      </dataInputAssociation>" + EOL);
		}
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (!"ActorId".equals(entry.getKey()) && entry.getValue() != null) {
				xmlDump.append("      <dataInputAssociation>" + EOL);
				xmlDump.append(
					"        <assignment>" + EOL +
					"          <from xs:type=\"tFormalExpression\">" + entry.getValue().toString() + "</from>" + EOL +
					"          <to xs:type=\"tFormalExpression\">" + workItemNode.getName() + "_" + entry.getKey() + "Input</to>" + EOL +
					"        </assignment>" + EOL +
					"        <sourceRef>" + workItemNode.getName() + "_" + entry.getKey() + "</sourceRef>" + EOL +
					"        <targetRef>" + workItemNode.getName() + "_" + entry.getKey() + "Input</targetRef>" + EOL);
				xmlDump.append("      </dataInputAssociation>" + EOL);
			}
		}
		for (Map.Entry<String, String> entry: workItemNode.getOutMappings().entrySet()) {
			xmlDump.append("      <dataOutputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>" + workItemNode.getName() + "_" + entry.getKey() + "</sourceRef>" + EOL +
				"        <targetRef>" + workItemNode.getName() + "_" + entry.getValue() + "</targetRef>" + EOL);
			xmlDump.append("      </dataOutputAssociation>" + EOL);
		}
	}

}
