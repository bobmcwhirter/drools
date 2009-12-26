package org.drools.bpmn2.xml;

import java.util.Map;

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
            xmlDump.append("operationRef=\"_" + 
                XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_ServiceOperation\" implementation=\"Other\" >" + EOL);
            xmlDump.append(
                "      <ioSpecification>" + EOL +
                "        <dataInput id=\"_2_param\" name=\"Parameter\" />" + EOL +
                "        <dataOutput id=\"_2_result\" name=\"Result\" />" + EOL +
                "        <inputSet>" + EOL +
                "          <dataInputRefs>_2_param</dataInputRefs>" + EOL +
                "        </inputSet>" + EOL +
                "        <outputSet>" + EOL +
                "          <dataOutputRefs>_2_result</dataOutputRefs>" + EOL +
                "        </outputSet>" + EOL +
                "      </ioSpecification>" + EOL);
            String inMapping = workItemNode.getInMapping("Parameter");
            if (inMapping != null) {
                xmlDump.append(
                    "      <dataInputAssociation>" + EOL +
                    "        <sourceRef>" + inMapping + "</sourceRef>" + EOL +
                    "        <targetRef>_2_param</targetRef>" + EOL +
                    "      </dataInputAssociation>" + EOL);
            }
            String outMapping = workItemNode.getOutMapping("Result");
            if (outMapping != null) {
                xmlDump.append(
                    "      <dataOutputAssociation>" + EOL +
                    "        <sourceRef>_2_result</sourceRef>" + EOL +
                    "        <targetRef>" + outMapping + "</targetRef>" + EOL +
                    "      </dataOutputAssociation>" + EOL);
            }
            endNode("serviceTask", xmlDump);
            return;
        } 
        if ("Send Task".equals(type)) {
            writeNode("sendTask", workItemNode, xmlDump, includeMeta);
            xmlDump.append("messageRef=\"_" + 
                    XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_Message\" implementation=\"Other\" >" + EOL);
                xmlDump.append(
                    "      <ioSpecification>" + EOL +
                    "        <dataInput id=\"_2_param\" name=\"Message\" />" + EOL +
                    "        <inputSet>" + EOL +
                    "          <dataInputRefs>_2_param</dataInputRefs>" + EOL +
                    "        </inputSet>" + EOL +
                    "        <outputSet/>" + EOL +
                    "      </ioSpecification>" + EOL);
                String inMapping = workItemNode.getInMapping("Message");
                if (inMapping != null) {
                    xmlDump.append(
                        "      <dataInputAssociation>" + EOL +
                        "        <sourceRef>" + inMapping + "</sourceRef>" + EOL +
                        "        <targetRef>_2_param</targetRef>" + EOL +
                        "      </dataInputAssociation>" + EOL);
                }
            endNode("sendTask", xmlDump);
            return;
        } 
        if ("Receive Task".equals(type)) {
            writeNode("receiveTask", workItemNode, xmlDump, includeMeta);
            xmlDump.append("messageRef=\"_" + 
                    XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_Message\" implementation=\"Other\" >" + EOL);
                xmlDump.append(
                    "      <ioSpecification>" + EOL +
                    "        <dataOutput id=\"_2_result\" name=\"Message\" />" + EOL +
                    "        <inputSet/>" + EOL +
                    "        <outputSet>" + EOL +
                    "          <dataOutputRefs>_2_result</dataOutputRefs>" + EOL +
                    "        </outputSet>" + EOL +
                    "      </ioSpecification>" + EOL);
                String outMapping = workItemNode.getOutMapping("Message");
                if (outMapping != null) {
                    xmlDump.append(
                        "      <dataOutputAssociation>" + EOL +
                        "        <sourceRef>_2_result</sourceRef>" + EOL +
                        "        <targetRef>" + outMapping + "</targetRef>" + EOL +
                        "      </dataOutputAssociation>" + EOL);
                }
            endNode("receiveTask", xmlDump);
            return;
        } 
		writeNode("task", workItemNode, xmlDump, includeMeta);
		xmlDump.append("tns:taskName=\"" + type + "\" >" + EOL);
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
		writeInputAssociation(workItemNode, xmlDump);
        writeOutputAssociation(workItemNode, xmlDump);
	}
	
	protected void writeInputAssociation(WorkItemNode workItemNode, StringBuilder xmlDump) {
		for (Map.Entry<String, String> entry: workItemNode.getInMappings().entrySet()) {
			xmlDump.append("      <dataInputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>" + entry.getValue() + "</sourceRef>" + EOL +
				"        <targetRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "Input</targetRef>" + EOL);
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
	}
	
    protected void writeOutputAssociation(WorkItemNode workItemNode, StringBuilder xmlDump) {
		for (Map.Entry<String, String> entry: workItemNode.getOutMappings().entrySet()) {
			xmlDump.append("      <dataOutputAssociation>" + EOL);
			xmlDump.append(
				"        <sourceRef>_" + XmlBPMNProcessDumper.getUniqueNodeId(workItemNode) + "_" + entry.getKey() + "Output</sourceRef>" + EOL +
				"        <targetRef>" + entry.getValue() + "</targetRef>" + EOL);
			xmlDump.append("      </dataOutputAssociation>" + EOL);
		}
	}

}
