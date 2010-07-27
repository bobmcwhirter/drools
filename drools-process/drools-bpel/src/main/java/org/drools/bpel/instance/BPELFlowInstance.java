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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.bpel.core.BPELFlow;
import org.drools.runtime.process.NodeInstance;
import org.drools.workflow.core.Node;
import org.drools.workflow.instance.node.CompositeNodeInstance;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELFlowInstance extends CompositeNodeInstance {

    private static final long serialVersionUID = 400L;
    
    private List<String> activatedLinks = new ArrayList<String>();
    private Map<String, NodeInstance> waitingActivityInstances = new HashMap<String, NodeInstance>();
    
    public BPELFlow getBPELFlow() {
        return (BPELFlow) getNode();
    }
    
    public void activateLink(String linkName) {
        activatedLinks.add(linkName);
        NodeInstance waitingActivityInstance = waitingActivityInstances.get(linkName);
        if (waitingActivityInstance != null) {
            ((org.drools.workflow.instance.NodeInstance) waitingActivityInstance)
            	.trigger(null, Node.CONNECTION_DEFAULT_TYPE);
        }
    }
    
    public void addWaitingActivityInstance(NodeInstance nodeInstance, String linkName) {
        waitingActivityInstances.put(linkName, nodeInstance);
    }
    
    public boolean isLinkActive(String linkName) {
        return activatedLinks.contains(linkName);
    }
    
    public void internalTrigger(NodeInstance from, String type) {
        if (BPELLinkManager.checkActivityEnabled(this)) {
            super.internalTrigger(from, type);
        }
    }
    
    public void triggerCompleted(String outType) {
        super.triggerCompleted(outType);
        BPELLinkManager.activateTargetLinks(this);
    }
}
