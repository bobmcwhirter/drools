package org.drools.task;

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
import javax.persistence.OneToMany;

import org.drools.task.utils.CollectionUtils;

@Embeddable
public class Delegation implements Serializable {
    @Enumerated(EnumType.STRING)      
    private Allowed                    allowed;
    
    @OneToMany
    @JoinColumn(name = "Delegates_Id", nullable = true)
    private List<OrganizationalEntity> delegates = Collections.emptyList();

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
