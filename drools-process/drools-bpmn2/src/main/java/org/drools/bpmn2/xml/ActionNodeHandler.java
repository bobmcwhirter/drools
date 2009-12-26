package org.drools.bpmn2.xml;

import org.drools.rule.builder.dialect.java.JavaDialect;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;
import org.drools.xml.XmlDumper;
import org.xml.sax.Attributes;

public class ActionNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        throw new IllegalArgumentException("Reading in should be handled by specific handlers");
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return ActionNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		ActionNode actionNode = (ActionNode) node;
		DroolsConsequenceAction action = (DroolsConsequenceAction) actionNode.getAction();
		if (action != null) {
		    String s = action.getConsequence();
		    if ("".equals(s)) {
		        writeNode("intermediateThrowEvent", actionNode, xmlDump, includeMeta);
		        endNode(xmlDump);
		    } else if (s.startsWith("kcontext.getKnowledgeRuntime().signalEvent(\"")) {
                writeNode("intermediateThrowEvent", actionNode, xmlDump, includeMeta);
                xmlDump.append(">" + EOL);
                s = s.substring(44);
                String type = s.substring(0, s.indexOf("\""));
                s = s.substring(s.indexOf(",") + 2);
                String variable = null;
                if (!s.startsWith("null")) {
                    variable = s.substring(0, s.indexOf(")"));
                    xmlDump.append(
                        "      <dataInput id=\"_" + actionNode.getUniqueId() + "_Input\" />" + EOL + 
                        "      <dataInputAssociation>" + EOL + 
                        "        <sourceRef>" + variable + "</sourceRef>" + EOL + 
                        "        <targetRef>_" + actionNode.getUniqueId() + "_Input</targetRef>" + EOL + 
                        "      </dataInputAssociation>" + EOL + 
                        "      <inputSet>" + EOL + 
                        "        <dataInputRefs>_" + actionNode.getUniqueId() + "_Input</dataInputRefs>" + EOL + 
                        "      </inputSet>" + EOL);
                }
                xmlDump.append("      <signalEventDefinition signalRef=\"" + type + "\"/>" + EOL);
                endNode("intermediateThrowEvent", xmlDump);
            } else {
                writeNode("scriptTask", actionNode, xmlDump, includeMeta);
                if (JavaDialect.ID.equals(action.getDialect())) {
                    xmlDump.append("scriptLanguage=\"" + XmlBPMNProcessDumper.JAVA_LANGUAGE + "\" ");
                }
                if (action.getConsequence() != null) {
                    xmlDump.append(">" + EOL + 
                        "      <script>" + XmlDumper.replaceIllegalChars(action.getConsequence()) + "</script>" + EOL);
                    endNode("scriptTask", xmlDump);
                } else {
                    endNode(xmlDump);
                }
            }
		} else {
		    writeNode("scriptTask", actionNode, xmlDump, includeMeta);
	        endNode(xmlDump);
		}
	}

}
