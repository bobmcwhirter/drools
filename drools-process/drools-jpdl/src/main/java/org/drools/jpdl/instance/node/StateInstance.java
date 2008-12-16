package org.drools.jpdl.instance.node;

import org.drools.runtime.process.NodeInstance;
import org.drools.workflow.instance.node.EventBasedNodeInstanceInterface;

public class StateInstance extends JpdlNodeInstance implements EventBasedNodeInstanceInterface {

	private static final long serialVersionUID = 1L;
	
	public void execute(NodeInstance from, String type) {
		// Do nothing
		addSignalListener();
	}

	public void addEventListeners() {
		addSignalListener();
	}
	
	private void addSignalListener() {
		getProcessInstance().addEventListener("signal", this, false);
	}

	public void removeEventListeners() {
		getProcessInstance().removeEventListener("signal", this, false);
	}
	
	public void signalEvent(String type, Object event) {
		if ("signal".equals(type)) {
			if (event instanceof String) {
				leave((String) event);
			} else {
				leave();
			}
		} else {
			super.signalEvent(type, event);
		}
	}

}