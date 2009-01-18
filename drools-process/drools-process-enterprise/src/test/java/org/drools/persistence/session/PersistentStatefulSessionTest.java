package org.drools.persistence.session;

import java.util.Properties;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;

public class PersistentStatefulSessionTest extends TestCase {
	
	public void testPersistenceWorkItems() {
		Properties properties = new Properties();
		properties.setProperty("drools.commandService", "org.drools.persistence.session.SingleSessionCommandService");
		properties.setProperty("drools.processInstanceManagerFactory", "org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory");
		properties.setProperty("drools.workItemManagerFactory", "org.drools.persistence.processinstance.JPAWorkItemManagerFactory");
		properties.setProperty("drools.processSignalManagerFactory", "org.drools.persistence.processinstance.JPASignalManagerFactory");
		KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ClassPathResource("WorkItemsProcess.rf"), ResourceType.DRF);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession(config);
        ProcessInstance processInstance = session.startProcess("org.drools.test.TestProcess");
        session.insert("TestString");
        System.out.println("Started process instance " + processInstance.getId());
        
        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        
        session = kbase.newStatefulKnowledgeSession(config);
        processInstance = session.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        session = kbase.newStatefulKnowledgeSession(config);
        session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        
        session = kbase.newStatefulKnowledgeSession(config);
        processInstance = session.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        session = kbase.newStatefulKnowledgeSession(config);
        session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        
        session = kbase.newStatefulKnowledgeSession(config);
        processInstance = session.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        session = kbase.newStatefulKnowledgeSession(config);
        session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        
        workItem = handler.getWorkItem();
        assertNull(workItem);
        
        session = kbase.newStatefulKnowledgeSession(config);
        processInstance = session.getProcessInstance(processInstance.getId());
        assertEquals(1, session.getObjects().size());
        for (Object o: session.getObjects()) {
        	System.out.println(o);
        }
        assertNull(processInstance);
	}
    
}
