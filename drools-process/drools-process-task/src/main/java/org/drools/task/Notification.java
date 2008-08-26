package org.drools.task;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.drools.task.utils.CollectionUtils;

@Entity
public class Notification implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long                             id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "Notification_Documentation_Id", nullable = true)
    private List<IL8NText>                   documentation = Collections.emptyList();

    private int                              priority;

    @OneToMany
    @JoinColumn(name = "Notification_Recipients_Id", nullable = true)
    private List<OrganizationalEntity>       recipients = Collections.emptyList();;

    @OneToMany
    @JoinColumn(name = "Notification_BusinessAdministrators_Id", nullable = true)
    private List<OrganizationalEntity>       businessAdministrators = Collections.emptyList();

    @Embedded
    private NotificationPresentationElements presentationElements;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<IL8NText> getDocumentation() {
        return documentation;
    }

    public void setDocumentation(List<IL8NText> documentation) {
        this.documentation = documentation;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<OrganizationalEntity> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<OrganizationalEntity> recipients) {
        this.recipients = recipients;
    }

    public List<OrganizationalEntity> getBusinessAdministrators() {
        return businessAdministrators;
    }

    public void setBusinessAdministrators(List<OrganizationalEntity> businessAdministrators) {
        this.businessAdministrators = businessAdministrators;
    }

    public NotificationPresentationElements getNotificationPresentationElements() {
        return presentationElements;
    }

    public void setNotificationPresentationElements(NotificationPresentationElements presentationElements) {
        this.presentationElements = presentationElements;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + CollectionUtils.hashCode( businessAdministrators );
        result = prime * result + CollectionUtils.hashCode( documentation );
        result = prime * result + ((presentationElements == null) ? 0 : presentationElements.hashCode());
        result = prime * result + priority;
        result = prime * result + CollectionUtils.hashCode( recipients );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof Notification) ) return false;
        Notification other = (Notification) obj;
        
        if ( presentationElements == null ) {
            if ( other.presentationElements != null ) return false;
        } else if ( !presentationElements.equals( other.presentationElements ) ) return false;
        
        return CollectionUtils.equals( businessAdministrators, other.businessAdministrators ) && CollectionUtils.equals( documentation, other.documentation )
        && CollectionUtils.equals( recipients, other.recipients );
    }
                
}
