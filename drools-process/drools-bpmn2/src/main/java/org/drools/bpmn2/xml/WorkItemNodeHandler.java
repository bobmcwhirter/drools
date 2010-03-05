package org.drools.bpmn2.xml;

import java.util.Map;

import org.drools.compiler.xml.XmlDumper;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.WorkItemNode;
import org.xml.sax.Attributes;

public class WorkItemNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        throw new IllegalArgumentException("Reading in should be handled by specific handlers");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return WorkItemNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		WorkItemNode workItemNode = (WorkItemNode) node;
		String type = workItemNode.getWork().getName();
		if ("Manual Task".equals(type)) {
		    writeNode("manualTask", workItemNode, xmlDump, includeMeta);
	        endNode(xmlDump);
	        return;
		} 
        if ("Service Task".equals(type)) {
            writeNode("serviceTask", workItemNode, xmlDump, includeMeta);
            xmlDump.append("operationRef=\"" + 
                XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_ServiceOperation\" implementation=\"Other\" >" + EOL);
            xmlDump.append(
                "      <ioSpecification>" + EOL +
                "        <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_param\" name=\"Parameter\" />" + EOL +
                "        <dataOutput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_result\" name=\"Result\" />" + EOL +
                "        <inputSet>" + EOL +
                "          <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_param</dataInputRefs>" + EOL +
                "        </inputSet>" + EOL +
                "        <outputSet>" + EOL +
                "          <dataOutputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_result</dataOutputRefs>" + EOL +
                "        </outputSet>" + EOL +
                "      </ioSpecification>" + EOL);
            String inMapping = workItemNode.getInMapping("Parameter");
            if (inMapping != null) {
                xmlDump.append(
                    "      <dataInputAssociation>" + EOL +
                    "        <sourceRef>" + XmlDumper.replaceIllegalChars(inMapping) + "</sourceRef>" + EOL +
                    "        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_param</targetRef>" + EOL +
                    "      </dataInputAssociation>" + EOL);
            }
            String outMapping = workItemNode.getOutMapping("Result");
            if (outMapping != null) {
                xmlDump.append(
                    "      <dataOutputAssociation>" + EOL +
                    "        <sourceRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_result</sourceRef>" + EOL +
                    "        <targetRef>" + XmlDumper.replaceIllegalChars(outMapping) + "</targetRef>" + EOL +
                    "      </dataOutputAssociation>" + EOL);
            }
            endNode("serviceTask", xmlDump);
            return;
        } 
        if ("Send Task".equals(type)) {
            writeNode("sendTask", workItemNode, xmlDump, includeMeta);
            xmlDump.append("messageRef=\"" + 
                    XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_Message\" implementation=\"Other\" >" + EOL);
                xmlDump.append(
                    "      <ioSpecification>" + EOL +
                    "        <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_param\" name=\"Message\" />" + EOL +
                    "        <inputSet>" + EOL +
                    "          <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_param</dataInputRefs>" + EOL +
                    "        </inputSet>" + EOL +
                    "        <outputSet/>" + EOL +
                    "      </ioSpecification>" + EOL);
                String inMapping = workItemNode.getInMapping("Message");
                if (inMapping != null) {
                    xmlDump.append(
                        "      <dataInputAssociation>" + EOL +
                        "        <sourceRef>" + XmlDumper.replaceIllegalChars(inMapping) + "</sourceRef>" + EOL +
                        "        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_param</targetRef>" + EOL +
                        "      </dataInputAssociation>" + EOL);
                }
            endNode("sendTask", xmlDump);
            return;
        } 
        if ("Receive Task".equals(type)) {
            writeNode("receiveTask", workItemNode, xmlDump, includeMeta);
            xmlDump.append("messageRef=\"" + 
                    XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_Message\" implementation=\"Other\" >" + EOL);
                xmlDump.append(
                    "      <ioSpecification>" + EOL +
                    "        <dataOutput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_result\" name=\"Message\" />" + EOL +
                    "        <inputSet/>" + EOL +
                    "        <outputSet>" + EOL +
                    "          <dataOutputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_result</dataOutputRefs>" + EOL +
                    "        </outputSet>" + EOL +
                    "      </ioSpecification>" + EOL);
                String outMapping = workItemNode.getOutMapping("Message");
                if (outMapping != null) {
                    xmlDump.append(
                        "      <dataOutputAssociation>" + EOL +
                        "        <sourceRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_result</sourceRef>" + EOL +
                        "        <targetRef>" + XmlDumper.replaceIllegalChars(outMapping) + "</targetRef>" + EOL +
                        "      </dataOutputAssociation>" + EOL);
                }
            endNode("receiveTask", xmlDump);
            return;
        } 
		writeNode("task", workItemNode, xmlDump, includeMeta);
		xmlDump.append("tns:taskName=\"" + XmlDumper.replaceIllegalChars(type) + "\" >" + EOL);
		writeIO(workItemNode, xmlDump);
		endNode("task", xmlDump);
	}
	
	protected void writeIO(WorkItemNode workItemNode, StringBuilder xmlDump) {
		xmlDump.append("      <ioSpecification>" + EOL);
		for (Map.Entry<String, String> entry: workItemNode.getInMappings().entrySet()) {
			xmlDump.append("        <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input\" name=\"" + XmlDumper.replaceIllegalChars(entry.getKey()) + "\" />" + EOL);
		}
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (entry.getValue() != null) {
				xmlDump.append("        <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input\" name=\"" + XmlDumper.replaceIllegalChars(entry.getKey()) + "\" />" + EOL);
			}
		}
		for (Map.Entry<String, String> entry: workItemNode.getOutMappings().entrySet()) {
			xmlDump.append("        <dataOutput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Output\" name=\"" + XmlDumper.replaceIllegalChars(entry.getKey()) + "\" />" + EOL);
		}
		xmlDump.append("        <inputSet>" + EOL);
		for (Map.Entry<String, String> entry: workItemNode.getInMappings().entrySet()) {
			xmlDump.append("          <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</dataInputRefs>" + EOL);
		}
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (entry.getValue() != null) {
				xmlDump.append("          <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</dataInputRefs>" + EOL);
			}
		}
		xmlDump.append(
			"        </inputSet>" + EOL);
		xmlDump.append("        <outputSet>" + EOL);
		for (Map.Entry<String, String> entry: workItemNode.getOutMappings().entrySet()) {
			xmlDump.append("          <dataOutputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Output</dataOutputRefs>" + EOL);
		}
		xmlDump.append(
			"        </outputSet>" + EOL);
		xmlDump.append(
			"      </ioSpecification>" + EOL);
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (entry.getValue() != null) {
				xmlDump.append(
					"      <property id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "\" />" + EOL);
			}
		}
		writeInputAssociation(workItemNode, xmlDump);
        writeOutputAssociation(workItemNode, xmlDump);
	}
	
	protected void writeInputAssociation(WorkItemNode workItemNode, StringBuilder xmlDump) {
		for (Map.Entry<String, String> entry: workItemNode.getInMappings().entrySet()) {
			xmlDump.append("      <dataInputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>" + XmlDumper.replaceIllegalChars(entry.getValue()) + "</sourceRef>" + EOL +
				"        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</targetRef>" + EOL);
			xmlDump.append("      </dataInputAssociation>" + EOL);
		}
		for (Map.Entry<String, Object> entry: workItemNode.getWork().getParameters().entrySet()) {
			if (entry.getValue() != null) {
				xmlDump.append("      <dataInputAssociation>" + EOL);
				xmlDump.append(
					"        <assignment>" + EOL +
					"          <from xs:type=\"tFormalExpression\">" + XmlDumper.replaceIllegalChars(entry.getValue().toString()) + "</from>" + EOL +
					"          <to xs:type=\"tFormalExpression\">" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</to>" + EOL +
					"        </assignment>" + EOL +
					"        <sourceRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "</sourceRef>" + EOL +
					"        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + XmlDumper.replaceIllegalChars(entry.getKey()) + "Input</targetRef>" + EOL);
				xmlDump.append("      </dataInputAssociation>" + EOL);
			}
		}
	}
	
    protected void writeOutputAssociation(WorkItemNode workItemNode, StringBuilder xmlDump) {
		for (Map.Entry<String, String> entry: workItemNode.getOutMappings().entrySet()) {
			xmlDump.append("      <dataOutputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "Output</sourceRef>" + EOL +
				"        <targetRef>" + XmlDumper.replaceIllegalChars(entry.getValue()) + "</targetRef>" + EOL);
			xmlDump.append("      </dataOutputAssociation>" + EOL);
		}
	}

}
