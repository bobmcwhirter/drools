package org.drools.process.enterprise.processinstance;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.process.enterprise.repository.ProcessInfo;
import org.drools.process.enterprise.repository.ProcessRepository;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemHandler;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.ProcessInstanceImpl;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.drools.rule.Package;

@Stateless
public class ProcessServiceBean implements ProcessService {

	private @PersistenceContext EntityManager manager;
	private @EJB ProcessRepository processRepository;
	   
	public long startProcess(String processId) {
		System.out.println("Starting process " + processId);
		InternalWorkingMemory workingMemory = createWorkingMemory();
		// start process
		ProcessInstance processInstance = workingMemory.startProcess(processId);
		persistProcessInstances(workingMemory);
		return processInstance.getId();
	}

	public void completeWorkItem(long workItemId, Map<String, Object> results) {
		InternalWorkingMemory workingMemory = createWorkingMemory();
		WorkItemInfo workItemInfo = manager.find(WorkItemInfo.class, workItemId);
		if (workItemInfo == null) {
			throw new IllegalArgumentException(
				"Could not find work item " + workItemId);
		}
		manager.remove(workItemInfo);
		// TODO: should load other process instances as well
		long processInstanceId = workItemInfo.getProcessInstanceId();
		ProcessInstanceInfo processInstanceInfo = manager.find(ProcessInstanceInfo.class, processInstanceId);
		if (processInstanceInfo == null) {
			throw new IllegalArgumentException(
				"Could not find process instance " + processInstanceId);
		}
		ProcessInstanceImpl processInstance = (ProcessInstanceImpl) processInstanceInfo.getProcessInstance();
		processInstance.setProcess(((InternalRuleBase) workingMemory.getRuleBase()).getProcess(processInstance.getProcessId())); 
		processInstance.setWorkingMemory(workingMemory);
		processInstance.reconnect();
		workingMemory.getWorkItemManager().completeWorkItem(workItemId, results);
		persistProcessInstances(workingMemory);
	}
	
	private InternalWorkingMemory createWorkingMemory() {
		// TODO: share rule base
		List<ProcessInfo> processInfos = processRepository.getProcessInfos();
		PackageBuilder builder = new PackageBuilder();
		for (ProcessInfo processInfo: processInfos) {
			builder.addRuleFlow(new StringReader(processInfo.getProcessXML()));
		}
		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(pkg);
		// create new working memory
		InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();
		// TODO auto-register work item handlers (e.g. based on config)
		workingMemory.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
		workingMemory.getWorkItemManager().registerWorkItemHandler("Human Task", new EnterpriseWorkItemHandler());
		return workingMemory;
	}
	
	private void persistProcessInstances(WorkingMemory workingMemory) {
		for (ProcessInstance pi: workingMemory.getProcessInstances()) {
			// persist process instance if necessary
			ProcessInstanceInfo processInstanceInfo = manager.find(ProcessInstanceInfo.class, pi.getId());
			if (processInstanceInfo == null) {
				processInstanceInfo = new ProcessInstanceInfo();
			}
			processInstanceInfo.setProcessInstance(pi);
			manager.persist(processInstanceInfo);
			System.out.println("Persisted process instance" + pi.getId());
		}
		// TODO remove process instances that are not longer there
		// TODO make smarter, e.g. by registering a listener
		// before starting execution and only updating changed instances
	}

	public class EnterpriseWorkItemHandler implements WorkItemHandler {

		public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
			WorkItemInfo workItemInfo = new WorkItemInfo();
			workItemInfo.setWorkItemId(workItem.getId());
			workItemInfo.setProcessInstanceId(workItem.getProcessInstanceId());
			workItemInfo.setName(workItem.getName());
			workItemInfo.setParameters(workItem.getParameters());
			ProcessServiceBean.this.manager.persist(workItemInfo);
			System.out.println("Persisted work item " + workItem.getName() + " [" + workItem.getId() + "]");
			for (Map.Entry<String, Object> entry: workItem.getParameters().entrySet()) {
				System.out.println("  " + entry.getKey() + " = " + entry.getValue());
			}
		}

		public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
			WorkItemInfo workItemInfo = ProcessServiceBean.this.manager.find(WorkItemInfo.class, workItem.getId());
			ProcessServiceBean.this.manager.remove(workItemInfo);
			System.out.println("Aborted work item " + workItem.getName() + " [" + workItem.getId() + "]");
		}

	}

}
