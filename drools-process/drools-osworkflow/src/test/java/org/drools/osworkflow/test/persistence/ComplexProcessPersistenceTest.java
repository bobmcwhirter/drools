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

package org.drools.osworkflow.test.persistence;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.SessionConfiguration;
import org.drools.command.runtime.process.GetProcessInstanceCommand;
import org.drools.command.runtime.process.StartProcessCommand;
import org.drools.osworkflow.core.command.DoActionCommand;
import org.drools.persistence.session.SingleSessionCommandService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.process.ProcessInstance;

import bitronix.tm.TransactionManagerServices;

public class ComplexProcessPersistenceTest extends AbstractJPAPersistenceTest {
	
	public void testComplex() {
        Environment environment = KnowledgeBaseFactory.newEnvironment();
        environment.set( EnvironmentName.ENTITY_MANAGER_FACTORY, getEmf() );
        environment.set( "drools.TransactionManager",
             TransactionManagerServices.getTransactionManager() );

        Properties properties = setupCommonProperties();
		RuleBaseConfiguration conf = new RuleBaseConfiguration(properties);
		// load the process
		KnowledgeBase ruleBase = createKnowledgeBase(conf,"entrevistarf.rf");
		
		SessionConfiguration config = new SessionConfiguration(properties);
		
		SingleSessionCommandService service = new SingleSessionCommandService(ruleBase, config, environment);
		int sessionId = service.getSessionId();
		StartProcessCommand startProcessCommand = new StartProcessCommand();
		startProcessCommand.setProcessId("entrevista");
		ProcessInstance processInstance = (ProcessInstance) service.execute(startProcessCommand);
		System.out.println("Started process instance " + processInstance.getId());

		service = new SingleSessionCommandService(sessionId, ruleBase, config, environment);
        GetProcessInstanceCommand getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNotNull(processInstance);
		System.out.println("Now working with processInstance " + processInstance.getId());

        service = new SingleSessionCommandService(sessionId, ruleBase, config, environment);
        DoActionCommand doActionCmd = new DoActionCommand();
        doActionCmd.setProcessInstanceId(processInstance.getId());
        doActionCmd.setActionId(2); //Action to be executed at current step
        service.execute(doActionCmd);
        
        service = new SingleSessionCommandService(sessionId, ruleBase, config, environment);
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNotNull(processInstance);
		System.out.println("Now working with processInstance " + processInstance.getId());
        
        
        service = new SingleSessionCommandService(sessionId, ruleBase, config, environment);
        doActionCmd = new DoActionCommand();
        doActionCmd.setProcessInstanceId(processInstance.getId());
        doActionCmd.setActionId(4); //Action to be executed at current step
        service.execute(doActionCmd);
        
        service = new SingleSessionCommandService(sessionId, ruleBase, config, environment);
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNull(processInstance);
	}

}
