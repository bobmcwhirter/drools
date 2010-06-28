package org.drools.process.audit;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class VariableInstanceLog implements Serializable {
    
	private static final long serialVersionUID = 4L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
    private long processInstanceId;
    private String processId;
    private String variableInstanceId;
    private String variableId;
    private String value;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "log_date")
    private Date date;
    
    VariableInstanceLog() {
    }
    
	public VariableInstanceLog(long processInstanceId, String processId,
			               	   String variableInstanceId, String variableId, String value) {
        this.processInstanceId = processInstanceId;
        this.processId = processId;
		this.variableInstanceId = variableInstanceId;
		this.variableId = variableId;
		this.value = value;
        this.date = new Date();
    }
	
    public long getId() {
    	return id;
    }
    
    void setId(long id) {
		this.id = id;
	}

    public long getProcessInstanceId() {
        return processInstanceId;
    }
    
	void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

    public String getProcessId() {
        return processId;
    }
    
	void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getVariableInstanceId() {
		return variableInstanceId;
	}

	public void setVariableInstanceId(String variableInstanceId) {
		this.variableInstanceId = variableInstanceId;
	}

	public String getVariableId() {
		return variableId;
	}

	public void setVariableId(String variableId) {
		this.variableId = variableId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getDate() {
        return date;
    }
    
	void setDate(Date date) {
		this.date = date;
	}

    public String toString() {
        return "Change variable '" + 
        	processId + "#" + variableId + "' to '" + value + "' [" + processInstanceId + "#" + variableInstanceId + "]";
    }
    
}
