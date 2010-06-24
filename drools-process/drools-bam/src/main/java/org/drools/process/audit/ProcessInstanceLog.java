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
public class ProcessInstanceLog implements Serializable {
    
	private static final long serialVersionUID = 4L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
    private long processInstanceId;
    private String processId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date start;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date end;
    
    ProcessInstanceLog() {
    }
    
    public ProcessInstanceLog(long processInstanceId, String processId) {
        setProcessInstanceId(processInstanceId);
        setProcessId(processId);
        setStart(new Date());
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
    
    private void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public String getProcessId() {
        return processId;
    }
    
    public void setProcessId(String processId) {
        this.processId = processId;
    }
    
    public Date getStart() {
        return start;
    }
    
    public void setStart(Date start) {
        this.start = start;
    }
    
    public Date getEnd() {
        return end;
    }
    
    public void setEnd(Date end) {
        this.end = end;
    }
    
    public String toString() {
        return "Process '" + processId + "' [" + processInstanceId + "]";
    }
    
}
