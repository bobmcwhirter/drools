package org.drools.task;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.drools.task.utils.CollectionUtils;

@Embeddable
public class PeopleAssignments implements Serializable {
    @ManyToOne()
    private User                       taskInitiator;

    @OneToMany
    @JoinColumn(name = "PeopleAssignments_PotentialOwners_Id", nullable = true)
    private List<OrganizationalEntity> potentialOwners        = Collections.emptyList();

    @OneToMany
    @JoinColumn(name = "PeopleAssignments_ExcludedOwners_Id", nullable = true)
    private List<OrganizationalEntity> excludedOwners         = Collections.emptyList();

    @OneToMany
    @JoinColumn(name = "PeopleAssignments_TaskStakeholders_Id", nullable = true)
    private List<OrganizationalEntity> taskStakeholders       = Collections.emptyList();

    @OneToMany
    @JoinColumn(name = "PeopleAssignments_BusinessAdministrators_Id", nullable = true)
    private List<OrganizationalEntity> businessAdministrators = Collections.emptyList();

    @OneToMany
    @JoinColumn(name = "PeopleAssignments_Recipients_Id", nullable = true)
    private List<OrganizationalEntity> recipients             = Collections.emptyList();
    
    public PeopleAssignments() {
        
    }

    public User getTaskInitiator() {
        return taskInitiator;
    }

    public void setTaskInitiator(User taskInitiator) {
        this.taskInitiator = taskInitiator;
    }

    public List<OrganizationalEntity> getPotentialOwners() {
        return potentialOwners;
    }

    public void setPotentialOwners(List<OrganizationalEntity> potentialOwners) {
        this.potentialOwners = potentialOwners;
    }

    public List<OrganizationalEntity> getExcludedOwners() {
        return excludedOwners;
    }

    public void setExcludedOwners(List<OrganizationalEntity> excludedOwners) {
        this.excludedOwners = excludedOwners;
    }

    public List<OrganizationalEntity> getTaskStakeholders() {
        return taskStakeholders;
    }

    public void setTaskStakeholders(List<OrganizationalEntity> taskStakeholders) {
        this.taskStakeholders = taskStakeholders;
    }

    public List<OrganizationalEntity> getBusinessAdministrators() {
        return businessAdministrators;
    }

    public void setBusinessAdministrators(List<OrganizationalEntity> businessAdministrators) {
        this.businessAdministrators = businessAdministrators;
    }

    public List<OrganizationalEntity> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<OrganizationalEntity> recipients) {
        this.recipients = recipients;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + CollectionUtils.hashCode( businessAdministrators );
        result = prime * result + CollectionUtils.hashCode( excludedOwners );
        result = prime * result + CollectionUtils.hashCode( potentialOwners );
        result = prime * result + CollectionUtils.hashCode( recipients );
        result = prime * result + ((taskInitiator == null) ? 0 : taskInitiator.hashCode());
        result = prime * result + CollectionUtils.hashCode( taskStakeholders );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof PeopleAssignments) ) return false;
        PeopleAssignments other = (PeopleAssignments) obj;
        
        if ( taskInitiator == null ) {
            if ( other.taskInitiator != null ) return false;
        } else if ( !taskInitiator.equals( other.taskInitiator ) ) return false;
        
        return CollectionUtils.equals( businessAdministrators, other.businessAdministrators ) && CollectionUtils.equals( excludedOwners, other.excludedOwners )
        && CollectionUtils.equals( potentialOwners, other.potentialOwners ) && CollectionUtils.equals( recipients, other.recipients )
        && CollectionUtils.equals( taskStakeholders, other.taskStakeholders );
    }
    
    
}
