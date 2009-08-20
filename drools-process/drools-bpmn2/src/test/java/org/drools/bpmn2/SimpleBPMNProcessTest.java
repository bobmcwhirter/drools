package org.drools.bpmn2;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;

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

	public void testCallActivity() throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf).loadSemanticModule("BPMN2SemanticModule.conf");
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

	public void testIntermediateCatchEvent() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEvent.xml");
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		// now signal process instance
		processInstance.signalEvent("MyMessage", "SomeValue");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	private KnowledgeBase createKnowledgeBase(String process) throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf).loadSemanticModule("BPMN2SemanticModule.conf");
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
		kbuilder.add(ResourceFactory.newClassPathResource(process), ResourceType.DRF);
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
}
