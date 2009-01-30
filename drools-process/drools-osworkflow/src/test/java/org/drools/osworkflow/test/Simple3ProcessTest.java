package org.drools.osworkflow.test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.drools.osworkflow.DroolsWorkflow;
import org.drools.osworkflow.OSWorkflowParser;
import org.drools.osworkflow.core.OSWorkflowProcess;
import org.drools.osworkflow.xml.OSWorkflowSemanticModule;
import org.drools.osworkflow.xml.XmlOSWorkflowProcessDumper;
import org.drools.xml.SemanticModules;
import org.drools.xml.XmlProcessReader;

import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;

public class Simple3ProcessTest extends TestCase {
	
	public void testSimpleProcess() {
		Workflow workflow = new DroolsWorkflow();
		DefaultConfiguration config = new DefaultConfiguration();
		workflow.setConfiguration(config);
		try {
			long workflowId = workflow.initialize("holiday2", 100, null);
			Collection<Step> currentSteps = workflow.getCurrentSteps(workflowId);
			//verify it's step 1
			Step currentStep = currentSteps.iterator().next();
			assertEquals("Unexpected current step", 1, currentStep.getStepId());

			int[] availableActions = workflow.getAvailableActions(workflowId, Collections.EMPTY_MAP);
			//verify we only have one available action
			assertEquals("Unexpected number of available actions", 1, availableActions.length);
			//verify it's action 1
			assertEquals("Unexpected available action", 1, availableActions[0]);
			
			
		}
		catch (Exception e) {
			int a = 1;
		}
		
	}
	
	
}
