package org.drools.persistence.session;

import javax.persistence.EntityManagerFactory;

import org.drools.marshalling.PlaceholderResolverStrategyFactory;

public class PersistenceConfig {
	
	private String sessionId;
	private PlaceholderResolverStrategyFactory placeholderResolverStrategyFactory;
	private EntityManagerFactory entityManagerFactory;
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public PlaceholderResolverStrategyFactory getPlaceholderResolverStrategyFactory() {
		return placeholderResolverStrategyFactory;
	}
	
	public void setPlaceholderResolverStrategy(
			PlaceholderResolverStrategyFactory placeholderResolverStrategyFactory) {
		this.placeholderResolverStrategyFactory = placeholderResolverStrategyFactory;
	}
	
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}
	
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

}
