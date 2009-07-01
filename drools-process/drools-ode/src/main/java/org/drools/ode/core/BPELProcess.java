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
