package org.drools.task;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Task implements Serializable {
    /**
     * WSHT uses a name for the unique identifier, for now we use a generated ID which is also the key, which can be 
     * mapped to the name or a unique name field added later.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)        
    private long                 id;

    /**
     * While WSHT says this is an expression, it always resolves to an integer, so resolve before setting
     * default value is 0.
     */
    private int                  priority;

    @Embedded
    private PeopleAssignments    peopleAssignments;
    

    @Embedded    
    private Delegation           delegation;

    @Embedded
    private TaskPresentationElements presentationElements;

    @Embedded
    private TaskData             taskData;   
    
    @Embedded 
    private Deadlines            deadlines;
    
    public Task() {
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public PeopleAssignments getPeopleAssignments() {
        return peopleAssignments;
    }

    public void setPeopleAssignments(PeopleAssignments peopleAssignments) {
        this.peopleAssignments = peopleAssignments;
    }

    public Delegation getDelegation() {
        return delegation;
    }

    public void setDelegation(Delegation delegation) {
        this.delegation = delegation;
    }


    public TaskPresentationElements getTaskPresentationElements() {
        return presentationElements;
    }

    public void setTaskPresentationElements(TaskPresentationElements presentationElements) {
        this.presentationElements = presentationElements;
    }

    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }

    public Deadlines getDeadlines() {
        return deadlines;
    }

    public void setDeadlines(Deadlines deadlines) {
        this.deadlines = deadlines;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deadlines == null) ? 0 : deadlines.hashCode());
        result = prime * result + ((delegation == null) ? 0 : delegation.hashCode());
        result = prime * result + ((peopleAssignments == null) ? 0 : peopleAssignments.hashCode());
        result = prime * result + ((presentationElements == null) ? 0 : presentationElements.hashCode());
        result = prime * result + priority;
        result = prime * result + ((taskData == null) ? 0 : taskData.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof Task) ) return false;
        Task other = (Task) obj;
        if ( deadlines == null ) {
            if ( other.deadlines != null ) return false;
        } else if ( !deadlines.equals( other.deadlines ) ) return false;
        if ( delegation == null ) {
            if ( other.delegation != null ) return false;
        } else if ( !delegation.equals( other.delegation ) ) return false;
        if ( peopleAssignments == null ) {
            if ( other.peopleAssignments != null ) return false;
        } else if ( !peopleAssignments.equals( other.peopleAssignments ) ) return false;
        if ( presentationElements == null ) {
            if ( other.presentationElements != null ) return false;
        } else if ( !presentationElements.equals( other.presentationElements ) ) return false;
        if ( priority != other.priority ) return false;
        if ( taskData == null ) {
            if ( other.taskData != null ) return false;
        } else if ( !taskData.equals( other.taskData ) ) return false;
        return true;
    }        
    
    
}
