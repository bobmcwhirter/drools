package org.drools.task;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
public class Group extends OrganizationalEntity implements Serializable {    
      private String displayName;
      
      public Group() {
          super();
      }
      
      public Group(String displayName) {
          super();
          this.displayName = displayName;
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
        if ( !(obj instanceof Group) ) return false;
        Group other = (Group) obj;
        if ( displayName == null ) {
            if ( other.displayName != null ) return false;
        } else if ( !displayName.equals( other.displayName ) ) return false;
        return true;
    }


      
}
