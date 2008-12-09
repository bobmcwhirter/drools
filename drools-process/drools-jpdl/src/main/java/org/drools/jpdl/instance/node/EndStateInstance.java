package org.drools.jpdl.instance.node;

import org.drools.process.instance.ProcessInstance;
import org.drools.workflow.instance.NodeInstanceContainer;

public class EndStateInstance extends JpdlNodeInstance {

    private static final long serialVersionUID = 1L;

    public void leave() {
        ((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
        getProcessInstance().setState(ProcessInstance.STATE_COMPLETED);
    }
    
}
