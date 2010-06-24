package org.drools.process.audit;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class JPAWorkingMemoryDbLoggerTest extends TestCase {

    PoolingDataSource ds1;

    @Override
    protected void setUp() throws Exception {
        ds1 = new PoolingDataSource();
        ds1.setUniqueName( "jdbc/testDS1" );
        ds1.setClassName( "org.h2.jdbcx.JdbcDataSource" );
        ds1.setMaxPoolSize( 3 );
        ds1.setAllowLocalTransactions( true );
        ds1.getDriverProperties().put( "user",
                                       "sa" );
        ds1.getDriverProperties().put( "password",
                                       "sasa" );
        ds1.getDriverProperties().put( "URL",
                                       "jdbc:h2:mem:mydb" );
        ds1.init();

    }

    @Override
    protected void tearDown() throws Exception {
        ds1.close();
    }

	public void testLogger1() throws Exception {
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 TransactionManagerServices.getTransactionManager() );
        StatefulKnowledgeSession session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        new JPAWorkingMemoryDbLogger(session);
        JPAProcessInstanceDbLog log = new JPAProcessInstanceDbLog();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        
        System.out.println("Checking process instances for process 'com.sample.ruleflow'");
        List<ProcessInstanceLog> processInstances =
        	log.findProcessInstances("com.sample.ruleflow");
        assertEquals(1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(0);
        System.out.print(processInstance);
        System.out.println(" -> " + processInstance.getStart() + " - " + processInstance.getEnd());
        assertNotNull(processInstance.getStart());
        assertNotNull(processInstance.getEnd());
        assertEquals(processInstanceId, processInstance.getProcessInstanceId());
        assertEquals("com.sample.ruleflow", processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = log.findNodeInstances(processInstanceId);
        assertEquals(6, nodeInstances.size());
        for (NodeInstanceLog nodeInstance: nodeInstances) {
        	System.out.println(nodeInstance);
            assertEquals(processInstanceId, processInstance.getProcessInstanceId());
            assertEquals("com.sample.ruleflow", processInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        log.clear();
        processInstances = log.findProcessInstances("com.sample.ruleflow");
        assertEquals(0, processInstances.size());
        log.dispose();
	}
	
	public void testLogger2() {
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 TransactionManagerServices.getTransactionManager() );
        StatefulKnowledgeSession session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        new JPAWorkingMemoryDbLogger(session);
        JPAProcessInstanceDbLog log = new JPAProcessInstanceDbLog();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // start process instance
        session.startProcess("com.sample.ruleflow");
        session.startProcess("com.sample.ruleflow");
        
        System.out.println("Checking process instances for process 'com.sample.ruleflow'");
        List<ProcessInstanceLog> processInstances =
        	log.findProcessInstances("com.sample.ruleflow");
        assertEquals(2, processInstances.size());
        for (ProcessInstanceLog processInstance: processInstances) {
            System.out.print(processInstance);
            System.out.println(" -> " + processInstance.getStart() + " - " + processInstance.getEnd());
            List<NodeInstanceLog> nodeInstances = log.findNodeInstances(processInstance.getProcessInstanceId());
            for (NodeInstanceLog nodeInstance: nodeInstances) {
            	System.out.print(nodeInstance);
                System.out.println(" -> " + nodeInstance.getDate());
            }
            assertEquals(6, nodeInstances.size());
        }
        log.clear();
        log.dispose();
	}
	
	public void testLogger3() {
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 TransactionManagerServices.getTransactionManager() );
        StatefulKnowledgeSession session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        new JPAWorkingMemoryDbLogger(session);
        JPAProcessInstanceDbLog log = new JPAProcessInstanceDbLog();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow2").getId();
        
        System.out.println("Checking process instances for process 'com.sample.ruleflow2'");
        List<ProcessInstanceLog> processInstances =
        	log.findProcessInstances("com.sample.ruleflow2");
        assertEquals(1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(0);
        System.out.print(processInstance);
        System.out.println(" -> " + processInstance.getStart() + " - " + processInstance.getEnd());
        assertNotNull(processInstance.getStart());
        assertNotNull(processInstance.getEnd());
        assertEquals(processInstanceId, processInstance.getProcessInstanceId());
        assertEquals("com.sample.ruleflow2", processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = log.findNodeInstances(processInstanceId);
        for (NodeInstanceLog nodeInstance: nodeInstances) {
        	System.out.print(nodeInstance);
            System.out.println(" -> " + nodeInstance.getDate());
            assertEquals(processInstanceId, processInstance.getProcessInstanceId());
            assertEquals("com.sample.ruleflow2", processInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        assertEquals(14, nodeInstances.size());
        log.clear();
        log.dispose();
	}
	
    private KnowledgeBase createKnowledgeBase() {
    	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    	kbuilder.add(new ClassPathResource("ruleflow.rf"), ResourceType.DRF);
    	kbuilder.add(new ClassPathResource("ruleflow2.rf"), ResourceType.DRF);
    	KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
    	kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }
}
