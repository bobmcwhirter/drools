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

package org.drools.bpel.instance;

import org.drools.bpel.core.BPELActivity;
import org.drools.bpel.core.BPELActivity.SourceLink;
import org.drools.bpel.core.BPELActivity.TargetLink;
import org.drools.bpel.xpath.XPathReturnValueEvaluator;
import org.drools.core.util.ArrayUtils;
import org.drools.process.instance.ProcessInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.spi.ProcessContext;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public final class BPELLinkManager {
    
    private BPELLinkManager() {
    }
    
    private static SourceLink[] getSourceLinks(NodeInstance activityInstance) {
        return ((BPELActivity) 
            ((NodeInstanceImpl) activityInstance).getNode())
                .getSourceLinks();
    }
    
    private static TargetLink[] getTargetLinks(NodeInstance activityInstance) {
        return ((BPELActivity) 
            ((NodeInstanceImpl) activityInstance).getNode())
                .getTargetLinks();
    }
    
    public static boolean checkActivityEnabled(NodeInstance activityInstance) {
        boolean enabled = true;
        TargetLink[] incomingLinks = getTargetLinks(activityInstance);
        if (incomingLinks != null) {
            for (int i = 0; i < incomingLinks.length; i++) {
                BPELFlowInstance flowInstance = getFlowInstance(activityInstance, incomingLinks[i].getLinkName());
                if (!flowInstance.isLinkActive(incomingLinks[i].getLinkName())) {
                    enabled = false;
                    flowInstance.addWaitingActivityInstance(activityInstance, incomingLinks[i].getLinkName());
                }
            }
        }
        return enabled;
    }
    
    public static void activateTargetLinks(NodeInstance activityInstance) {
        SourceLink[] outgoingLinks = getSourceLinks(activityInstance);
        if (outgoingLinks != null) {
            for (int i = 0; i < outgoingLinks.length; i++) {
                BPELFlowInstance flowInstance = getFlowInstance(activityInstance, outgoingLinks[i].getLinkName());
                String transitionCondition = outgoingLinks[i].getTransitionCondition(); 
                if (transitionCondition == null || evaluateTransitionCondition(transitionCondition, activityInstance)) {
                	flowInstance.activateLink(outgoingLinks[i].getLinkName());
                }
            }
        }
    }

    private static BPELFlowInstance getFlowInstance(NodeInstance activityInstance, String linkName) {
        NodeInstanceContainer parent = activityInstance.getNodeInstanceContainer();
        while (!(parent instanceof BPELFlowInstance)
                || !ArrayUtils.contains(((BPELFlowInstance) parent).getBPELFlow().getLinks(), linkName)) {
             parent = ((NodeInstance) parent).getNodeInstanceContainer();     
        }
        return (BPELFlowInstance) parent;
    }
    
    private static boolean evaluateTransitionCondition(String transitionCondition, NodeInstance activityInstance) {
    	try {
	    	XPathReturnValueEvaluator evaluator = new XPathReturnValueEvaluator();
	    	evaluator.setExpression(transitionCondition);
	    	ProcessContext processContext = new ProcessContext();
	    	processContext.setNodeInstance(activityInstance);
	    	return (Boolean) evaluator.evaluate(((ProcessInstance) activityInstance.getProcessInstance()).getWorkingMemory(), processContext);
    	} catch (Throwable t) {
    		throw new IllegalArgumentException("Could not evaluate transition condition " + transitionCondition, t);
    	}
    }

}
