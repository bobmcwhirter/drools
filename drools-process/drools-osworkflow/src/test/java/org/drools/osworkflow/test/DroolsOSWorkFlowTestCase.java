package org.drools.osworkflow.test;

import java.util.Collection;
import java.util.Collections;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.drools.osworkflow.DroolsWorkflow;

import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;

public class DroolsOSWorkFlowTestCase extends TestCase {

	private String resource;

	
	private DroolsOSWorkFlowTestCase(String xmlname) {
		super("testPrimer");
		this.resource = xmlname;
	}
	
	public static TestSuite suite() {
		InnerTestSuite suite = new InnerTestSuite();
		addResources(suite);
		return suite;
	}
	
	
	public static void addResources(InnerTestSuite suite) {
		suite.addTestFor("simple");
	}
	
	
	/**
	 * Testcase.
	 */
	public void testPrimer() {
		Workflow workflow = new DroolsWorkflow();
		DefaultConfiguration config = new DefaultConfiguration();
		workflow.setConfiguration(config);
		try {
		    long workflowId = workflow.initialize(this.resource, 1, null);
			
			Collection<Step> currentSteps = workflow.getCurrentSteps(workflowId);
            //verify we only have one current step
            assertNumberOfCurrentSteps(1, currentSteps.size());
            //verify it's step 1
            Step currentStep = currentSteps.iterator().next();
            assertCurrentStep(1,currentStep);
            int[] availableActions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
            //verify we only have one available action
            assertEquals("Unexpected number of available actions", 1, availableActions.length);
            //verify it's action 2
            assertEquals("Unexpected available action", 2, availableActions[0]);
            
            workflow.doAction(workflowId, 2, null);
            currentSteps = workflow.getCurrentSteps(workflowId);
            //verify we only have one current step
            assertEquals("Unexpected number of current steps", 1, currentSteps.size());
            //verify it's step 1
            currentStep = currentSteps.iterator().next();
            assertEquals("Unexpected current step", 1, currentStep.getStepId());
            
            availableActions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
            //verify we only have one available action
            assertEquals("Unexpected number of available actions", 1, availableActions.length);
            //verify it's action 3
            assertEquals("Unexpected available action", 3, availableActions[0]);
            
            workflow.doAction(workflowId, 3, null);
            currentSteps = workflow.getCurrentSteps(workflowId);
            //verify we only have no more current steps
            assertEquals("Unexpected number of current steps", 0, currentSteps.size());
            //verify process completed
            assertEquals("Unexpected state", WorkflowEntry.COMPLETED, workflow.getEntryState(workflowId));

            availableActions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
            //verify we only have no available action
            assertEquals("Unexpected number of available actions", 0, availableActions.length);
		} 
		catch (InvalidInputException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
		catch (WorkflowException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
	}
	
	private void assertCurrentStep (int stepIteration , long expectedStepId) {
		assertEquals("Unexpected current step", stepIteration, expectedStepId);
	}
	
	public void assertCurrentStep (int stepIteration , Step currentStep) {
		assertCurrentStep (stepIteration, currentStep.getId());
	}
	
	public void assertAvaliableActions (int stepIdIteration, int expectedAvaliableActions) {
		assertEquals("Unexpected number of available actions", 1, expectedAvaliableActions);
	}
	
	public void assertWFState (Workflow workflow, int stepId , int expectedStateId) {
//		int wId = workflow.getHistorySteps(id)
//		assertEquals("Unexpected state", expectedStateId, workflow.getEntryState(id))
	}
	
	public void assertNumberOfCurrentSteps(int stepIteration , int expectedSteps) {
		assertEquals("Unexpected number of current steps", stepIteration, expectedSteps);
	}
	
	/**
	 * @author ar26818347
	 */
	private static class InnerTestSuite extends TestSuite {
		
		public DroolsOSWorkFlowTestCase addTestFor(String xmlname) {
			DroolsOSWorkFlowTestCase test = new DroolsOSWorkFlowTestCase(xmlname);
			this.addTest(test);
			return test;
		}
		
		
	}
}

