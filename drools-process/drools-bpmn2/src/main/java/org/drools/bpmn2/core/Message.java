package org.drools.bpmn2.core;

import java.io.Serializable;

public class Message implements Serializable {
    
	private static final long serialVersionUID = 4L;
	
    private String id;
    private String type;
    
    public Message(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
