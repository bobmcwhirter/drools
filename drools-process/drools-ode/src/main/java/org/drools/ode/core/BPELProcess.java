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

package org.drools.ode.core;

import org.apache.ode.bpel.iapi.ProcessConf;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.workflow.core.impl.WorkflowProcessImpl;

public class BPELProcess extends WorkflowProcessImpl {

    public static final String ODE_TYPE = "ODE";

	private static final long serialVersionUID = 4L;
	
	private byte[] bpelJar;
	private transient ProcessConf processConf;

	public BPELProcess() {
        setType(ODE_TYPE);
        VariableScope variableScope = new VariableScope();
        addContext(variableScope);
        setDefaultContext(variableScope);
	}

	public ProcessConf getProcessConf() {
		return processConf;
	}

	public void setProcessConf(ProcessConf processConf) {
		this.processConf = processConf;
	}
	
	public byte[] getBPELJar() {
		return bpelJar;
	}
	
	public void setBPELJar(byte[] bpelJar) {
		this.bpelJar = bpelJar;
	}

}
