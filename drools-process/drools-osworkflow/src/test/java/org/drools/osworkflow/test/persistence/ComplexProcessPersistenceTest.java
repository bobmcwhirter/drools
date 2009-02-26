package org.drools.osworkflow.test.persistence;

import java.util.Properties;

import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.SessionConfiguration;
import org.drools.osworkflow.core.command.DoActionCommand;
import org.drools.persistence.session.SingleSessionCommandService;
import org.drools.process.command.GetProcessInstanceCommand;
import org.drools.process.command.StartProcessCommand;
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
		RuleBase ruleBase = createKnowledgeBase(conf,"/entrevistarf.rf");
		
		SessionConfiguration config = new SessionConfiguration(properties);
		
		SingleSessionCommandService service = new SingleSessionCommandService(ruleBase, config, environment);
		int sessionId = service.getSessionId();
		StartProcessCommand startProcessCommand = new StartProcessCommand();
		startProcessCommand.setProcessId("entrevista");
		ProcessInstance processInstance = (ProcessInstance) service.execute(startProcessCommand);
		System.out.println("Started process instance " + processInstance.getId());

		service = new SingleSessionCommandService(ruleBase, config, environment, sessionId);
        GetProcessInstanceCommand getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNotNull(processInstance);
		System.out.println("Now working with processInstance " + processInstance.getId());

        service = new SingleSessionCommandService(ruleBase, config, environment, sessionId);
        DoActionCommand doActionCmd = new DoActionCommand();
        doActionCmd.setProcessInstanceId(processInstance.getId());
        doActionCmd.setActionId(2); //Action to be executed at current step
        service.execute(doActionCmd);
        
        service = new SingleSessionCommandService(ruleBase, config, environment, sessionId);
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNotNull(processInstance);
		System.out.println("Now working with processInstance " + processInstance.getId());
        
        
        service = new SingleSessionCommandService(ruleBase, config, environment, sessionId);
        doActionCmd = new DoActionCommand();
        doActionCmd.setProcessInstanceId(processInstance.getId());
        doActionCmd.setActionId(4); //Action to be executed at current step
        service.execute(doActionCmd);
        
        service = new SingleSessionCommandService(ruleBase, config, environment, sessionId);
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNull(processInstance);
	}

}
