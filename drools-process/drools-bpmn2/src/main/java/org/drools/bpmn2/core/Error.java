package org.drools.bpmn2.core;

import java.io.Serializable;

public class Error implements Serializable {
    
	private static final long serialVersionUID = 4L;
	
    private String id;
    private String errorCode;
    
    public Error(String id, String errorCode) {
        this.id = id;
        this.errorCode = errorCode;
    }
    
    public String getId() {
        return id;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
