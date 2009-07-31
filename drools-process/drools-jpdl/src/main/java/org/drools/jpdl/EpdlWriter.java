/*
 *  Copyright 2009 salaboy.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.drools.jpdl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.jpdl.core.JpdlProcess;

import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.ConnectionRef;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.core.node.StateNode;
import org.jbpm.graph.def.Action;
import org.jbpm.instantiation.Delegation;

/**
 *
 * @author salaboy
 */
public class EpdlWriter {
    private static int suggestedNodeId = 99;
    public static void write(JpdlProcess process) {
        Node[] nodes = process.getNodes();
        int id = 0;
        
        
        System.out.println("<process xmlns=\"http://drools.org/drools-5.0/process\""+
                " xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" "+
                "xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\" " +
                "type=\"RuleFlow\" name=\"flow\" id=\""+process.getName()+"\" package-name=\"org.drools.examples\" >");
        System.out.println("<header>");
        System.out.println("</header>");
        //Print nodes and Connections
        printNodesAndConnections(nodes);
        

        System.out.println("</process>");
    }

    private static void printNodesAndConnections(Node[] nodes) {
        String generatedConnections = "";
        System.out.println("<nodes>");
        for (Node node : nodes) {
            //System.out.println("Node Type: "+node.getClass()+" - Name"+node.getName());
            if (node instanceof org.drools.jpdl.core.node.StartState) {
                System.out.println("<start id=\"" + node.getId() + "\" name=\"" + node.getName() + "\" />");
            } else if (node instanceof org.drools.jpdl.core.node.Fork) {
                System.out.println("<split id=\"" + node.getId() + "\" name=\"" + node.getName() + "\" type=\"1\" />");
            } else if (node instanceof org.drools.jpdl.core.node.Join) {
                System.out.println("<join id=\"" + node.getId() + "\" name=\"" + node.getName() + "\" type=\"1\" />");
            } else if (node instanceof org.drools.jpdl.core.node.SuperState) {
                generatedConnections += suggestSplitNode(node);
                generatedConnections += suggestJoinNode(node);
                System.out.println("<composite id=\""+ node.getId() +"\" name=\""+ node.getName() +"\">");
                    List<org.drools.jpdl.core.node.JpdlNode> nodesList = ((org.drools.jpdl.core.node.SuperState)node).getNodes();
                    printNodesAndConnections(nodesList.toArray(new Node[nodesList.size()]));
                    printInPortsAndOutPorts(nodesList.toArray(new Node[nodesList.size()]));
                System.out.println("</composite>");

            } else if (node instanceof org.drools.jpdl.core.node.State) {
                generatedConnections += suggestJoinNode(node);
                System.out.println("<state id=\"" + node.getId() + "\" name=\"" + node.getName() + "\" >");
                System.out.println("    <constraints>");
                Set<String> keys = node.getOutgoingConnections().keySet();
                for (String key : keys) {
                    for (Connection connection : node.getOutgoingConnections(key)) {
                        System.out.println("        <constraint toNodeId=\"" + connection.getTo().getId() + "\" name=\"signalTo" + connection.getTo().getName() + "\" />");
                    }
                }
                System.out.println("    </constraints>");
                System.out.println("</state>");
            } else if (node instanceof org.drools.jpdl.core.node.Decision) {
                System.out.println("<split id=\"" + node.getId() + "\" name=\"" + node.getName() + "\" type=\"2\" >");
                System.out.println("    <constraints>");
                Set<String> keys = node.getOutgoingConnections().keySet();
                for (String key : keys) {
                    for (Connection connection : node.getOutgoingConnections(key)) {
                        System.out.println("        <constraint toNodeId=\"" + connection.getTo().getId() + "\" name=\"signalTo" + connection.getTo().getName() + "\" toType=\"DROOLS_DEFAULT\" type=\"rule\" dialect=\"java\" >");
                        //System.out.println("            "+"There is no way to get the conditions in each leavingTransition (depracated since 3.2 - http://docs.jboss.com/jbpm/v3.2/userguide/html_single/#condition.element)");
                        //System.out.println("            "+"There is no way to access the decision expresion or the decision delegation class through the APIs");
                        System.out.println("        </constraint>");
                    }
                }
                System.out.println("    </constraints>");
                System.out.println("</split>");
            } else if (node instanceof org.drools.jpdl.core.node.EndState) {
                generatedConnections += suggestJoinNode(node);
                System.out.println("<end id=\"" + node.getId() + "\" name=\"" + node.getName() + "\" />");
            } else if (node instanceof org.drools.jpdl.core.node.JpdlNode) {
                generatedConnections += suggestSplitNode(node);
                generatedConnections += suggestJoinNode(node);
                System.out.println("<actionNode id=\"" + node.getId() + "\" name=\"" + node.getName() + "\">");
                System.out.println("    <action type=\"expression\" dialect=\"java\" >");
                Action action = ((org.drools.jpdl.core.node.JpdlNode) node).getAction();
                if (action != null) {
                    Delegation delegation = action.getActionDelegation();
                    if (delegation != null) {
                        // System.out.println("Introspect = "+delegation.getClassName());
                        // System.out.println("replaced"+delegation.getClassName().replace(".","/"));
                        // Resource resource = ResourceFactory.newInputStreamResource(EpdlWriter.class.getResourceAsStream(delegation.getClassName().replace(".","/")));
                        // System.out.println(""+resource);
                        //  System.out.println("Paste the content of the execute() method of the class"+delegation.getClassName());
                    }
                }
                System.out.println("    </action>");
                System.out.println("</actionNode>");
            }
        }
        System.out.println("</nodes>");
        System.out.println("<connections>");
        for (Node node : nodes) {
            Map<String, List<Connection>> outConnections = node.getOutgoingConnections();
            Set<String> keys = outConnections.keySet();
            if(keys.size() == 0){
                break;
            }
            for (String key : keys) {
                List<Connection> connections = outConnections.get(key);
                for (Connection connection : connections) {
                    System.out.println("    <connection from=\"" + node.getId() + "\" to=\"" + connection.getTo().getId() + "\" />");
                }
            }
        }
        if(!generatedConnections.equals("")){
            System.out.println("<!-- Generated Connection for suggested nodes -->");
            System.out.println(generatedConnections);
            System.out.println("<!-- END - Generated Connection for suggested nodes -->");
        }
        System.out.println("</connections>");
    }

    private static String suggestJoinNode(Node node) {
        String resultGeneratedConnection = "";
        Set<String> incomingConnectionsTypes = node.getIncomingConnections().keySet();
        //Probably we are inside a composite node or in a disconected node (??)
        if(incomingConnectionsTypes.size()== 0){
            return "";
        }
        String firstKey = incomingConnectionsTypes.iterator().next();
        boolean suggestJoinNode = false;
        if (incomingConnectionsTypes.size() > 1) {
            suggestJoinNode = true;
        } else if (incomingConnectionsTypes.size() == 1 && node.getIncomingConnections().get(firstKey).size() > 1) {
            suggestJoinNode = true;
        }
        if (suggestJoinNode) {

            System.out.println("<!-- This is a suggested Join Node -->");
            System.out.println("<join id=\"" + (suggestedNodeId) + "\" name=\"Join XOR - "+suggestedNodeId+"\" type=\"2\" />");
            
            for (String key : incomingConnectionsTypes) {
                Iterator<Connection> itConnections = node.getIncomingConnections(key).iterator();
                long fromNodeId = 0;
                while (itConnections.hasNext()) {
                    Connection connection = itConnections.next();
                    Node fromNode = connection.getFrom();

                    fromNodeId = connection.getTo().getId();
                    if(fromNode instanceof org.drools.jpdl.core.node.State){
                        System.out.println("<!-- Take a look at the State Node that is pointing here, " +
                                "you will need to change the constraint for signal it to the new JoinNode id -->");
                        System.out.println("<!-- in node id: "+fromNode.getId()+ " / name: "+fromNode.getName()+" -->");
                        System.out.println("<!-- you should change the fromId ("+fromNodeId+") attribute to: "+suggestedNodeId+"-->");
                        System.out.println("<!-- you can also change the name for something that reference the JoinNode -->");
                    }
                    fromNode.getOutgoingConnections(key).remove(connection);
                    resultGeneratedConnection += "  <connection from=\"" + fromNode.getId() + "\" to=\"" + suggestedNodeId + "\" />\n";
                }
                resultGeneratedConnection += "  <connection from=\"" + suggestedNodeId + "\" to=\"" + fromNodeId + "\" />\n";
            }
            System.out.println("<!-- END - This is a suggested Join Node -->");
            suggestedNodeId++;
        }
        return resultGeneratedConnection;
    }
    private static String suggestSplitNode(Node node) {
        String resultGeneratedConnection = "";
        Set<String> outgoingConnectionsTypes = node.getOutgoingConnections().keySet();
         //Probably we are inside a composite node or in a disconected node (??)
        if(outgoingConnectionsTypes.size()== 0){
            return "";
        }
        String firstKey = outgoingConnectionsTypes.iterator().next();
        boolean suggestSplitNode = false;
        if (outgoingConnectionsTypes.size() > 1) {
            suggestSplitNode = true;
        } else if (outgoingConnectionsTypes.size() == 1 && node.getOutgoingConnections().get(firstKey).size() > 1) {
            suggestSplitNode = true;
        }
        if(suggestSplitNode){
            System.out.println("<!-- This is a suggested Split Node -->");
            System.out.println("<split id=\"" + (suggestedNodeId) + "\" name=\"Split XOR - "+suggestedNodeId+"\" type=\"2\" >");
              System.out.println("    <constraints>");
                Set<String> keys = node.getOutgoingConnections().keySet();
                for(String key: keys){

                    for (Connection connection: node.getOutgoingConnections(key)) {
                        System.out.println("        <constraint toNodeId=\""+connection.getTo().getId()+"\" name=\"signalTo"+connection.getTo().getName()+
                                            "\" toType=\"DROOLS_DEFAULT\" type=\"rule\" dialect=\"java\" >");
                        //System.out.println("            "+"There is no way to get the conditions in each leavingTransition (depracated since 3.2 - http://docs.jboss.com/jbpm/v3.2/userguide/html_single/#condition.element)");
                        //System.out.println("            "+"There is no way to access the decision expresion or the decision delegation class through the APIs");
                        System.out.println("        </constraint>");

                    }
                }
                System.out.println("    </constraints>");
                System.out.println("</split>");
            System.out.println("<!-- END - This is a suggested Split Node -->");
            List<Connection> removeConnections = new ArrayList<Connection>();
            for (String key : outgoingConnectionsTypes) {
                Iterator<Connection> itConnections = node.getOutgoingConnections(key).iterator();
                while (itConnections.hasNext()) {
                    Connection connection = itConnections.next();
                    Node toNode = connection.getTo();
                    removeConnections.add(connection);
                    resultGeneratedConnection += "  <connection from=\"" + suggestedNodeId + "\" to=\"" + toNode.getId() + "\" />\n";
                }
                

            }
            resultGeneratedConnection += "  <connection from=\"" + node.getId() + "\" to=\"" + suggestedNodeId + "\" />\n";
            suggestedNodeId++;
            for(Connection conn : removeConnections){
                
                    node.getOutgoingConnections(conn.getFromType()).remove(conn);
               
            }

        }

        return resultGeneratedConnection;
    }

    private static void printInPortsAndOutPorts(Node[] toArray) {
        int lastNode = 0;
        if(toArray.length > 1){
            lastNode = toArray.length -1;
        }
        
        System.out.println("<in-ports>");
        System.out.println("    <in-port type=\"DROOLS_DEFAULT\" nodeId=\""+toArray[0].getId()+"\" nodeInType=\"DROOLS_DEFAULT\" />");
        System.out.println("</in-ports>");
        System.out.println("<out-ports>");
        System.out.println("    <out-port type=\"DROOLS_DEFAULT\" nodeId=\""+toArray[lastNode].getId()+"\" nodeOutType=\"DROOLS_DEFAULT\" />");
        System.out.println("</out-ports>");
    }


}
