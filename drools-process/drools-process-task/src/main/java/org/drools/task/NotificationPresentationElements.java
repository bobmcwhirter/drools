package org.drools.task;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import org.drools.task.utils.CollectionUtils;

@Embeddable
public class NotificationPresentationElements implements Serializable {
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "NotificationPresentationElements_Names_Id", nullable = true)    
    private List<I18NText> names        = Collections.emptyList();
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "NotificationPresentationElements_Subjects_Id", nullable = true)    
    private List<I18NText> subjects     = Collections.emptyList();
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "NotificationPresentationElements_Descriptions_Id", nullable = true)
    @Lob
    private List<I18NText> descriptions = Collections.emptyList();  
    
    public NotificationPresentationElements() {
        
    }


    public List<I18NText> getNames() {
        return names;
    }

    public void setNames(List<I18NText> names) {
        this.names = names;
    }

    public List<I18NText> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<I18NText> subjects) {
        this.subjects = subjects;
    }
    
    public List<I18NText> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<I18NText> descriptions) {
        this.descriptions = descriptions;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + CollectionUtils.hashCode( descriptions );
        result = prime * result + CollectionUtils.hashCode( names );
        result = prime * result + CollectionUtils.hashCode( subjects );
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof NotificationPresentationElements) ) return false;
        NotificationPresentationElements other = (NotificationPresentationElements) obj;
        
        return CollectionUtils.equals( descriptions, other.descriptions ) && CollectionUtils.equals( names, other.names )
        && CollectionUtils.equals( subjects, other.subjects );
    }
    
    
}
