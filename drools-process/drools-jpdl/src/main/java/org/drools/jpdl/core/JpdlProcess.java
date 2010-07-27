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

package org.drools.jpdl.core;

import org.drools.definition.process.Node;
import org.drools.jpdl.core.node.StartState;
import org.drools.process.core.context.swimlane.SwimlaneContext;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.workflow.core.impl.WorkflowProcessImpl;

public class JpdlProcess extends WorkflowProcessImpl {

    public static final String JPDL_TYPE = "jPDL";

	private static final long serialVersionUID = 1L;
	
	private Node startState;

    public JpdlProcess() {
        setType(JPDL_TYPE);
        VariableScope variableScope = new VariableScope();
        addContext(variableScope);
        setDefaultContext(variableScope);
        SwimlaneContext swimlaneContext = new SwimlaneContext();
        addContext(swimlaneContext);
        setDefaultContext(swimlaneContext);
    }
    
    public void setStartState(Node startState) {
        this.startState = startState;
    }
    
    public Node getStart() {
        if (startState != null) {
            return startState;
        }
        Node[] nodes = getNodes();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof StartState) {
                return (StartState) nodes[i];
            }
        }
        return null;
    }

    public VariableScope getVariableScope() {
        return (VariableScope) getDefaultContext(VariableScope.VARIABLE_SCOPE);
    }

}
