package org.drools.persistence.processinstance;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.drools.WorkingMemory;
import org.drools.process.instance.event.DefaultSignalManager;

public class JPASignalManager extends DefaultSignalManager {

    private EntityManager manager;
    private WorkingMemory workingMemory;
    
    public JPASignalManager(WorkingMemory workingMemory) {
    	this.workingMemory = workingMemory;
    }
    
    public void setEntityManager(EntityManager manager) {
        this.manager = manager;
    }
    
    public void signalEvent(String type, Object event) {
    	for (long id: getProcessInstancesForEvent(type)) {
    		workingMemory.getProcessInstance(id);
    	}
    	super.signalEvent(type, event);
    }
    
    @SuppressWarnings("unchecked")
	private List<Long> getProcessInstancesForEvent(String type) {
    	Query processInstancesForEvent = manager.createNamedQuery("ProcessInstancesWaitingForEvent");
		processInstancesForEvent.setParameter("type", type);
		List<Long> list = (List<Long>) processInstancesForEvent.getResultList();
		return list;
    }
    
}