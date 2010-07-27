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

package org.drools.ode.instance;

import org.drools.ode.core.BPELProcess;
import org.drools.workflow.instance.impl.WorkflowProcessInstanceImpl;

public class BPELProcessInstance extends WorkflowProcessInstanceImpl {
	
    private static final long serialVersionUID = 4L;
    
    private Long ODEProcessInstanceId;
    
    public BPELProcess getBPELProcess() {
    	return (BPELProcess) getProcess();
    }
    
    public Long getODEProcessInstanceId() {
		return ODEProcessInstanceId;
	}

	public void setODEProcessInstanceIdODE(Long ODEProcessInstanceId) {
		this.ODEProcessInstanceId = ODEProcessInstanceId;
	}

	protected void internalStart() {
		// Do nothing
	}

}
