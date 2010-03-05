package org.drools.bpmn2.xml;

import java.util.List;

import org.drools.compiler.xml.XmlDumper;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.EndNode;
import org.xml.sax.Attributes;

public class EndNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        throw new IllegalArgumentException("Reading in should be handled by end event handler");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return EndNode.class;
    }

    public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		EndNode endNode = (EndNode) node;
		writeNode("endEvent", endNode, xmlDump, includeMeta);
		if (endNode.isTerminate()) {
    		xmlDump.append(">" + EOL);
            xmlDump.append("        <terminateEventDefinition/>" + EOL);
    		endNode("endEvent", xmlDump);
		} else {
		    List<DroolsAction> actions = endNode.getActions(EndNode.EVENT_NODE_ENTER);
		    if (actions != null && !actions.isEmpty()) {
		        if (actions.size() == 1) {
		            DroolsConsequenceAction action = (DroolsConsequenceAction) actions.get(0);
		            String s = action.getConsequence();
		            if (s.startsWith("org.drools.process.instance.impl.WorkItemImpl workItem = new org.drools.process.instance.impl.WorkItemImpl();")) {
		                xmlDump.append(">" + EOL);
                        String variable = (String) endNode.getMetaData("MappingVariable");
                        if (variable != null) {
                            xmlDump.append(
                                "      <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(endNode) + "_Input\" />" + EOL + 
                                "      <dataInputAssociation>" + EOL + 
                                "        <sourceRef>" + XmlDumper.replaceIllegalChars(variable) + "</sourceRef>" + EOL + 
                                "        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(endNode) + "_Input</targetRef>" + EOL + 
                                "      </dataInputAssociation>" + EOL + 
                                "      <inputSet>" + EOL + 
                                "        <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(endNode) + "_Input</dataInputRefs>" + EOL + 
                                "      </inputSet>" + EOL);
                        }
                        xmlDump.append("      <messageEventDefinition messageRef=\"" + XmlBPMNProcessDumper.getUniqueNodeId(endNode) + "_Message\"/>" + EOL);
                        endNode("endEvent", xmlDump);
		            } else if (s.startsWith("kcontext.getKnowledgeRuntime().signalEvent(\"")) {
                        xmlDump.append(">" + EOL);
		                s = s.substring(44);
		                String type = s.substring(0, s.indexOf("\""));
		                s = s.substring(s.indexOf(",") + 2);
		                String variable = null;
		                if (!s.startsWith("null")) {
		                    variable = s.substring(0, s.indexOf(")"));
	                        xmlDump.append(
                                "      <dataInput id=\"" + XmlBPMNProcessDumper.getUniqueNodeId(endNode) + "_Input\" />" + EOL + 
                                "      <dataInputAssociation>" + EOL + 
                                "        <sourceRef>" + XmlDumper.replaceIllegalChars(variable) + "</sourceRef>" + EOL + 
                                "        <targetRef>" + XmlBPMNProcessDumper.getUniqueNodeId(endNode) + "_Input</targetRef>" + EOL + 
                                "      </dataInputAssociation>" + EOL + 
                                "      <inputSet>" + EOL + 
                                "        <dataInputRefs>" + XmlBPMNProcessDumper.getUniqueNodeId(endNode) + "_Input</dataInputRefs>" + EOL + 
                                "      </inputSet>" + EOL);
	                    }
		                if (type.startsWith("Compensate-")) {
			                xmlDump.append("      <compensateEventDefinition activityRef=\"" + XmlDumper.replaceIllegalChars(type.substring(11)) + "\"/>" + EOL);
		                } else {
		                	xmlDump.append("      <signalEventDefinition signalRef=\"" + XmlDumper.replaceIllegalChars(type) + "\"/>" + EOL);
		                }
		                endNode("endEvent", xmlDump);
		            } else if (s.startsWith("kcontext.getProcessInstance().signalEvent(\"")) {
		            	xmlDump.append(">" + EOL);
		            	s = s.substring(43);
		                String type = s.substring(0, s.indexOf("\""));
		                xmlDump.append("      <compensateEventDefinition activityRef=\"" + XmlDumper.replaceIllegalChars(type.substring(11)) + "\"/>" + EOL);
		                endNode("endEvent", xmlDump);
		            } else {
		                throw new IllegalArgumentException("Unknown action " + s);
		            }
		        }
		    } else {
		        endNode(xmlDump);
		    }
		}
	}

}
