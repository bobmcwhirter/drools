package org.drools.task;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public abstract class OrganizationalEntity implements Externalizable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;    
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong( id );
        
    } 
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        id = in.readLong();
    }      
    
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

  
}
