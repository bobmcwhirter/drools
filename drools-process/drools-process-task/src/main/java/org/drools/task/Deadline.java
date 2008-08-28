package org.drools.task;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.drools.task.utils.CollectionUtils;

@Entity
public class Deadline implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)    
    private long             id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "Deadline_Documentation_Id", nullable = true)     
    private List<I18NText>   documentation = Collections.emptyList();

    private Date             date;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "Deadline_Escalation_Id", nullable = true)    
    private List<Escalation> escalations = Collections.emptyList();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<I18NText> getDocumentation() {
        return documentation;
    }

    public void setDocumentation(List<I18NText> documentation) {
        this.documentation = documentation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Escalation> getEscalations() {
        return escalations;
    }

    public void setEscalations(List<Escalation> escalations) {
        this.escalations = escalations;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + CollectionUtils.hashCode( documentation );
        result = prime * result + CollectionUtils.hashCode( escalations );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof Deadline) ) return false;
        Deadline other = (Deadline) obj;
        if ( date == null ) {
            if ( other.date != null ) return false;
        } else if ( date.getTime() != other.date.getTime() ) return false;
        
        return CollectionUtils.equals( documentation, other.documentation ) && CollectionUtils.equals( escalations, other.escalations );
    }
    
    
}
