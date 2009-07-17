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

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.jpdl.core.JpdlProcess;

import org.drools.workflow.core.impl.NodeImpl;
import org.jbpm.graph.def.Action;
import org.jbpm.instantiation.Delegation;

/**
 *
 * @author salaboy
 */
public class EpdlWriter {

    public static void write(JpdlProcess process) {
        Node[] nodes = process.getNodes();
        int id = 0;

        System.out.println("<process xmlns=\"http://drools.org/drools-5.0/process\""+
                " xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" "+
                "xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\" " +
                "type=\"RuleFlow\" name=\"flow\" id=\""+process.getName()+"\" package-name=\"org.drools.examples\" >");
        System.out.println("<header>");
        System.out.println("</header>");
        System.out.println("<nodes>");
        for(Node node : nodes){
            //System.out.println("Node Type: "+node.getClass()+" - Name"+node.getName());
            if(node instanceof org.drools.jpdl.core.node.StartState){
                System.out.println("<start id=\""+node.getId()+"\" name=\""+node.getName()+"\" />");
            }
            else if(node instanceof org.drools.jpdl.core.node.Fork){
                System.out.println("<split id=\""+node.getId()+"\" name=\""+node.getName()+"\" type=\"1\" />");
            }
            else if(node instanceof org.drools.jpdl.core.node.Join){
                System.out.println("<join id=\""+node.getId()+"\" name=\""+node.getName()+"\" type=\"1\" />");
            }
            else if(node instanceof org.drools.jpdl.core.node.State){
                System.out.println("<state id=\""+node.getId()+"\" name=\""+node.getName()+"\" >");
                System.out.println("    <constraints>");
                for (Connection connection: node.getOutgoingConnections(NodeImpl.CONNECTION_DEFAULT_TYPE)) {
                    System.out.println("        <constraint toNodeId=\""+connection.getTo().getId()+"\" name=\"signalTo"+connection.getTo().getName()+"\" />");
                }
                System.out.println("    </constraints>");
                System.out.println("</state>");

            }
            else if(node instanceof org.drools.jpdl.core.node.EndState){
                System.out.println("<end id=\""+node.getId()+"\" name=\""+node.getName()+"\" />");
            }
            else if(node instanceof org.drools.jpdl.core.node.JpdlNode){
                System.out.println("<actionNode id=\""+node.getId()+"\" name=\""+node.getName()+"\">");
                System.out.println("    <action type=\"expression\" dialect=\"java\" >");
                Action action = ((org.drools.jpdl.core.node.JpdlNode)node).getAction();
                if(action != null){
                    Delegation delegation = action.getActionDelegation();
                    if(delegation != null){
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
        for(Node node : nodes){

            Map<String, List<Connection>> outConnections = node.getOutgoingConnections();
            Set<String> keys = outConnections.keySet();
            for(String key : keys){
                List<Connection> connections = outConnections.get(key);
                for(Connection connection : connections){
                    System.out.println("    <connection from=\""+node.getId()+"\" to=\""+connection.getTo().getId()+"\" />");
                }
            }

        }
         System.out.println("</connections>");

         System.out.println("</process>");
    }


}
