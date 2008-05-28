package org.drools.osworkflow;

import java.util.List;
import java.util.Map;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.process.instance.ProcessInstance;
import org.drools.rule.Package;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.InvalidActionException;
import com.opensymphony.workflow.InvalidEntryStateException;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.InvalidRoleException;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;

public class DroolsWorkflow implements Workflow {

	private WorkingMemory workingMemory;
	private Configuration configuration;
	
	public long initialize(String workflowName, int initialAction, Map inputs)
			throws InvalidRoleException, InvalidInputException,
			WorkflowException, InvalidEntryStateException,
			InvalidActionException {
		RuleBase ruleBase = workingMemory.getRuleBase();
		Package p = ruleBase.getPackage("org.drools.osworkflow");
		if (p.getRuleFlows().get(workflowName) == null) {
			WorkflowDescriptor descriptor = getConfiguration().getWorkflow(workflowName);
			if (descriptor == null) {
				throw new IllegalArgumentException(
					"Could not find process with name " + workflowName);
			}
			Package newPackage = new Package("org.drools.osworkflow");
			newPackage.addRuleFlow(OSWorkflowParser.parseOSWorkflow(descriptor));
			ruleBase.addPackage(newPackage);
		}
		// TODO initialAction
		return workingMemory.startProcess(workflowName, inputs).getId();
	}

	public void doAction(long id, int actionId, Map inputs)
			throws InvalidInputException, WorkflowException {
		ProcessInstance processInstance = findProcessInstance(id);
		
	}
	
	private ProcessInstance findProcessInstance(long id) {
		ProcessInstance processInstance = workingMemory.getProcessInstance(id);
		if (processInstance == null) {
			throw new IllegalArgumentException(
				"Could not find process instance with id " + id);
		}
		return processInstance;
	}
	
	public int[] getAvailableActions(long id) {
		// TODO
		return null;
	}

	public int[] getAvailableActions(long id, Map inputs) {
		// TODO
		return null;
	}

	public boolean canInitialize(String workflowName, int initialStep) {
		// TODO
		return false;
	}

	public boolean canInitialize(String workflowName, int initialAction, Map inputs) {
		// TODO
		return false;
	}

	public boolean canModifyEntryState(long id, int newState) {
		// TODO
		return false;
	}

	public void changeEntryState(long id, int newState) throws WorkflowException {
		// TODO
	}

	public void executeTriggerFunction(long id, int triggerId) throws WorkflowException {
		// TODO
	}

	public List getCurrentSteps(long id) {
		// TODO
		return null;
	}

	public int getEntryState(long id) {
		// TODO
		return 0;
	}

	public List getHistorySteps(long id) {
		// TODO
		return null;
	}

	public PropertySet getPropertySet(long id) {
		// TODO
		return null;
	}

	public List getSecurityPermissions(long id) {
		// TODO
		return null;
	}

	public List getSecurityPermissions(long id, Map inputs) {
		// TODO
		return null;
	}

	public WorkflowDescriptor getWorkflowDescriptor(String workflowName) {
		// TODO
		return null;
	}

	public String getWorkflowName(long id) {
		// TODO
		return null;
	}

	public String[] getWorkflowNames() {
		// TODO
		return null;
	}

	public List query(WorkflowQuery query) throws WorkflowException {
		// TODO
		return null;
	}

	public List query(WorkflowExpressionQuery query) throws WorkflowException {
		// TODO
		return null;
	}

	public boolean removeWorkflowDescriptor(String workflowName) throws FactoryException {
		// TODO
		return false;
	}

	public boolean saveWorkflowDescriptor(String workflowName, 
			WorkflowDescriptor descriptor, boolean replace)
			throws FactoryException {
		// TODO
		return false;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        Package p = new Package("org.drools.osworkflow");
        ruleBase.addPackage(p);
        workingMemory = ruleBase.newStatefulSession();
	}
	
	private Configuration getConfiguration() {
        Configuration config = (configuration != null) ? configuration : DefaultConfiguration.INSTANCE;
        if (!config.isInitialized()) {
            try {
                config.load(null);
            } catch (FactoryException e) {
            	e.printStackTrace();
                return null;
            }
        }
        return config;
	}

}
