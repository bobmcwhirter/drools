package org.drools.process.audit;

import java.util.Date;

public class NodeInstanceLog {
    
	public static final int TYPE_ENTER = 0;
	public static final int TYPE_EXIT = 1;
	
	private String id;
    private int type;
    private long processInstanceId;
    private String processId;
    private String nodeInstanceId;
    private String nodeId;
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
    
    public String getId() {
    	return id;
    }
    
    void setId(String id) {
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
