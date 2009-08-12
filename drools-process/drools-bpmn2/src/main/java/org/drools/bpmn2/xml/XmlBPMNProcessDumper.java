package org.drools.bpmn2.xml;

import java.util.ArrayList;
import java.util.List;

import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.definition.process.WorkflowProcess;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.workflow.core.node.Split;
import org.drools.xml.Handler;
import org.drools.xml.SemanticModule;
import org.drools.xml.XmlDumper;

public class XmlBPMNProcessDumper {
    
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
	    if (variableScope != null && !variableScope.getVariables().isEmpty()) {
	    	for (Variable variable: variableScope.getVariables()) {
	            xmlDump.append(
            		"  <itemDefinition id=\"" + variable.getName() + "Item\" ");
	            if (variable.getType() != null) {
	            	xmlDump.append("structureRef=\"" + variable.getType().getStringType() + "\" ");
	            }
	            xmlDump.append("/>" + EOL);
	        }
	    	xmlDump.append(EOL);
	    }
        
	    xmlDump.append(
    		"  <resource id=\"Actor\" name=\"Human Actor\" />" + EOL + EOL);

	    // the process itself
		xmlDump.append("  <process ");
        if (process.getId() != null) {
            xmlDump.append("id=\"" + process.getId() + "\" ");
        }
        if (process.getName() != null) {
            xmlDump.append("name=\"" + process.getName() + "\" ");
        }
        // TODO: package, version
        xmlDump.append(">" + EOL + EOL);
        visitHeader(process, xmlDump, includeMeta);
        visitNodes(process, xmlDump, includeMeta);
        visitConnections(process.getNodes(), xmlDump, includeMeta);
        xmlDump.append("  </process>" + EOL + EOL);
        xmlDump.append("</definitions>");
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
    	xmlDump.append("    <!-- process variables -->" + EOL);
        for (Variable variable: variables) {
            xmlDump.append("    <property id=\"" + variable.getName() + "\" ");
            if (variable.getType() != null) {
            	xmlDump.append("itemSubjectRef=\"tns:" + variable.getName() + "Item\"" );
            }
            // TODO: value
            xmlDump.append("/>" + EOL);
        }
        xmlDump.append(EOL);
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
        xmlDump.append("    <sequenceFlow sourceRef=\"_" + connection.getFrom().getId() + "\" ");
        // TODO fromType, toType
        xmlDump.append("targetRef=\"_" + connection.getTo().getId() + "\" ");
        if (includeMeta) {
            String bendpoints = (String) connection.getMetaData("bendpoints");
            if (bendpoints != null) {
                xmlDump.append("g:bendpoints=\"" + bendpoints + "\" ");
            }
        }
        if (connection.getFrom() instanceof Split) {
        	Split split = (Split) connection.getFrom();
        	if (split.getType() == Split.TYPE_XOR || split.getType() == Split.TYPE_OR) {
        		String constraint = split.getConstraint(connection).getConstraint();
        		xmlDump.append(">" + EOL +
    				"      <conditionExpression>" + XmlDumper.replaceIllegalChars(constraint) + "</conditionExpression>" + EOL +
    				"    </sequenceFlow>" + EOL);
        	} else {
            	xmlDump.append("/>" + EOL);
            }
        } else {
        	xmlDump.append("/>" + EOL);
        }
    }
    
}
