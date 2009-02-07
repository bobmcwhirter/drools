package org.drools.persistence.processinstance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.drools.marshalling.MarshallerReaderContext;
import org.drools.marshalling.MarshallerWriteContext;
import org.drools.marshalling.ProcessInstanceMarshaller;
import org.drools.marshalling.ProcessMarshallerRegistry;
import org.drools.process.instance.impl.ProcessInstanceImpl;
import org.drools.runtime.process.ProcessInstance;
import org.hibernate.annotations.CollectionOfElements;

@Entity
public class ProcessInstanceInfo {

    private @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long                processInstanceId;
    private String      processId;
    private Date        startDate;
    private Date        lastReadDate;
    private Date        lastModificationDate;
    private int         state;

    private @Lob
    byte[]              processInstanceByteArray;

    private @Transient
    @Lob
    byte[]              processInstanceByteArrayShadow;

    @CollectionOfElements
    private Set<String> eventTypes = new HashSet<String>();

    private @Transient
    ProcessInstance     processInstance;

    ProcessInstanceInfo() {
    }

    public ProcessInstanceInfo(ProcessInstance processInstance) {
        this.processInstance = processInstance;
        this.processId = processInstance.getProcessId();
        startDate = new Date();
    }

    public long getId() {
        return processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public Date getLastReadDate() {
        return lastReadDate;
    }

    public void updateLastReadDate() {
        lastReadDate = new Date();
    }

    public int getState() {
        return state;
    }

    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    private ProcessInstanceMarshaller getMarshallerFromContext(MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;
        String processInstanceType = stream.readUTF();
        return ProcessMarshallerRegistry.INSTANCE.getMarshaller( processInstanceType );
    }

    private void saveProcessInstanceType(MarshallerWriteContext context,
                                         ProcessInstance processInstance,
                                         String processInstanceType) throws IOException {
        ObjectOutputStream stream = context.stream;
        // saves the processInstance type first
        stream.writeUTF( processInstanceType );
    }

    @PostLoad
    public void postLoad() {
        if ( processInstance == null ) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream( processInstanceByteArray );
                MarshallerReaderContext context = new MarshallerReaderContext( bais,
                                                                               null,
                                                                               null,
                                                                               null );
                ProcessInstanceMarshaller marshaller = getMarshallerFromContext( context );
                processInstance = marshaller.readProcessInstance( context );
                context.close();
            } catch ( IOException e ) {
                e.printStackTrace();
                throw new IllegalArgumentException( "IOException while loading process instance: " + e.getMessage() );
            }
        }
        // reference it as a shadow field, this can be used to check for changes and do rollbacks without hitting the DB.
        this.processInstanceByteArrayShadow = this.processInstanceByteArray;
        this.processInstanceByteArray = null; // setting this to null will force an update
    }

    @PreUpdate
    // executes before update
    @PrePersist
    // executes before insert
    public void update() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            MarshallerWriteContext context = new MarshallerWriteContext( baos,
                                                                         null,
                                                                         null,
                                                                         null,
                                                                         null );
            String processType = ((ProcessInstanceImpl) processInstance).getProcess().getType();
            saveProcessInstanceType( context,
                                     processInstance,
                                     processType );
            ProcessInstanceMarshaller marshaller = ProcessMarshallerRegistry.INSTANCE.getMarshaller( processType );
            marshaller.writeProcessInstance( context,
                                             processInstance );
            context.close();
        } catch ( IOException e ) {
            throw new IllegalArgumentException( "IOException while storing process instance " + processInstance.getId() + ": " + e.getMessage() );
        }
        byte[] newByteArray = baos.toByteArray();
        if ( !Arrays.equals( newByteArray,
                             processInstanceByteArrayShadow ) ) {
            this.state = processInstance.getState();
            this.lastModificationDate = new Date();
            this.processInstanceByteArray = newByteArray;
            this.processInstanceByteArrayShadow = newByteArray;
            this.eventTypes.clear();
            for ( String type : processInstance.getEventTypes() ) {
                eventTypes.add( type );
            }
        } else {
            // there was no change so restore for the perist process
            this.processInstanceByteArray = this.processInstanceByteArrayShadow;
        }
    }

    @PostUpdate
    // executes after update
    @PostPersist
    // executes after insert
    public void postUpdate() {
        // we need to null this to force an update check
        this.processInstanceByteArray = null;
    }

    public void rollback() {
        // restore the processInstanceByteArray
        this.processInstanceByteArray = this.processInstanceByteArrayShadow;

        //Simulate load which forces rebuilding of the process from the shadow
        postLoad();
    }

    public void commit() {
        this.processInstanceByteArrayShadow = processInstanceByteArray;
    }
}
