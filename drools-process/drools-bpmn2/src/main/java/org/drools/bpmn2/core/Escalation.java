package org.drools.bpmn2.core;

import java.io.Serializable;

public class Escalation implements Serializable {
    
	private static final long serialVersionUID = 4L;
	
    private String id;
    private String escalationCode;
    
    public Escalation(String id, String escalationCode) {
        this.id = id;
        this.escalationCode = escalationCode;
    }
    
    public String getId() {
        return id;
    }

    public String getEscalationCode() {
        return escalationCode;
    }

}
