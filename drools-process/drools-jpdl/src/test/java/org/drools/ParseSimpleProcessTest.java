package org.drools;

import junit.framework.TestCase;

import org.drools.jpdl.EpdlWriter;
import org.drools.jpdl.JpdlParser;
import org.drools.jpdl.core.JpdlProcess;
import org.drools.process.core.validation.ProcessValidationError;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.WorkItemHandler;
import org.drools.rule.Package;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

public class ParseSimpleProcessTest extends TestCase {

    public void testSimpleProcess2() throws Exception {
        JpdlParser parser = new JpdlParser();
        JpdlProcess process = parser.loadJpdlProcess("simple2/processdefinition.xml");
        ProcessValidationError[] errors = parser.getErrors();
        for (ProcessValidationError error: errors) {
            System.err.println(error);
        }
        assertEquals(0, errors.length);
        
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        Package p = new Package("com.sample");
        p.addProcess(process);
        ruleBase.addPackage( p );
        
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        workingMemory.getWorkItemManager().registerWorkItemHandler(
            "Email", handler);
        assertTrue(handler.getWorkItemId() == -1);
        ProcessInstance processInstance = workingMemory.startProcess("simple");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        processInstance.signalEvent("signal", null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

	public void testSimpleProcess3() throws Exception {
	    JpdlParser parser = new JpdlParser();
	    JpdlProcess process = parser.loadJpdlProcess("simple3/processdefinition.xml");
	    ProcessValidationError[] errors = parser.getErrors();
	    for (ProcessValidationError error: errors) {
            System.err.println(error);
        }
        assertEquals(0, errors.length);
        
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        Package p = new Package("com.sample");
        p.addProcess(process);
        ruleBase.addPackage( p );
        
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        ProcessInstance processInstance = workingMemory.startProcess("simple");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        processInstance.signalEvent("signal", null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    private static class TestWorkItemHandler implements WorkItemHandler {
        private long workItemId = -1;
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            workItemId = workItem.getId();
        }
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }
        public long getWorkItemId() {
            return workItemId;
        }
    }

      public void testWriteEPDLStateNodes() throws Exception {
        JpdlParser parser = new JpdlParser();
        JpdlProcess process = parser.loadJpdlProcess("simple2states/processdefinition.xml");
        ProcessValidationError[] errors = parser.getErrors();
        
        EpdlWriter.write(process);
        
      }
       public void testWriteEPDLDecisionNodes() throws Exception {
        JpdlParser parser = new JpdlParser();
        JpdlProcess process = parser.loadJpdlProcess("simple2decision/processdefinition.xml");
        ProcessValidationError[] errors = parser.getErrors();

        EpdlWriter.write(process);

      }
       public void testWriteEPDLSuggestJoinNode() throws Exception {
        JpdlParser parser = new JpdlParser();
        JpdlProcess process = parser.loadJpdlProcess("simple2suggestJoinComplex/processdefinition.xml");
        ProcessValidationError[] errors = parser.getErrors();

        EpdlWriter.write(process);

      }
       public void testWriteEPDLSuggestSplitNode() throws Exception {
        JpdlParser parser = new JpdlParser();
        JpdlProcess process = parser.loadJpdlProcess("simple2suggestSplitInActionNode/processdefinition.xml");
        ProcessValidationError[] errors = parser.getErrors();

        EpdlWriter.write(process);

      }
      public void testWriteEPDLSuperState() throws Exception {
        JpdlParser parser = new JpdlParser();
        JpdlProcess process = parser.loadJpdlProcess("simple2superState/processdefinition.xml");
        ProcessValidationError[] errors = parser.getErrors();

        EpdlWriter.write(process);

      }
       public void testWriteEPDLNestedForksWithSuperState() throws Exception {
        JpdlParser parser = new JpdlParser();
        JpdlProcess process = parser.loadJpdlProcess("simpleNestedForkWithSuperState/processdefinition.xml");
        ProcessValidationError[] errors = parser.getErrors();

        EpdlWriter.write(process);
        
        for (ProcessValidationError error: errors) {
            System.err.println(error);
        }
        assertEquals(0, errors.length);

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        Package p = new Package("com.sample");
        p.addProcess(process);
        ruleBase.addPackage( p );

        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        ProcessInstance processInstance = workingMemory.startProcess("simple");

      }


}
