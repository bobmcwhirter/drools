/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
