package org.drools.bpmn2.xml;

import java.util.List;

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
		            if (s.startsWith("kcontext.getKnowledgeRuntime().signalEvent(\"")) {
                        xmlDump.append(">" + EOL);
		                s = s.substring(44);
		                String type = s.substring(0, s.indexOf("\""));
		                s = s.substring(s.indexOf(",") + 2);
		                String variable = null;
		                if (!s.startsWith("null")) {
		                    variable = s.substring(0, s.indexOf(")"));
	                        xmlDump.append(
                                "      <dataInput id=\"_" + endNode.getUniqueId() + "_Input\" />" + EOL + 
                                "      <dataInputAssociation>" + EOL + 
                                "        <sourceRef>" + variable + "</sourceRef>" + EOL + 
                                "        <targetRef>_" + endNode.getUniqueId() + "_Input</targetRef>" + EOL + 
                                "      </dataInputAssociation>" + EOL + 
                                "      <inputSet>" + EOL + 
                                "        <dataInputRefs>_" + endNode.getUniqueId() + "_Input</dataInputRefs>" + EOL + 
                                "      </inputSet>" + EOL);
	                    }
		                xmlDump.append("      <signalEventDefinition signalRef=\"" + type + "\"/>" + EOL);
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
