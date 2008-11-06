package org.drools.persistence.processinstance;

import org.drools.WorkingMemory;
import org.drools.process.instance.WorkItemManagerFactory;
import org.drools.runtime.process.WorkItemManager;

public class JPAWorkItemManagerFactory implements WorkItemManagerFactory {

	public WorkItemManager createWorkItemManager(WorkingMemory workingMemory) {
		return new JPAWorkItemManager(workingMemory);
	}

}
