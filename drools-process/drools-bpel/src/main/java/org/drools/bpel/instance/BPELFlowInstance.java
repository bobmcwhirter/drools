package org.drools.bpel.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.bpel.core.BPELFlow;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.CompositeNode;
import org.drools.workflow.instance.NodeInstance;
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
            waitingActivityInstance.trigger(null, Node.CONNECTION_DEFAULT_TYPE);
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
    
	public void triggerEvent(String type, Object event) {
		super.triggerEvent(type, event);
		CompositeNode.NodeAndType nodeAndType = getCompositeNode().internalGetLinkedIncomingNode(Node.CONNECTION_DEFAULT_TYPE);
		NodeInstance nodeInstance = getNodeInstance(nodeAndType.getNode());
        nodeInstance.trigger(null, nodeAndType.getType());
	}

    public void triggerCompleted(String outType) {
        super.triggerCompleted(outType);
        BPELLinkManager.activateTargetLinks(this);
    }
}
