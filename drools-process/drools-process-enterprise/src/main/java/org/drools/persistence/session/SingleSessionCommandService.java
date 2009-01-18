package org.drools.persistence.session;

import javax.transaction.xa.XAException;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.common.InternalWorkingMemory;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.persistence.Persister;
import org.drools.persistence.Transaction;
import org.drools.persistence.jpa.JPAPersister;
import org.drools.persistence.processinstance.JPAProcessInstanceManager;
import org.drools.persistence.processinstance.JPASignalManager;
import org.drools.persistence.processinstance.JPAWorkItemManager;
import org.drools.process.command.Command;
import org.drools.process.command.CommandService;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;

public class SingleSessionCommandService implements CommandService {

	private Persister<StatefulSession> persister;
	private PersistenceConfig pconfig;
	
	public SingleSessionCommandService(RuleBase ruleBase) {
		this(ruleBase, null);
	}
	
	public SingleSessionCommandService(RuleBase ruleBase, SessionConfiguration conf) {
		if (conf == null) {
			conf = new SessionConfiguration();
		}
		PersistenceConfig pconfig = getPersistenceConfig();
		if (pconfig.getSessionId() != null) { 
			persister = new JPAPersisterManager(
				pconfig.getPlaceholderResolverStrategyFactory(),
				pconfig.getEntityManagerFactory())
					.getSessionPersister(pconfig.getSessionId(), ruleBase, conf);
		} else {
			persister = new JPAPersisterManager(
				pconfig.getPlaceholderResolverStrategyFactory(),
				pconfig.getEntityManagerFactory())
					.getSessionPersister(ruleBase, conf);
		}
		init();
	}
	
	public SingleSessionCommandService(KnowledgeBase kbase) {
		this(((KnowledgeBaseImpl) kbase).getRuleBase());
	}
	
	public SingleSessionCommandService(KnowledgeBase kbase, KnowledgeSessionConfiguration conf) {
		this(((KnowledgeBaseImpl) kbase).getRuleBase(), (SessionConfiguration) conf);
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
	
	public StatefulSession getSession() {
		return persister.getObject();
	}
	
	public <T> T execute(Command<T> command) {
		StatefulSession session = persister.getObject();
		Transaction transaction = persister.getTransaction();
		try {
			transaction.start();
			T result = command.execute(session);
			transaction.commit();
			if (pconfig.getSessionId() == null) {
				pconfig.setSessionId(getSessionId());
			}
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
	
	public PersistenceConfig getPersistenceConfig() {
		Environment environment = EnvironmentFactory.newEnvironment();
		pconfig = (PersistenceConfig) environment.get(PersistenceConfig.class.getName());
		if (pconfig == null) {
			pconfig = new PersistenceConfig();
			environment.set(PersistenceConfig.class.getName(), pconfig);
		}
		return pconfig;
	}
}