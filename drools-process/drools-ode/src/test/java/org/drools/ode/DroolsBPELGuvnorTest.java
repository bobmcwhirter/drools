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

package org.drools.ode;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.ode.store.BPELResourceTypeBuilder;
import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.ResourceTypeBuilderRegistry;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.ode.core.BPELProcess;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * This is a sample file to launch a process.
 */
public class DroolsBPELGuvnorTest extends TestCase {

	public void testDroolsBPELProcess() throws Exception {
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		KnowledgeRuntimeLogger logger = 
			KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "audit");
		// start a new process instance
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("Operation", "hello");
		parameters.put("Body", 
			"<ns1:hello xmlns:ns1=\"http://ode/bpel/unit-test.wsdl\">\n" +
    		"  <TestPart xmlns=\"\">Hello</TestPart>\n" +
    		"</ns1:hello>");
		ksession.startProcess("{http://ode/bpel/unit-test}HelloWorld3-1", parameters);
		Thread.sleep(3000);
		logger.close();
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		ResourceType bpelType = new ResourceType(BPELProcess.ODE_TYPE, "ODE BPEL Process", "bpel.jar");
		ResourceTypeBuilderRegistry.getInstance().register(
			bpelType, new BPELResourceTypeBuilder());
		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("Guvnor");
		kagent.applyChangeSet(ResourceFactory.newClassPathResource("ChangeSet.xml"));
		return kagent.getKnowledgeBase();
	}

}