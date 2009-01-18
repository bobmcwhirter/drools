package org.drools.persistence.session;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.marshalling.PlaceholderResolverStrategyFactory;
import org.drools.persistence.Persister;
import org.drools.persistence.jpa.JPAPersister;

public class JPAPersisterManager {

	private EntityManagerFactory emf;
	private PlaceholderResolverStrategyFactory factory;
	
	public JPAPersisterManager(PlaceholderResolverStrategyFactory factory, EntityManagerFactory emf) {
		if (emf == null) {
			this.emf = Persistence.createEntityManagerFactory("org.drools.persistence.jpa");
		} else {
			this.emf = emf;
		}
		this.factory = factory;
	}
	
	public JPAPersisterManager(PlaceholderResolverStrategyFactory factory) {
		this(factory, null);
	}
	
	public void dispose() {
		emf.close();
	}
	
	public Persister<StatefulSession> getSessionPersister(StatefulSession session) {
		return new JPAPersister<StatefulSession>(emf, new StatefulSessionSnapshotter(session, factory));
	}
	
	public Persister<StatefulSession> getSessionPersister(RuleBase ruleBase) {
		return new JPAPersister<StatefulSession>(emf, new StatefulSessionSnapshotter(ruleBase.newStatefulSession(), factory));
	}
	
	public Persister<StatefulSession> getSessionPersister(RuleBase ruleBase, SessionConfiguration conf) {
		return new JPAPersister<StatefulSession>(emf, new StatefulSessionSnapshotter(ruleBase.newStatefulSession(conf), factory));
	}
	
	public Persister<StatefulSession> getSessionPersister(String uniqueId, RuleBase ruleBase) {
		return getSessionPersister(uniqueId, ruleBase, null);
	}
	
	public Persister<StatefulSession> getSessionPersister(String uniqueId, RuleBase ruleBase, SessionConfiguration conf) {
		Persister<StatefulSession> persister = new JPAPersister<StatefulSession>(
			emf, new StatefulSessionSnapshotter(ruleBase, conf, factory));
		persister.setUniqueId(uniqueId);
		persister.load();
		return persister;
	}
	
}
