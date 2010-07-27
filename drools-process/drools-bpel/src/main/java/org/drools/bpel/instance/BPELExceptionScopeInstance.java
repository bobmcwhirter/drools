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

import org.drools.bpel.core.BPELFaultHandler;
import org.drools.process.core.context.exception.ExceptionHandler;
import org.drools.process.instance.context.exception.ExceptionScopeInstance;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.context.WorkflowContextInstance;

public class BPELExceptionScopeInstance extends ExceptionScopeInstance implements WorkflowContextInstance {

    private static final long serialVersionUID = 400L;
    
    private NodeInstanceContainer nodeInstanceContainer;

    public NodeInstanceContainer getNodeInstanceContainer() {
        return nodeInstanceContainer;
    }

    public void setNodeInstanceContainer(NodeInstanceContainer nodeInstanceContainer) {
        this.nodeInstanceContainer = nodeInstanceContainer;
    }

	public void handleException(ExceptionHandler handler, String exception, Object params) {
		if (handler instanceof BPELFaultHandler) {
			BPELFaultHandlerInstance handlerInstance = new BPELFaultHandlerInstance();
	        handlerInstance.setExceptionHandler(handler);
	        handlerInstance.setNodeInstanceContainer(nodeInstanceContainer);
			handlerInstance.handleException(exception, params);
		} else {
			throw new IllegalArgumentException(
				"A BPEL Exception scope can only handle BPELFaultHandlers: " + handler);
		}
	}

}
