package org.drools.osworkflow.test.persistence;

import java.util.Properties;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.SessionConfiguration;
import org.drools.osworkflow.core.command.DoActionCommand;
import org.drools.persistence.session.SingleSessionCommandService;
import org.drools.process.command.GetProcessInstanceCommand;
import org.drools.process.command.StartProcessCommand;
import org.drools.runtime.process.ProcessInstance;

public class ComplexProcessPersistenceTestCase extends AbstractJPAPersistenceTestCase {
	
	public void testComplex() {
		Properties properties = setupCommonProperties();
		RuleBaseConfiguration conf = new RuleBaseConfiguration(properties);
		// load the process
		RuleBase ruleBase = createKnowledgeBase(conf,"/entrevistarf.rf");
		
		SessionConfiguration config = new SessionConfiguration(properties);
		
		SingleSessionCommandService service = new SingleSessionCommandService(ruleBase, config);
		StartProcessCommand startProcessCommand = new StartProcessCommand();
		startProcessCommand.setProcessId("entrevista");
		ProcessInstance processInstance = (ProcessInstance) service.execute(startProcessCommand);
		System.out.println("Started process instance " + processInstance.getId());

		service = new SingleSessionCommandService(ruleBase, config);
        GetProcessInstanceCommand getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNotNull(processInstance);
		System.out.println("Now working with processInstance " + processInstance.getId());

        service = new SingleSessionCommandService(ruleBase, config);
        DoActionCommand doActionCmd = new DoActionCommand();
        doActionCmd.setProcessInstanceId(processInstance.getId());
        doActionCmd.setActionId(2); //Action to be executed at current step
        service.execute(doActionCmd);
        
        service = new SingleSessionCommandService(ruleBase, config);
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNotNull(processInstance);
		System.out.println("Now working with processInstance " + processInstance.getId());
        
        
        service = new SingleSessionCommandService(ruleBase, config);
        doActionCmd = new DoActionCommand();
        doActionCmd.setProcessInstanceId(processInstance.getId());
        doActionCmd.setActionId(4); //Action to be executed at current step
        service.execute(doActionCmd);
        
        service = new SingleSessionCommandService(ruleBase, config);
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNull(processInstance);
	}

}
