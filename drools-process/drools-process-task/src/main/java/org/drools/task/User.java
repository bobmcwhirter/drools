package org.drools.task;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
public class User extends OrganizationalEntity implements Externalizable{
    private String displayName;
    
    public User() {
        super();
    }
    
    public User(String displayName) {
        super();
        this.displayName = displayName;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        if (displayName != null) {
        	out.writeBoolean( true );
        	out.writeUTF( displayName );
        } else {
        	out.writeBoolean( false );
        }
    } 
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        if ( in.readBoolean() ) {
        	displayName = in.readUTF();
        }        
    }      
    
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof User) ) return false;
        User other = (User) obj;
        if ( displayName == null ) {
            if ( other.displayName != null ) return false;
        } else if ( !displayName.equals( other.displayName ) ) return false;
        return true;
    }



    
        
}
