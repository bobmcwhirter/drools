package org.drools.persistence.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.EnvironmentImpl;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class PersistentStatefulSessionTest extends TestCase {

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

    public void testLocalTransactionPerStatement() {

        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                "org.drools.persistence.session.SingleSessionCommandService" );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                "org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory" );
        properties.setProperty( "drools.workItemManagerFactory",
                                "org.drools.persistence.processinstance.JPAWorkItemManagerFactory" );
        properties.setProperty( "drools.processSignalManagerFactory",
                                "org.drools.persistence.processinstance.JPASignalManagerFactory" );
        KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration( properties );

        String str = "";
        str += "package org.drools.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );

        env.set( "drools.TransactionManager",
                 TransactionManagerServices.getTransactionManager() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( config,
                                                                               env );
        List list = new ArrayList();

        ksession.setGlobal( "list",
                            list );

        ksession.insert( 1 );
        ksession.insert( 2 );
        ksession.insert( 3 );

        ksession.fireAllRules();

        assertEquals( 3,
                      list.size() );

    }

    public void testUserTransactions() throws Exception {

        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                "org.drools.persistence.session.SingleSessionCommandService" );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                "org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory" );
        properties.setProperty( "drools.workItemManagerFactory",
                                "org.drools.persistence.processinstance.JPAWorkItemManagerFactory" );
        properties.setProperty( "drools.processSignalManagerFactory",
                                "org.drools.persistence.processinstance.JPASignalManagerFactory" );
        KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration( properties );

        String str = "";
        str += "package org.drools.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        env.set( "drools.TransactionManager",
                 TransactionManagerServices.getTransactionManager() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( config,
                                                                               env );

        //      EntityManager em = emf.createEntityManager();
        //      SessionInfo sInfo = em.find( SessionInfo.class, 1 );
        //      assertNotNull( sInfo );
        //      //System.out.println( "session creation : " + sInfo.getVersion() );
        //      em.close();

        List list = new ArrayList();

        // insert and commit
        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( 1 );
        ksession.insert( 2 );
        ut.commit();

        // insert and rollback
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 3 );
        ut.rollback();

        // check we rolled back the state changes from the 3rd insert
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );

        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 3 );
        ksession.insert( 4 );
        ut.commit();

        // rollback again, this is testing that we can do consequetive rollbacks and commits without issue
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 5 );
        ksession.insert( 6 );
        ut.rollback();

        ksession.fireAllRules();

        assertEquals( 4,
                      list.size() );

    }

    public void testPersistenceWorkItems() {
        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                "org.drools.persistence.session.SingleSessionCommandService" );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                "org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory" );
        properties.setProperty( "drools.workItemManagerFactory",
                                "org.drools.persistence.processinstance.JPAWorkItemManagerFactory" );
        properties.setProperty( "drools.processSignalManagerFactory",
                                "org.drools.persistence.processinstance.JPASignalManagerFactory" );
        KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration( properties );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "WorkItemsProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession( config,
                                                                              env );
        ProcessInstance processInstance = session.startProcess( "org.drools.test.TestProcess" );
        session.insert( "TestString" );
        System.out.println( "Started process instance " + processInstance.getId() );

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );

        session = kbase.newStatefulKnowledgeSession( config,
                                                     null );
        processInstance = session.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        session = kbase.newStatefulKnowledgeSession( config,
                                                     null );
        session.getWorkItemManager().completeWorkItem( workItem.getId(),
                                                       null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        session = kbase.newStatefulKnowledgeSession( config,
                                                     null );
        processInstance = session.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        session = kbase.newStatefulKnowledgeSession( config,
                                                     null );
        session.getWorkItemManager().completeWorkItem( workItem.getId(),
                                                       null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        session = kbase.newStatefulKnowledgeSession( config,
                                                     null );
        processInstance = session.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        session = kbase.newStatefulKnowledgeSession( config,
                                                     null );
        session.getWorkItemManager().completeWorkItem( workItem.getId(),
                                                       null );

        workItem = handler.getWorkItem();
        assertNull( workItem );

        session = kbase.newStatefulKnowledgeSession( config,
                                                     null );
        processInstance = session.getProcessInstance( processInstance.getId() );
        assertEquals( 1,
                      session.getObjects().size() );
        for ( Object o : session.getObjects() ) {
            System.out.println( o );
        }
        assertNull( processInstance );

    }

    public void testPersistenceSubProcess() {
        Properties properties = new Properties();
        properties.setProperty( "drools.commandService",
                                "org.drools.persistence.session.SingleSessionCommandService" );
        properties.setProperty( "drools.processInstanceManagerFactory",
                                "org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory" );
        properties.setProperty( "drools.workItemManagerFactory",
                                "org.drools.persistence.processinstance.JPAWorkItemManagerFactory" );
        properties.setProperty( "drools.processSignalManagerFactory",
                                "org.drools.persistence.processinstance.JPASignalManagerFactory" );
        KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration( properties );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ClassPathResource( "SuperProcess.rf" ),
                      ResourceType.DRF );
        kbuilder.add( new ClassPathResource( "SubProcess.rf" ),
                      ResourceType.DRF );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession( config,
                                                                              env );
        ProcessInstance processInstance = session.startProcess( "com.sample.SuperProcess" );
        System.out.println( "Started process instance " + processInstance.getId() );

        TestWorkItemHandler handler = TestWorkItemHandler.getInstance();
        WorkItem workItem = handler.getWorkItem();
        assertNotNull( workItem );

        session = kbase.newStatefulKnowledgeSession( config,
                                                     null );
        processInstance = session.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        session = kbase.newStatefulKnowledgeSession( config,
                                                     null );
        session.getWorkItemManager().completeWorkItem( workItem.getId(),
                                                       null );

        workItem = handler.getWorkItem();
        assertNotNull( workItem );

        session = kbase.newStatefulKnowledgeSession( config,
                                                     null );
        processInstance = session.getProcessInstance( processInstance.getId() );
        assertNotNull( processInstance );

        session = kbase.newStatefulKnowledgeSession( config,
                                                     null );
        session.getWorkItemManager().completeWorkItem( workItem.getId(),
                                                       null );

        workItem = handler.getWorkItem();
        assertNull( workItem );

        session = kbase.newStatefulKnowledgeSession( config,
                                                     null );
        processInstance = session.getProcessInstance( processInstance.getId() );
        assertNull( processInstance );
    }

}
