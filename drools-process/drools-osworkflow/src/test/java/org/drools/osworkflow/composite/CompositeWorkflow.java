package org.drools.osworkflow.composite;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.drools.osworkflow.DroolsWorkflow;
import org.drools.util.ArrayUtils;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.InvalidActionException;
import com.opensymphony.workflow.InvalidEntryStateException;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.InvalidRoleException;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;

/**
 * Composite class that includes one instance of OSWorkflows and one of
 * DroolsWorkflow. It delegates all its behaviour in both instances, making
 * assertions over the results.
 * 
 * @author Miguel Fossati
 * 
 */
public class CompositeWorkflow implements Workflow {

	private Workflow osWorkflow;
	private Workflow droolsWorkflow;

	public CompositeWorkflow() {
		this.osWorkflow = new BasicWorkflow("test");
		this.droolsWorkflow = new DroolsWorkflow();
	}

	// public CompositeWorkflow(Workflow osWorkFlowinstance , Workflow
	// droolsWorkflowInstance) {
	// this.osWorkflow = osWorkFlowinstance;
	// this.droolsWorkflow = droolsWorkflowInstance;
	// }

	public boolean canInitialize(String workflowName, int initialStep) {
		boolean aux1 = this.osWorkflow.canInitialize(workflowName, initialStep);
		boolean aux2 = this.droolsWorkflow.canInitialize(workflowName,
				initialStep);
		Assert.assertEquals(aux1, aux2);
		return (aux1);
	}

	public boolean canInitialize(String workflowName, int initialAction,
			Map inputs) {
		boolean aux1 = this.osWorkflow.canInitialize(workflowName,
				initialAction, inputs);
		boolean aux2 = this.droolsWorkflow.canInitialize(workflowName,
				initialAction, inputs);
		Assert.assertEquals(aux1, aux2);
		return (aux1);
	}

	public boolean canModifyEntryState(long id, int newState) {
		boolean aux1 = this.osWorkflow.canModifyEntryState(id, newState);
		boolean aux2 = this.droolsWorkflow.canModifyEntryState(id, newState);
		Assert.assertEquals(aux1, aux2);
		return (aux1);
	}

	public void changeEntryState(long id, int newState)
			throws WorkflowException {
		this.osWorkflow.changeEntryState(id, newState);
		this.droolsWorkflow.changeEntryState(id, newState);

	}

	public void doAction(long id, int actionId, Map inputs)
			throws InvalidInputException, WorkflowException {
		this.osWorkflow.doAction(id, actionId, inputs);
		this.droolsWorkflow.doAction(id, actionId, inputs);

	}

	public void executeTriggerFunction(long id, int triggerId)
			throws WorkflowException {
		this.osWorkflow.changeEntryState(id, triggerId);
		this.droolsWorkflow.changeEntryState(id, triggerId);

	}

	public int[] getAvailableActions(long id) {
		return this.getAvailableActions(id, null);
	}

	public int[] getAvailableActions(long id, Map inputs) {
		int[] aux1 = this.osWorkflow.getAvailableActions(id, inputs);
		int[] aux2 = this.droolsWorkflow.getAvailableActions(id, inputs);
//		Assert.assertTrue(ArrayUtils.equals(aux1, aux2));
		return aux1;
	}

	public List getCurrentSteps(long id) {
		List aux1 = this.osWorkflow.getCurrentSteps(id);
		List aux2 = this.droolsWorkflow.getCurrentSteps(id);
		// TODO add assertion
		return aux1;
	}

	public int getEntryState(long id) {
		int aux1 = this.osWorkflow.getEntryState(id);
		int aux2 = this.droolsWorkflow.getEntryState(id);
		Assert.assertEquals(aux1, aux2);
		return aux1;
	}

	public List getHistorySteps(long id) {
		List aux1 = this.osWorkflow.getHistorySteps(id);
		List aux2 = this.droolsWorkflow.getHistorySteps(id);
		// TODO add assertion
		return aux1;
	}

	public PropertySet getPropertySet(long id) {
		PropertySet aux1 = this.osWorkflow.getPropertySet(id);
		PropertySet aux2 = this.droolsWorkflow.getPropertySet(id);
		// TODO add assertion
		return aux1;
	}

	public List getSecurityPermissions(long id) {
		return this.getSecurityPermissions(id, null);
	}

	public List getSecurityPermissions(long id, Map inputs) {
		List aux1 = this.osWorkflow.getSecurityPermissions(id, inputs);
		List aux2 = this.droolsWorkflow.getSecurityPermissions(id, inputs);
		// TODO add assertion
		return aux1;
	}

	public WorkflowDescriptor getWorkflowDescriptor(String workflowName) {
		WorkflowDescriptor wf = this.osWorkflow
				.getWorkflowDescriptor(workflowName);
		return wf;
	}

	public String getWorkflowName(long id) {
		String wfName = this.osWorkflow.getWorkflowName(id);
		return wfName;
	}

	public String[] getWorkflowNames() {
		String[] wfName = this.osWorkflow.getWorkflowNames();
		return wfName;
	}

	public long initialize(String workflowName, int initialAction, Map inputs)
			throws InvalidRoleException, InvalidInputException,
			WorkflowException, InvalidEntryStateException,
			InvalidActionException {
		long id1 = this.osWorkflow.initialize(workflowName, initialAction,
				inputs);
		long id2 = this.droolsWorkflow.initialize(workflowName, initialAction,
				inputs);
		Assert.assertEquals(id1, id2);
		return id1;
	}

	public List query(WorkflowQuery query) throws WorkflowException {
		List l1 = this.osWorkflow.query(query);
		List l2 = this.droolsWorkflow.query(query);
		return l1;
	}

	public List query(WorkflowExpressionQuery query) throws WorkflowException {
		List l1 = this.osWorkflow.query(query);
		List l2 = this.droolsWorkflow.query(query);
		return l1;
	}

	public boolean removeWorkflowDescriptor(String workflowName)
			throws FactoryException {
		boolean bool1 = this.osWorkflow.removeWorkflowDescriptor(workflowName);
		boolean bool2 = this.droolsWorkflow
				.removeWorkflowDescriptor(workflowName);
		Assert.assertEquals(bool1, bool2);
		return false;
	}

	public boolean saveWorkflowDescriptor(String workflowName,
			WorkflowDescriptor descriptor, boolean replace)
			throws FactoryException {
		boolean bool1 = this.osWorkflow.saveWorkflowDescriptor(workflowName,
				descriptor, replace);
		boolean bool2 = this.droolsWorkflow.saveWorkflowDescriptor(
				workflowName, descriptor, replace);
		Assert.assertEquals(bool1, bool2);
		return false;
	}

	public void setConfiguration(Configuration configuration) {
		this.osWorkflow.setConfiguration(configuration);
		this.droolsWorkflow.setConfiguration(configuration);
	}

}
