package org.drools.task;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.drools.task.utils.CollectionUtils;

@Embeddable
public class TaskPresentationElements implements Serializable {
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "TaskPresentationElements_Names_Id", nullable = true)    
    private List<IL8NText> names        = Collections.emptyList();
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "TaskPresentationElements_Subjects_Id", nullable = true)    
    private List<IL8NText> subjects     = Collections.emptyList();
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "TaskPresentationElements_Descriptions_Id", nullable = true)
    @Lob
    private List<IL8NText> descriptions = Collections.emptyList();  
    
    public TaskPresentationElements() {
        
    }


    public List<IL8NText> getNames() {
        return names;
    }

    public void setNames(List<IL8NText> names) {
        this.names = names;
    }

    public List<IL8NText> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<IL8NText> subjects) {
        this.subjects = subjects;
    }
    
    public List<IL8NText> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<IL8NText> descriptions) {
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
        if ( !(obj instanceof TaskPresentationElements) ) return false;
        TaskPresentationElements other = (TaskPresentationElements) obj;
        
        return CollectionUtils.equals( descriptions, other.descriptions ) && CollectionUtils.equals( names, other.names )
        && CollectionUtils.equals( subjects, other.subjects );
    }
    
    
}
