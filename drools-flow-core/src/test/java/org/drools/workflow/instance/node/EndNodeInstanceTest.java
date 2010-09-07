/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workflow.instance.node;


import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.process.instance.ProcessInstance;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.instance.impl.NodeInstanceFactoryRegistry;

public class EndNodeInstanceTest extends TestCase {
    
    public void testEndNode() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();        
        
        MockNode mockNode = new MockNode();        
        MockNodeInstanceFactory factory = new MockNodeInstanceFactory( new MockNodeInstance( mockNode ) );
        NodeInstanceFactoryRegistry.INSTANCE.register(  mockNode.getClass(), factory );
        
        WorkflowProcessImpl process = new WorkflowProcessImpl(); 
        
        Node endNode = new EndNode();  
        endNode.setId( 1 );
        endNode.setName( "end node" );        
                            
        mockNode.setId( 2 );
        new ConnectionImpl(mockNode, Node.CONNECTION_DEFAULT_TYPE, endNode, Node.CONNECTION_DEFAULT_TYPE);
        
        process.addNode( mockNode );
        process.addNode( endNode );
                
        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();   
        processInstance.setState( ProcessInstance.STATE_ACTIVE );
        processInstance.setProcess( process );
        processInstance.setKnowledgeRuntime( (InternalKnowledgeRuntime) ksession );
        
        MockNodeInstance mockNodeInstance = ( MockNodeInstance ) processInstance.getNodeInstance( mockNode );
        
        mockNodeInstance.triggerCompleted();
        assertEquals( ProcessInstance.STATE_COMPLETED, processInstance.getState() );                               
    }
}
