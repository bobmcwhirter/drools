package org.drools.bpmn2.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.bpmn2.xpath.XPathDialect;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.drools.definition.process.WorkflowProcess;
import org.drools.process.core.ContextContainer;
import org.drools.process.core.Work;
import org.drools.process.core.context.swimlane.Swimlane;
import org.drools.process.core.context.swimlane.SwimlaneContext;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.rule.builder.dialect.java.JavaDialect;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.core.node.CompositeNode;
import org.drools.workflow.core.node.HumanTaskNode;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.Handler;
import org.drools.xml.SemanticModule;
import org.drools.xml.XmlDumper;

public class XmlBPMNProcessDumper {
	
	public static final String JAVA_LANGUAGE = "http://www.java.com/java";
	public static final String RULE_LANGUAGE = "http://www.jboss.org/drools/rule";
    public static final String XPATH_LANGUAGE = "http://www.w3.org/1999/XPath";
    
	public static XmlBPMNProcessDumper INSTANCE = new XmlBPMNProcessDumper();
	
    private final static String EOL = System.getProperty( "line.separator" );
    
    private SemanticModule semanticModule;
    
    private XmlBPMNProcessDumper() {
    	semanticModule = new BPMNSemanticModule();
    }
    
    public String dump(WorkflowProcess process) {
        return dump(process, true);
    }
    
    public String dump(WorkflowProcess process, boolean includeMeta) {
        StringBuilder xmlDump = new StringBuilder();
        visitProcess(process, xmlDump, includeMeta);
        return xmlDump.toString();
    }
    
    protected void visitProcess(WorkflowProcess process, StringBuilder xmlDump, boolean includeMeta) {
        xmlDump.append(
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + EOL +
            "<definitions id=\"Definition\"" + EOL +
            "             targetNamespace=\"http://www.jboss.org/drools\"" + EOL +
            "             typeLanguage=\"http://www.java.com/javaTypes\"" + EOL +
            "             expressionLanguage=\"http://www.mvel.org/2.0\"" + EOL +
            "             xmlns=\"http://schema.omg.org/spec/BPMN/2.0\"" + EOL +
            "             xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" + EOL +
            "             xs:schemaLocation=\"http://schema.omg.org/spec/BPMN/2.0 BPMN20.xsd\"" + EOL +
            "             xmlns:g=\"http://www.jboss.org/drools/flow/gpd\"" + EOL +
            "             xmlns:tns=\"http://www.jboss.org/drools\">" + EOL + EOL);

    	// item definitions
    	VariableScope variableScope = (VariableScope)
    		((org.drools.process.core.Process) process).getDefaultContext(VariableScope.VARIABLE_SCOPE);
    	visitVariableScope(variableScope, "_", xmlDump);
    	visitSubVariableScopes(process.getNodes(), xmlDump);
        
	    xmlDump.append(
    		"  <resource id=\"Actor\" name=\"Human Actor\" />" + EOL + EOL);

	    visitInterfaces(process, xmlDump);
	       
	    // the process itself
		xmlDump.append("  <process processType=\"executable\" ");
        if (process.getId() != null) {
            xmlDump.append("id=\"" + process.getId() + "\" ");
        }
        if (process.getName() != null) {
            xmlDump.append("name=\"" + process.getName() + "\" ");
        }
        String packageName = process.getPackageName();
        if (packageName != null && !"org.drools.bpmn2".equals(packageName)) {
            xmlDump.append("tns:packageName=\"" + packageName + "\" ");
        }
        // TODO: package, version
        xmlDump.append(">" + EOL + EOL);
        visitLanes(process, xmlDump);
        visitHeader(process, xmlDump, includeMeta);
        visitNodes(process, xmlDump, includeMeta);
        visitConnections(process.getNodes(), xmlDump, includeMeta);
        xmlDump.append("  </process>" + EOL + EOL);
        xmlDump.append("</definitions>");
    }
    
    private void visitVariableScope(VariableScope variableScope, String prefix, StringBuilder xmlDump) {
        if (variableScope != null && !variableScope.getVariables().isEmpty()) {
            for (Variable variable: variableScope.getVariables()) {
                xmlDump.append(
                    "  <itemDefinition id=\"" + prefix + variable.getName() + "Item\" ");
                if (variable.getType() != null) {
                    xmlDump.append("structureRef=\"" + variable.getType().getStringType() + "\" ");
                }
                xmlDump.append("/>" + EOL);
            }
            xmlDump.append(EOL);
        }
    }
    
    private void visitSubVariableScopes(Node[] nodes, StringBuilder xmlDump) {
        for (Node node: nodes) {
            if (node instanceof ContextContainer) {
                VariableScope variableScope = (VariableScope) 
                    ((ContextContainer) node).getDefaultContext(VariableScope.VARIABLE_SCOPE);
                if (variableScope != null) {
                    visitVariableScope(variableScope, "_" + ((NodeImpl) node).getUniqueId() + "-", xmlDump);
                }
            }
            if (node instanceof NodeContainer) {
                visitSubVariableScopes(((NodeContainer) node).getNodes(), xmlDump);
            }
        }
    }
    
    private void visitLanes(WorkflowProcess process, StringBuilder xmlDump) {
        // lanes
        Collection<Swimlane> swimlanes = ((SwimlaneContext)
            ((org.drools.workflow.core.WorkflowProcess) process)
                .getDefaultContext(SwimlaneContext.SWIMLANE_SCOPE)).getSwimlanes();
        if (!swimlanes.isEmpty()) {
            xmlDump.append("    <laneSet>" + EOL);
            for (Swimlane swimlane: swimlanes) {
                xmlDump.append("      <lane name=\"" + swimlane.getName() + "\" >" + EOL);
                visitLane(process, swimlane.getName(), xmlDump);
                xmlDump.append("      </lane>" + EOL);
            }
            xmlDump.append("    </laneSet>" + EOL);
        }
    }
    
    private void visitLane(NodeContainer container, String lane, StringBuilder xmlDump) {
        for (Node node: container.getNodes()) {
            if (node instanceof HumanTaskNode) {
                String swimlane = ((HumanTaskNode) node).getSwimlane();
                if (lane.equals(swimlane)) {
                    xmlDump.append("        <flowElementRef>_" + ((NodeImpl) node).getUniqueId() + "</flowElementRef>" + EOL);
                }
            } else {
                String swimlane = (String) node.getMetaData("Lane");
                if (lane.equals(swimlane)) {
                    xmlDump.append("        <flowElementRef>_" + ((NodeImpl) node).getUniqueId() + "</flowElementRef>" + EOL);
                }
            }
            if (node instanceof NodeContainer) {
                visitLane((NodeContainer) node, lane, xmlDump);
            }
        }
    }
    
    protected void visitHeader(WorkflowProcess process, StringBuilder xmlDump, boolean includeMeta) {
    	// TODO: imports, function imports
    	// TODO: globals
        // TODO: swimlanes
    	// TODO: exception handlers
        VariableScope variableScope = (VariableScope)
        	((org.drools.process.core.Process) process).getDefaultContext(VariableScope.VARIABLE_SCOPE);
        if (variableScope != null) {
            visitVariables(variableScope.getVariables(), xmlDump);
        }
    }
    
    public static void visitVariables(List<Variable> variables, StringBuilder xmlDump) {
    	if (!variables.isEmpty()) {
            xmlDump.append("    <!-- process variables -->" + EOL);
            for (Variable variable: variables) {
                if (variable.getMetaData("DataObject") == null) {
                    xmlDump.append("    <property id=\"" + variable.getName() + "\" ");
                    if (variable.getType() != null) {
                    	xmlDump.append("itemSubjectRef=\"_" + variable.getName() + "Item\"" );
                    }
                    // TODO: value?
                    xmlDump.append("/>" + EOL);
                }
            }
            for (Variable variable: variables) {
                if (variable.getMetaData("DataObject") != null) {
                    xmlDump.append("    <dataObject id=\"" + variable.getName() + "\" ");
                    if (variable.getType() != null) {
                        xmlDump.append("itemSubjectRef=\"_" + variable.getName() + "Item\"" );
                    }
                    // TODO: value?
                    xmlDump.append("/>" + EOL);
                }
            }
            xmlDump.append(EOL);
    	}
    }
    
    protected void visitInterfaces(WorkflowProcess process, StringBuilder xmlDump) {
        for (Node node: process.getNodes()) {
            if (node instanceof WorkItemNode) {
                Work work = ((WorkItemNode) node).getWork();
                if (work != null) {
                    if ("Service Task".equals(work.getName())) {
                        String interfaceName = (String) work.getParameter("Interface");
                        if (interfaceName == null) {
                            interfaceName = "";
                        }
                        String operationName = (String) work.getParameter("Operation");
                        if (operationName == null) {
                            operationName = "";
                        }
                        String parameterType = (String) work.getParameter("ParameterType");
                        if (parameterType == null) {
                            parameterType = "";
                        }
                        xmlDump.append(
                            "  <itemDefinition id=\"_" + getUniqueNodeId(node) + "_InMessageType\" structureRef=\"" + parameterType + "\"/>" + EOL +
                            "  <message id=\"_" + getUniqueNodeId(node) + "_InMessage\" structureRef=\"_" + getUniqueNodeId(node) + "_InMessageType\" />" + EOL +
                            "  <interface id=\"_" + getUniqueNodeId(node) + "_ServiceInterface\" name=\"" + interfaceName + "\">" + EOL +
                            "    <operation id=\"_" + getUniqueNodeId(node) + "_ServiceOperation\" name=\"" + operationName + "\">" + EOL + 
                            "      <inMessageRef>_" + getUniqueNodeId(node) + "_InMessage</inMessageRef>" + EOL +
                            "    </operation>" + EOL +
                            "  </interface>" + EOL + EOL);
                    } else if ("Send Task".equals(work.getName())) {
                        String messageType = (String) work.getParameter("MessageType");
                        if (messageType == null) {
                            messageType = "";
                        }
                        xmlDump.append(
                            "  <itemDefinition id=\"_" + getUniqueNodeId(node) + "_MessageType\" structureRef=\"" + messageType + "\"/>" + EOL +
                            "  <message id=\"_" + getUniqueNodeId(node) + "_Message\" structureRef=\"_" + getUniqueNodeId(node) + "_MessageType\" />" + EOL + EOL);
                    } else if ("Receive Task".equals(work.getName())) {
                        String messageType = (String) work.getParameter("MessageType");
                        if (messageType == null) {
                            messageType = "";
                        }
                        xmlDump.append(
                            "  <itemDefinition id=\"_" + getUniqueNodeId(node) + "_MessageType\" structureRef=\"" + messageType + "\"/>" + EOL +
                            "  <message id=\"_" + getUniqueNodeId(node) + "_Message\" structureRef=\"_" + getUniqueNodeId(node) + "_MessageType\" />" + EOL + EOL);
                    }
                }
            }
        }
    }
    
    private void visitNodes(WorkflowProcess process, StringBuilder xmlDump, boolean includeMeta) {
    	xmlDump.append("    <!-- nodes -->" + EOL);
        for (Node node: process.getNodes()) {
            visitNode(node, xmlDump, includeMeta);
        }
        xmlDump.append(EOL);
    }
    
    public void visitNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
     	Handler handler = semanticModule.getHandlerByClass(node.getClass());
        if (handler != null) {
        	((AbstractNodeHandler) handler).writeNode((org.drools.workflow.core.Node) node, xmlDump, includeMeta);
        } else {
        	throw new IllegalArgumentException(
                "Unknown node type: " + node);
        }
    }
    
    private void visitConnections(Node[] nodes, StringBuilder xmlDump, boolean includeMeta) {
    	xmlDump.append("    <!-- connections -->" + EOL);
        List<Connection> connections = new ArrayList<Connection>();
        for (Node node: nodes) {
            for (List<Connection> connectionList: node.getIncomingConnections().values()) {
                connections.addAll(connectionList);
            }
        }
        for (Connection connection: connections) {
            visitConnection(connection, xmlDump, includeMeta);
        }
        xmlDump.append(EOL);
    }
    
    public void visitConnection(Connection connection, StringBuilder xmlDump, boolean includeMeta) {
        xmlDump.append("    <sequenceFlow sourceRef=\"_" + getUniqueNodeId(connection.getFrom()) + "\" ");
        // TODO fromType, toType
        xmlDump.append("targetRef=\"_" + getUniqueNodeId(connection.getTo()) + "\" ");
        if (includeMeta) {
            String bendpoints = (String) connection.getMetaData("bendpoints");
            if (bendpoints != null) {
                xmlDump.append("g:bendpoints=\"" + bendpoints + "\" ");
            }
        }
        if (connection.getFrom() instanceof Split) {
        	Split split = (Split) connection.getFrom();
        	if (split.getType() == Split.TYPE_XOR || split.getType() == Split.TYPE_OR) {
        		Constraint constraint = split.getConstraint(connection);
        		xmlDump.append(">" + EOL +
    				"      <conditionExpression xs:type=\"tFormalExpression\" ");
        		if (constraint == null) {
        		    xmlDump.append("/>");
        		} else {
                    if ("code".equals(constraint.getType())) {
                        if (JavaDialect.ID.equals(constraint.getDialect())) {
                            xmlDump.append("language=\"" + JAVA_LANGUAGE + "\" ");
                        } else if (XPathDialect.ID.equals(constraint.getDialect())) {
                            xmlDump.append("language=\"" + XPATH_LANGUAGE + "\" ");
                        }
                    } else {
                        xmlDump.append("language=\"" + RULE_LANGUAGE + "\" ");
                    }
                    if (constraint.getName() != null && constraint.getName().trim().length() > 0) {
            			xmlDump.append("tns:name=\"" + XmlDumper.replaceIllegalChars(constraint.getName()) + "\" ");
            		}
                    String constraintString = constraint.getConstraint();
                    if (constraintString == null) {
                        constraintString = "";
                    }
                    xmlDump.append(">" + XmlDumper.replaceIllegalChars(constraintString) + "</conditionExpression>");
        		}
        		xmlDump.append(EOL
    		        + "    </sequenceFlow>" + EOL);
        	} else {
            	xmlDump.append("/>" + EOL);
            }
        } else {
        	xmlDump.append("/>" + EOL);
        }
    }
    
    public static String getUniqueNodeId(Node node) {
    	String result = node.getId() + "";
    	NodeContainer nodeContainer = node.getNodeContainer();
    	while (nodeContainer instanceof CompositeNode) {
    		CompositeNode composite = (CompositeNode) nodeContainer;
    		result = composite.getId() + "-" + result;
    		nodeContainer = composite.getNodeContainer();
    	}
    	return result;
    }
    
}
