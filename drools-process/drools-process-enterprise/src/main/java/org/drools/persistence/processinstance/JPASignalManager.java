package org.drools.persistence.processinstance;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.drools.WorkingMemory;
import org.drools.process.instance.event.DefaultSignalManager;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.process.ProcessInstance;

public class JPASignalManager extends DefaultSignalManager {

    private WorkingMemory workingMemory;

    public JPASignalManager(WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }
    
    public void signalEvent(String type,
                            Object event) {
        for ( long id : getProcessInstancesForEvent( type ) ) {
            workingMemory.getProcessInstance( id );
        }
        super.signalEvent( type,
                           event );
    }

    public void signalEvent(ProcessInstance processInstance,
                            String type,
                            Object event) {
        //		Transaction transaction = persister.getTransaction();
        //		try {
        //			transaction.start();
        //	    	super.signalEvent(processInstance, type, event);
        //			transaction.commit();
        //		} catch (Throwable t) {
        //			t.printStackTrace();
        //			try {
        //				transaction.rollback();
        //				throw new RuntimeException("Could not execute command", t);
        //			} catch (XAException e) {
        //				throw new RuntimeException("Could not rollback transaction", e);
        //			}
        //		}
    }

    @SuppressWarnings("unchecked")
    private List<Long> getProcessInstancesForEvent(String type) {
        EntityManager em = (EntityManager) this.workingMemory.getEnvironment().get( EnvironmentName.ENTITY_MANAGER );
        
        Query processInstancesForEvent = em.createNamedQuery( "ProcessInstancesWaitingForEvent" );
        processInstancesForEvent.setParameter( "type",
                                               type );
        List<Long> list = (List<Long>) processInstancesForEvent.getResultList();
        return list;
    }

}