package org.drools.osworkflow.test;

import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;

import junit.framework.TestCase;

import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.osworkflow.DroolsWorkflow;
import org.drools.osworkflow.OSWorkflowParser;
import org.drools.osworkflow.core.OSWorkflowProcess;
import org.drools.osworkflow.xml.XmlOSWorkflowProcessDumper;
import org.drools.xml.XmlProcessReader;

import com.opensymphony.workflow.InvalidActionException;
import com.opensymphony.workflow.InvalidEntryStateException;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.InvalidRoleException;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.spi.Step;

public class ComplexProcessTestCase extends TestCase {

	public void testComplexProcess() {
		Workflow workflow = new DroolsWorkflow();
		DefaultConfiguration config = new DefaultConfiguration();
		workflow.setConfiguration(config);
		try {
			long id = workflow.initialize("entrevista", 1, null);
			Collection<Step> currentSteps = workflow.getCurrentSteps(id);
			// verify we only have one current step
			assertEquals("Unexpected number of current steps", 1, currentSteps.size());
			// verify it's step 1
			Step currentStep = currentSteps.iterator().next();
			assertEquals("Unexpected current step", 1, currentStep.getStepId());

			int[] availableActions = workflow.getAvailableActions(id, Collections.EMPTY_MAP);
			// verify we only have 6 available actions
			assertEquals("Unexpected number of available actions", 6, availableActions.length);
			workflow.doAction(id, 2, null);
			currentSteps = workflow.getCurrentSteps(id);
			// verify we have one current step
			assertEquals("Unexpected number of current steps", 1, currentSteps.size());
			// verify it's step 2
			currentStep = currentSteps.iterator().next();
			assertEquals("Unexpected current step", 2, currentStep.getStepId());

			availableActions = workflow.getAvailableActions(id, Collections.EMPTY_MAP);
			// verify we have 6 available action
			assertEquals("Unexpected number of available actions", 6, availableActions.length);
			// //verify it's action 3
			// assertEquals("Unexpected available action", 3,
			// availableActions[0]);
			workflow.doAction(id, 6, null);
			currentSteps = workflow.getCurrentSteps(id);
			// verify we have no current Step (since we ended the process)
			assertEquals("Unexpected number of current steps", 0, currentSteps.size());
			// verify we have no available action
			availableActions = workflow.getAvailableActions(id, Collections.EMPTY_MAP);
			assertEquals("Unexpected number of available actions", 0, availableActions.length);

		} catch (InvalidActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRoleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidEntryStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WorkflowException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testToXML() throws Exception {
        DefaultConfiguration config = new DefaultConfiguration();
        config.load(null);
        WorkflowDescriptor workflowDescriptor = config.getWorkflow("entrevista");
	    OSWorkflowProcess process = new OSWorkflowParser().parseOSWorkflow(workflowDescriptor);
	    
	    String processXML = XmlOSWorkflowProcessDumper.INSTANCE.dump(process);
	    System.out.println(processXML);
	    
	    XmlProcessReader reader = new XmlProcessReader(
            new PackageBuilderConfiguration().getSemanticModules());
	    System.setProperty( "drools.schema.validating", "false" );
	    OSWorkflowProcess process2 = (OSWorkflowProcess) reader.read(new StringReader(processXML));
        assertNotNull(process2);
	}
	
}
