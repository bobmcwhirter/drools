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