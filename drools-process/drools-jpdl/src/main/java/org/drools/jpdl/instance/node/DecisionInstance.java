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

package org.drools.jpdl.instance.node;

import org.drools.jpdl.core.node.Decision;
import org.drools.workflow.instance.NodeInstance;
import org.jbpm.JbpmException;
import org.jbpm.graph.node.DecisionCondition;
import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;

public class DecisionInstance extends JpdlNodeInstance {

	private static final long serialVersionUID = 1L;

    public Decision getDecision() {
        return (Decision) getNode();
    }

    public void execute(NodeInstance from, String type) {
    	String transitionName = null;
    	try {
    		Delegation decisionDelegation = getDecision().getDecisionDelegation();
    		if (decisionDelegation != null) {
    			DecisionHandler decisionHandler = (DecisionHandler) decisionDelegation.instantiate();
    			transitionName = decisionHandler.decide(new JpdlExecutionContext());
    		} else if (getDecision().getDecisionExpression() != null) {
    			String decisionExpression = getDecision().getDecisionExpression();
    			Object result = JbpmExpressionEvaluator.evaluate(
					decisionExpression, new JpdlExecutionContext());
    			if (result == null) {
    				throw new JbpmException("decision expression '" + 
						decisionExpression + "' returned null");
    			}
    			transitionName = result.toString();
    		} else if (getDecision().getDecisionConditions() != null) {
    			for (DecisionCondition decisionCondition: getDecision().getDecisionConditions()) {
    				Object result = JbpmExpressionEvaluator.evaluate(
						decisionCondition.getExpression(), new JpdlExecutionContext());
    				if (Boolean.TRUE.equals(result)) {
    					transitionName = decisionCondition.getTransitionName();
    					break;
    				}
    			}
    		} else {
    			// TODO
    			/*Iterator iter = leavingTransitions.iterator();
    			while (iter.hasNext() && (transition==null)) {
    				Transition candidate = (Transition) iter.next();
    				String conditionExpression = candidate.getCondition();
    				if (conditionExpression!=null) {
    					Object result = JbpmExpressionEvaluator.evaluate(conditionExpression, executionContext);
    					if (Boolean.TRUE.equals(result)) {
    						transition = candidate;
    					}
    				}
    			}*/
    		}
    		if (transitionName == null) {
    			leave();
    		} else {
    			leave(transitionName);
    		}
    	} catch (Exception exception) {
    		raiseException(exception);
    	}
    }

}
