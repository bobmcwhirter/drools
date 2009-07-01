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
