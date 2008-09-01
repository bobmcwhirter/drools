package org.drools.task;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.drools.task.utils.CollectionUtils;

@Embeddable
public class Delegation  implements Externalizable {
    @Enumerated(EnumType.STRING)      
    private Allowed                    allowed;
    
    @ManyToMany
    @JoinTable(name = "Delegation_delegates", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "entity_id"))    
    private List<OrganizationalEntity> delegates = Collections.emptyList();
    
    public void writeExternal(ObjectOutput out) throws IOException {
        if ( allowed != null ) {
            out.writeBoolean( true );
            out.writeUTF( allowed.toString() );
        } else {
            out.writeBoolean( false );
        }
        CollectionUtils.writeOrganizationalEntityList( delegates, out );       
    } 
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        allowed = Allowed.valueOf( in.readUTF() );
        delegates = CollectionUtils.readOrganizationalEntityList( in );
    }       

    public Allowed getAllowed() {
        return allowed;
    }    
    
    public void setAllowed(Allowed allowed) {
        this.allowed = allowed;
    }

    public List<OrganizationalEntity> getDelegates() {
        return delegates;
    }
    

    public void setDelegates(List<OrganizationalEntity> delegates) {
        this.delegates = delegates;
    }    
    
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((allowed == null) ? 0 : allowed.hashCode());
        result = prime * result + CollectionUtils.hashCode( delegates );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof Delegation) ) return false;
        Delegation other = (Delegation) obj;
        if ( allowed == null ) {
            if ( other.allowed != null ) return false;
        } else if ( !allowed.equals( other.allowed ) ) return false;
        
        return CollectionUtils.equals( delegates, other.delegates );
    }
}
