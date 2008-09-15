package org.drools.task.event;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TaskClaimedEvent extends TaskEvent implements Externalizable {
    private String userId;
    
    public TaskClaimedEvent() {
        super();
    }
    
    public TaskClaimedEvent(long taskId, String userId) {
        super( taskId );
        this.userId = userId;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeUTF( userId );
    }  
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal( in );
        userId = in.readUTF();
    }
    
    public String getUserId() {
        return userId;
    }
        
}
