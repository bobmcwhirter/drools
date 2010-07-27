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

package org.drools.bpel.core;

import java.util.ArrayList;
import java.util.List;

import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.drools.workflow.core.node.CompositeContextNode;

public class BPELFaultHandlerScope extends CompositeContextNode {

	public static final String INTERNAL_FAULT_DATA_VARIABLE = "DroolsInternalFaultDataVariable";
	public static final String INTERNAL_FAULT_NAME_VARIABLE = "DroolsInternalFaultNameVariable";

	private static final long serialVersionUID = 4L;
	
	public BPELFaultHandlerScope() {
		VariableScope variableScope = new VariableScope();
		List<Variable> variables = new ArrayList<Variable>();
		Variable variable = new Variable();
		variable.setName(INTERNAL_FAULT_DATA_VARIABLE);
		variables.add(variable);
		variable = new Variable();
		variable.setName(INTERNAL_FAULT_NAME_VARIABLE);
		variable.setType(new StringDataType());
		variables.add(variable);
		variableScope.setVariables(variables);
	    addContext(variableScope);
	    setDefaultContext(variableScope);
	}
	
}
