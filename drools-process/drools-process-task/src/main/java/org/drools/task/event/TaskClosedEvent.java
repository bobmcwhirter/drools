package org.drools.task.event;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TaskClosedEvent extends TaskEvent implements Externalizable {
    private String userId;
    
    public TaskClosedEvent(long taskId, String userId) {
        super( taskId );
        this.userId = userId;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(  userId  );
    }   
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        userId = in.readUTF();
    }


    public String getUserId() {
        return userId;
    }

}
