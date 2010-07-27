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

package org.drools.jpdl.core.node;

import java.util.List;

import org.jbpm.graph.node.DecisionCondition;
import org.jbpm.instantiation.Delegation;

public class Decision extends JpdlNode {

	private static final long serialVersionUID = 1L;

	private List<DecisionCondition> decisionConditions;
	private Delegation decisionDelegation;
	private String decisionExpression;

	public List<DecisionCondition> getDecisionConditions() {
		return decisionConditions;
	}

	public void setDecisionConditions(List<DecisionCondition> decisionConditions) {
		this.decisionConditions = decisionConditions;
	}

	public Delegation getDecisionDelegation() {
		return decisionDelegation;
	}

	public void setDecisionDelegation(Delegation decisionDelegation) {
		this.decisionDelegation = decisionDelegation;
	}

	public String getDecisionExpression() {
		return decisionExpression;
	}

	public void setDecisionExpression(String decisionExpression) {
		this.decisionExpression = decisionExpression;
	}

}
