package org.drools.bpmn2;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.bpmn2.handler.ReceiveTaskHandler;
import org.drools.bpmn2.handler.SendTaskHandler;
import org.drools.bpmn2.handler.ServiceTaskHandler;
import org.drools.bpmn2.xml.BPMN2SemanticModule;
import org.drools.bpmn2.xml.BPMNDISemanticModule;
import org.drools.bpmn2.xml.BPMNSemanticModule;
import org.drools.bpmn2.xml.XmlBPMNProcessDumper;
import org.drools.bpmn2.xpath.XPathDialectConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.drools.xml.XmlProcessReader;
import org.w3c.dom.Document;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class SimpleBPMNProcessTest extends TestCase {

	public void testMinimalProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-MinimalProcess.xml");
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		ProcessInstance processInstance = ksession.startProcess("Minimal");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testMinimalProcessWithGraphical() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-MinimalProcessWithGraphical.xml");
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		ProcessInstance processInstance = ksession.startProcess("Minimal");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testEvaluationProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess.xml");
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("RegisterRequest", new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "UserId-12345");
		ProcessInstance processInstance = ksession.startProcess("Evaluation", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testEvaluationProcess2() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess2.xml");
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "UserId-12345");
		ProcessInstance processInstance = ksession.startProcess("com.sample.evaluation", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testEvaluationProcess3() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess3.xml");
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("RegisterRequest", new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "john2");
		ProcessInstance processInstance = ksession.startProcess("Evaluation", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}
	
    public void testUserTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-UserTask.xml");
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }

	public void testExclusiveSplit() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplit.xml");
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "First");
		params.put("y", "Second");
		ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}
	
	public void testExclusiveSplitXPath() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitXPath.xml");
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(new ByteArrayInputStream(
                "<myDocument><chapter1>BlaBla</chapter1><chapter2>MoreBlaBla</chapter2></myDocument>".getBytes()));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", document);
        params.put("y", "SomeString");
        ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }

	public void testCallActivity() throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
        ((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNSemanticModule());
        ((PackageBuilderConfiguration) conf).addSemanticModule(new BPMN2SemanticModule());
        ((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNDISemanticModule());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
		kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-CallActivity.xml"), ResourceType.DRF);
		kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-CallActivitySubProcess.xml"), ResourceType.DRF);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error: kbuilder.getErrors()) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "oldValue");
		ProcessInstance processInstance = ksession.startProcess("ParentProcess", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
		assertEquals("new value", ((WorkflowProcessInstance) processInstance).getVariable("y"));
	}

	public void testSubProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-SubProcess.xml");
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		ProcessInstance processInstance = ksession.startProcess("SubProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

    public void testAdHocSubProcess() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-AdHocSubProcess.xml");
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("AdHocSubProcess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }

	public void testIntermediateCatchEventSignal() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignal.xml");
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		// now signal process instance
		processInstance.signalEvent("MyMessage", "SomeValue");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

    public void testIntermediateCatchEventTimer() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimer.xml");
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        new Thread(new Runnable() {
            public void run() {
                ksession.fireUntilHalt();
            }
        }).start();
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for timer to trigger
        Thread.sleep(1000);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        ksession.halt();
    }

    public void testErrorEventProcess() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ErrorEndEvent.xml");
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ProcessInstance processInstance = ksession.startProcess("ErrorEndEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ABORTED);
    }

    public void testGraphicalInformation() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-OryxExportedExample.xml");
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("testProzess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }

    public void testServiceTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ServiceProcess.xml");
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)
            ksession.startProcess("ServiceProcess", params);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("Hello john!", processInstance.getVariable("s"));
    }

    public void testSendTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-SendTask.xml");
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)
            ksession.startProcess("SendTask", params);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    public void testReceiveTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ReceiveTask.xml");
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)
            ksession.startProcess("ReceiveTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        receiveTaskHandler.messageReceived("java.lang.String", "Hello john!");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("Hello john!", processInstance.getVariable("s"));
    }

    public void testPersistence() throws Exception {
        setupDataSource();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(
            "org.drools.persistence.jpa");
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        env.set(EnvironmentName.TRANSACTION_MANAGER,
             TransactionManagerServices.getTransactionManager());
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-UserTask.xml");
        StatefulKnowledgeSession ksession =
            JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        int sessionId = ksession.getId();
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        ksession.dispose();
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(
            sessionId, kbase, null, env);
        assertNotNull(ksession.getProcessInstance(processInstance.getId()));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertNull(ksession.getProcessInstance(processInstance.getId()));
    }

	private KnowledgeBase createKnowledgeBase(String process) throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
        ((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMN2SemanticModule());
        ((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNDISemanticModule());
        ((PackageBuilderConfiguration) conf).addDialect("XPath", new XPathDialectConfiguration());
		XmlProcessReader processReader = new XmlProcessReader(
	        ((PackageBuilderConfiguration) conf).getSemanticModules());
		RuleFlowProcess p = (RuleFlowProcess)
		    processReader.read(SimpleBPMNProcessTest.class.getResourceAsStream("/" + process));
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
//		System.out.println(XmlBPMNProcessDumper.INSTANCE.dump(p));
		kbuilder.add(ResourceFactory.newReaderResource(
            new StringReader(XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.DRF);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error: kbuilder.getErrors()) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}
	
	private static void setupDataSource() {
	    PoolingDataSource ds1 = new PoolingDataSource();
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
	
	private static class TestWorkItemHandler implements WorkItemHandler {
	    private WorkItem workItem;
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            this.workItem = workItem;
        }
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }
        public WorkItem getWorkItem() {
            WorkItem result = this.workItem;
            this.workItem = null;
            return result;
        }
        
	}
}
