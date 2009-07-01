package org.drools.ode.instance;

import org.drools.runtime.process.NodeInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

public class BPELNodeInstance extends NodeInstanceImpl {

	private static final long serialVersionUID = 4L;

	public void internalTrigger(NodeInstance from, String type) {
		throw new UnsupportedOperationException();
	}

}
