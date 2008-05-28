package org.drools;

import java.util.Collection;
import java.util.Collections;

import junit.framework.TestCase;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.spi.Step;

public class SimpleOSWorkflowProcessTest extends TestCase {
	
	public void testSimpleProcess() {
		Workflow workflow = new BasicWorkflow("testuser");
		DefaultConfiguration config = new DefaultConfiguration();
		workflow.setConfiguration(config);
		try {
			long workflowId = workflow.initialize("simple", 1, null);
			
			Collection<Step> currentSteps = workflow.getCurrentSteps(workflowId);
			//verify we only have one current step
			assertEquals("Unexpected number of current steps", 1, currentSteps.size());
			//verify it's step 1
			Step currentStep = currentSteps.iterator().next();
			assertEquals("Unexpected current step", 1, currentStep.getStepId());

			int[] availableActions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
			//verify we only have one available action
			assertEquals("Unexpected number of available actions", 1, availableActions.length);
			//verify it's action 1
			assertEquals("Unexpected available action", 2, availableActions[0]);
			
			workflow.doAction(workflowId, 2, null);
			
			availableActions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
			//verify we only have one available action
			assertEquals("Unexpected number of available actions", 1, availableActions.length);
			//verify it's action 1
			assertEquals("Unexpected available action", 3, availableActions[0]);
			
			workflow.doAction(workflowId, 3, null);
			
			availableActions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
			//verify we only have one available action
			assertEquals("Unexpected number of available actions", 0, availableActions.length);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail();
		}
	}

}
