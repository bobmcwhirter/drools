package org.drools.osworkflow.test.persistence;

import java.util.Properties;

import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.SessionConfiguration;
import org.drools.osworkflow.core.command.DoActionCommand;
import org.drools.osworkflow.instance.OSWorkflowProcessInstance;
import org.drools.osworkflow.instance.node.StepNodeInstance;
import org.drools.persistence.session.SingleSessionCommandService;
import org.drools.process.command.GetProcessInstanceCommand;
import org.drools.process.command.StartProcessCommand;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.process.ProcessInstance;

import bitronix.tm.TransactionManagerServices;

public class OSWorkFlowPersistenceSplitJoinTest extends AbstractJPAPersistenceTest {

	public void testPersistence() {
        Environment environment = KnowledgeBaseFactory.newEnvironment();
        environment.set( EnvironmentName.ENTITY_MANAGER_FACTORY, getEmf() );
        environment.set( "drools.TransactionManager",
             TransactionManagerServices.getTransactionManager() );
        
		Properties properties = setupCommonProperties();
		RuleBaseConfiguration conf = new RuleBaseConfiguration(properties);
		// load the process
		RuleBase ruleBase = createKnowledgeBase(conf,"/simple2rf.rf");
		
		SessionConfiguration config = new SessionConfiguration(properties);

		SingleSessionCommandService service = new SingleSessionCommandService(ruleBase, config, environment);
		StartProcessCommand startProcessCommand = new StartProcessCommand();
		startProcessCommand.setProcessId("simple2");
		ProcessInstance processInstance = (ProcessInstance) service.execute(startProcessCommand);
		System.out.println("Started process instance " + processInstance.getId());

		service = new SingleSessionCommandService(ruleBase, config, environment);
        GetProcessInstanceCommand getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNotNull(processInstance);
		System.out.println("Now working with processInstance " + processInstance.getId());
		assertEquals("Active", 
    			((StepNodeInstance)
    					((OSWorkflowProcessInstance)processInstance)
    							.getNodeInstances().iterator().next()).getStatus());
        
        service = new SingleSessionCommandService(ruleBase, config, environment);
        DoActionCommand doActionCmd = new DoActionCommand();
        doActionCmd.setProcessInstanceId(processInstance.getId());
        doActionCmd.setActionId(2); //Action to be executed at current step
        service.execute(doActionCmd);
        
        
        service = new SingleSessionCommandService(ruleBase, config, environment);
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNotNull(processInstance);
        
        service = new SingleSessionCommandService(ruleBase, config, environment);
        doActionCmd = new DoActionCommand();
        doActionCmd.setProcessInstanceId(processInstance.getId());
        doActionCmd.setActionId(3); //Action to be executed at current step
        service.execute(doActionCmd);
		
        service = new SingleSessionCommandService(ruleBase, config, environment);
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNotNull(processInstance);
        
        service = new SingleSessionCommandService(ruleBase, config, environment);
        doActionCmd = new DoActionCommand();
        doActionCmd.setProcessInstanceId(processInstance.getId());
        doActionCmd.setActionId(4); //Action to be executed at current step
        service.execute(doActionCmd);
        
        service = new SingleSessionCommandService(ruleBase, config, environment);
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNotNull(processInstance);
        
        assertEquals("Active", 
    			((StepNodeInstance)
    					((OSWorkflowProcessInstance)processInstance)
    							.getNodeInstances().iterator().next()).getStatus());
        
        service = new SingleSessionCommandService(ruleBase, config, environment);
        doActionCmd = new DoActionCommand();
        doActionCmd.setProcessInstanceId(processInstance.getId());
        doActionCmd.setActionId(5); //Action to be executed at current step
        service.execute(doActionCmd);
        
        
        service = new SingleSessionCommandService(ruleBase, config, environment);
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNull(processInstance);
        
	}




}
