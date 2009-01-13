package org.drools.persistence.session;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.process.command.impl.LoggingInterceptor;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;

public class PersistentStatefulSessionTest extends TestCase {
	
	public void testPersistenceWorkItems() {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ClassPathResource("WorkItemsProcess.rf"), ResourceType.DRF);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        
        LoggingInterceptor interceptor = new LoggingInterceptor();
        SingleSessionCommandService commandService = new SingleSessionCommandService(kbase);
        interceptor.setNext(commandService);
        CommandBasedStatefulKnowledgeSession session = new CommandBasedStatefulKnowledgeSession(interceptor);
        ProcessInstance processInstance = session.startProcess("org.drools.test.TestProcess");
        System.out.println("Started process instance " + processInstance.getId());
        String sessionId = commandService.getSessionId();
        
        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        
        interceptor = new LoggingInterceptor();
        commandService = new SingleSessionCommandService(kbase, sessionId);
        interceptor.setNext(commandService);
        session = new CommandBasedStatefulKnowledgeSession(interceptor);
        processInstance = session.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        interceptor = new LoggingInterceptor();
        commandService = new SingleSessionCommandService(kbase, sessionId);
        interceptor.setNext(commandService);
        session = new CommandBasedStatefulKnowledgeSession(interceptor);
        session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        
        interceptor = new LoggingInterceptor();
        commandService = new SingleSessionCommandService(kbase, sessionId);
        interceptor.setNext(commandService);
        session = new CommandBasedStatefulKnowledgeSession(interceptor);
        processInstance = session.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        interceptor = new LoggingInterceptor();
        commandService = new SingleSessionCommandService(kbase, sessionId);
        interceptor.setNext(commandService);
        session = new CommandBasedStatefulKnowledgeSession(interceptor);
        session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        
        interceptor = new LoggingInterceptor();
        commandService = new SingleSessionCommandService(kbase, sessionId);
        interceptor.setNext(commandService);
        session = new CommandBasedStatefulKnowledgeSession(interceptor);
        processInstance = session.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        interceptor = new LoggingInterceptor();
        commandService = new SingleSessionCommandService(kbase, sessionId);
        interceptor.setNext(commandService);
        session = new CommandBasedStatefulKnowledgeSession(interceptor);
        session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        
        workItem = handler.getWorkItem();
        assertNull(workItem);
        
        interceptor = new LoggingInterceptor();
        commandService = new SingleSessionCommandService(kbase, sessionId);
        interceptor.setNext(commandService);
        session = new CommandBasedStatefulKnowledgeSession(interceptor);
        processInstance = session.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
	}
    
}
