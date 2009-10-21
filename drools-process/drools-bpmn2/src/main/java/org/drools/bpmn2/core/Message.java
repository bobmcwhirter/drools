package org.drools.bpmn2.core;

public class Message {
    
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
