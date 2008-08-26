package org.drools.task;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.drools.task.utils.CollectionUtils;

@Embeddable
public class TaskData implements Serializable {    
    @Enumerated(EnumType.STRING)    
    private Status           status = Status.Created; // initial default state

    private Date             createdOn;
    
    private Date             activationTime;
    
    private Date             expirationTime;
    
    private boolean          isSkipable;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "TaskData_Comments_Id", nullable = true)    
    private List<Comment>    comments = Collections.emptyList();
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "TaskData_Attachments_Id", nullable = true)    
    private List<Attachment> attachments = Collections.emptyList();   
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Date getCreatedOn() {
        return createdOn;
    }
    
    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
    
    public Date getActivationTime() {
        return activationTime;
    }
    
    public void setActivationTime(Date activationTime) {
        this.activationTime = activationTime;
    }
    
    public Date getExpirationTime() {
        return expirationTime;
    }
    
    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }
    
    public boolean isSkipable() {
        return isSkipable;
    }
    
    public void setSkipable(boolean isSkipable) {
        this.isSkipable = isSkipable;
    }
      
    public List<Comment> getComments() {
        return comments;
    }
    
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    
    public List<Attachment> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activationTime == null) ? 0 : activationTime.hashCode());
        result = prime * result + CollectionUtils.hashCode( attachments );
        result = prime * result + CollectionUtils.hashCode( comments );
        result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
        result = prime * result + ((expirationTime == null) ? 0 : expirationTime.hashCode());
        result = prime * result + (isSkipable ? 1231 : 1237);
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof TaskData) ) return false;
        TaskData other = (TaskData) obj;
        
        if ( createdOn == null ) {
            if ( other.createdOn != null ) return false;
            
        } else if ( createdOn.getTime() != other.createdOn.getTime() ) return false;
        if ( expirationTime == null ) {
            if ( other.expirationTime != null ) return false;            
        } else if ( expirationTime.getTime() != other.expirationTime.getTime() ) return false;
        if ( isSkipable != other.isSkipable ) return false;
        if ( status == null ) {
            if ( other.status != null ) return false;
        } else if ( !status.equals( other.status ) ) return false;        
        if ( activationTime == null ) {
            if ( other.activationTime != null ) return false;
        } else if ( activationTime.getTime() != other.activationTime.getTime() ) return false;                
        
        return CollectionUtils.equals( attachments, other.attachments ) && CollectionUtils.equals( comments, other.comments );
    }
    
    
}
