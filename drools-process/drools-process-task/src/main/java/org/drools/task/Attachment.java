package org.drools.task;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

@Entity
public class Attachment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long   id;

    /**
     * Several attachments may have the same name
     */
    private String name;

    /**
     * current "inline" and "URL" are allowed, this is extendable though and others may be added
     */
    private AccessType accessType;

    /**
     * MIME type
     */
    private String contentType;

    private Date   attachedAt;

    @OneToOne
    private User   attachedBy;

    @Lob
    private byte[] attachment;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getAttachedAt() {
        return attachedAt;
    }

    public void setAttachedAt(Date attachedAt) {
        this.attachedAt = attachedAt;
    }

    public User getAttachedBy() {
        return attachedBy;
    }

    public void setAttachedBy(User attachedBy) {
        this.attachedBy = attachedBy;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }        
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessType == null) ? 0 : accessType.hashCode());
        result = prime * result + ((attachedAt == null) ? 0 : attachedAt.hashCode());
        result = prime * result + ((attachedBy == null) ? 0 : attachedBy.hashCode());
        result = prime * result + Arrays.hashCode( attachment );
        result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof Attachment) ) return false;
        Attachment other = (Attachment) obj;
        if ( accessType == null ) {
            if ( other.accessType != null ) return false;
        } else if ( !accessType.equals( other.accessType ) ) return false;
        if ( attachedAt == null ) {
            if ( other.attachedAt != null ) return false;            
        } else if ( attachedAt.getTime() != other.attachedAt.getTime() ) return false;
        if ( attachedBy == null ) {
            if ( other.attachedBy != null ) return false;
        } else if ( !attachedBy.equals( other.attachedBy ) ) return false;
        if ( !Arrays.equals( attachment,
                             other.attachment ) ) return false;
        if ( contentType == null ) {
            if ( other.contentType != null ) return false;
        } else if ( !contentType.equals( other.contentType ) ) return false;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        return true;
    }
    
    

}
