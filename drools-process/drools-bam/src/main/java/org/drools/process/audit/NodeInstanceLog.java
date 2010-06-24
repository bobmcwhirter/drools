package org.drools.process.audit;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries( {
        @NamedQuery(name = "NodeInstanceLog.findByProcessId", query = "FROM NodeInstanceLog n "
                + "WHERE n.processId = :processId ORDER BY date"),
        @NamedQuery(name = "NodeInstanceLog.findByProcessIdAndNodeId", query = "FROM NodeInstanceLog n "
                + "WHERE n.processId = :processId AND n.nodeId = :nodeId ORDER BY date"),
        @NamedQuery(name = "NodeInstanceLog.findByProcessInstance", query = "FROM NodeInstanceLog n "
                + "WHERE n.processId = :processId AND n.processInstanceId = :processInstanceId ORDER BY date"),
        @NamedQuery(name = "NodeInstanceLog.findByProcessInstanceAndNodeId", query = "FROM NodeInstanceLog n "
                + "WHERE n.processId = :processId AND n.processInstanceId = :processInstanceId AND n.nodeId = :nodeId ORDER BY date") })
public class NodeInstanceLog implements Serializable {
    
	public static final int TYPE_ENTER = 0;
	public static final int TYPE_EXIT = 1;
	
	private static final long serialVersionUID = 4L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
    private int type;
    private long processInstanceId;
    private String processId;
    private String nodeInstanceId;
    private String nodeId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "log_date")
    private Date date;
    
    NodeInstanceLog() {
    }
    
	public NodeInstanceLog(int type, long processInstanceId, String processId,
			               String nodeInstanceId, String nodeId) {
		this.type = type;
        this.processInstanceId = processInstanceId;
        this.processId = processId;
		this.nodeInstanceId = nodeInstanceId;
		this.nodeId = nodeId;
        this.date = new Date();
    }
	
	public int getType() {
		return type;
	}
	
	void setType(int type) {
		this.type = type;
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

    public String getNodeInstanceId() {
		return nodeInstanceId;
	}

	void setNodeInstanceId(String nodeInstanceId) {
		this.nodeInstanceId = nodeInstanceId;
	}

	public String getNodeId() {
		return nodeId;
	}

	void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public Date getDate() {
        return date;
    }
    
	void setDate(Date date) {
		this.date = date;
	}

    public String toString() {
        return (type == 0 ? "Triggered " : "Left ") + "Node Instance '" + 
        	processId + "#" + nodeId + "' [" + processInstanceId + "#" + nodeInstanceId + "]";
    }
    
}
