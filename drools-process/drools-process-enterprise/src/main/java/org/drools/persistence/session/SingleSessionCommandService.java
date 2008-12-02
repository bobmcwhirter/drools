package org.drools.persistence.session;

import javax.transaction.xa.XAException;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.common.InternalWorkingMemory;
import org.drools.marshalling.PlaceholderResolverStrategyFactory;
import org.drools.persistence.Persister;
import org.drools.persistence.Transaction;
import org.drools.persistence.jpa.JPAPersister;
import org.drools.persistence.processinstance.JPAProcessInstanceManager;
import org.drools.persistence.processinstance.JPASignalManager;
import org.drools.persistence.processinstance.JPAWorkItemManager;
import org.drools.process.command.Command;
import org.drools.process.command.CommandService;

public class SingleSessionCommandService implements CommandService {

	private Persister<StatefulSession> persister;
	
	public SingleSessionCommandService(RuleBase ruleBase) {
		this(ruleBase, (PlaceholderResolverStrategyFactory) null);
	}
	
	public SingleSessionCommandService(RuleBase ruleBase, PlaceholderResolverStrategyFactory factory) {
		persister = new JPAPersisterManager(factory).getSessionPersister(ruleBase);
		init();
	}
	
	public SingleSessionCommandService(RuleBase ruleBase, String sessionId) {
		this(ruleBase, sessionId, null);
	}
	
	public SingleSessionCommandService(RuleBase ruleBase, String sessionId, PlaceholderResolverStrategyFactory factory) {
		persister = new JPAPersisterManager(factory).getSessionPersister(sessionId, ruleBase);
		init();
	}
	
	private void init() {
		StatefulSession session = persister.getObject();
		((JPAProcessInstanceManager) ((InternalWorkingMemory) session).getProcessInstanceManager())
			.setEntityManager(((JPAPersister<StatefulSession>) persister).getEntityManager());
		((JPAWorkItemManager) ((InternalWorkingMemory) session).getWorkItemManager())
			.setEntityManager(((JPAPersister<StatefulSession>) persister).getEntityManager());
		((JPASignalManager) ((InternalWorkingMemory) session).getSignalManager())
			.setEntityManager(((JPAPersister<StatefulSession>) persister).getEntityManager());
		((JPASignalManager) ((InternalWorkingMemory) session).getSignalManager())
			.setPersister(persister);
	}
	
	public Object execute(Command command) {
		StatefulSession session = persister.getObject();
		Transaction transaction = persister.getTransaction();
		try {
			transaction.start();
			Object result = command.execute(session);
			transaction.commit();
			return result;
		} catch (Throwable t) {
			t.printStackTrace();
			try {
				transaction.rollback();
				throw new RuntimeException("Could not execute command", t);
			} catch (XAException e) {
				throw new RuntimeException("Could not rollback transaction", e);
			}
		}
	}
	
	public void dispose() {
		StatefulSession session = persister.getObject();
		if (session != null) {
			session.dispose();
		}
	}
	
	public String getSessionId() {
		return persister.getUniqueId(); 
	}

}
