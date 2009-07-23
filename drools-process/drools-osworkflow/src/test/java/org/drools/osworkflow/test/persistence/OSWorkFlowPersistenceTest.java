package org.drools.osworkflow.test.persistence;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.SessionConfiguration;
import org.drools.command.runtime.process.GetProcessInstanceCommand;
import org.drools.command.runtime.process.StartProcessCommand;
import org.drools.osworkflow.core.command.DoActionCommand;
import org.drools.osworkflow.instance.OSWorkflowProcessInstance;
import org.drools.osworkflow.instance.node.StepNodeInstance;
import org.drools.persistence.session.SingleSessionCommandService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.process.ProcessInstance;

import bitronix.tm.TransactionManagerServices;

public class OSWorkFlowPersistenceTest extends AbstractJPAPersistenceTest {

    public void testPersistence() {
        Environment environment = KnowledgeBaseFactory.newEnvironment();
        environment.set( EnvironmentName.ENTITY_MANAGER_FACTORY, getEmf() );
        environment.set( "drools.TransactionManager",
             TransactionManagerServices.getTransactionManager() );

        Properties properties = setupCommonProperties();
		RuleBaseConfiguration conf = new RuleBaseConfiguration(properties);
		// load the process
		KnowledgeBase ruleBase = createKnowledgeBase(conf,"simplerf.rf");

		SessionConfiguration config = new SessionConfiguration(properties);
		
		SingleSessionCommandService service = new SingleSessionCommandService(ruleBase,config, environment);
		StartProcessCommand startProcessCommand = new StartProcessCommand();
		startProcessCommand.setProcessId("simple");
		ProcessInstance processInstance = (ProcessInstance) service.execute(startProcessCommand);
		System.out.println("Started process instance " + processInstance.getId());

		service = new SingleSessionCommandService(ruleBase, config, environment);
        GetProcessInstanceCommand getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        assertNotNull(processInstance);
		System.out.println("Now working with processInstance " + processInstance.getId());
		assertEquals("Queued", 
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
        
        assertEquals("Underway", 
        			((StepNodeInstance)
        					((OSWorkflowProcessInstance)processInstance)
        							.getNodeInstances().iterator().next()).getStatus());
        System.out.println("Now working with processInstance " + processInstance.getId());
        
        
        service = new SingleSessionCommandService(ruleBase, config, environment);
        doActionCmd = new DoActionCommand();
        doActionCmd.setProcessInstanceId(processInstance.getId());
        doActionCmd.setActionId(3); //Action to be executed at current step
        service.execute(doActionCmd);
        
        
        service = new SingleSessionCommandService(ruleBase, config, environment);
        getProcessInstanceCommand = new GetProcessInstanceCommand();
        getProcessInstanceCommand.setProcessInstanceId(processInstance.getId());
        processInstance = (ProcessInstance) service.execute(getProcessInstanceCommand);
        //Because it is finished the instance will not longer be available 
        assertNull(processInstance);
		

	}




}
