package org.drools.osworkflow.test.persistence;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.compiler.PackageBuilder;
import org.drools.marshalling.ProcessMarshallerRegistry;
import org.drools.osworkflow.core.OSWorkflowProcess;
import org.drools.osworkflow.instance.OSWorkflowProcessInstance;
import org.drools.osworkflow.persistence.marshaller.OSWorkflowProcessInstanceMarshaller;
import org.drools.rule.Package;

public abstract class AbstractJPAPersistenceTestCase extends TestCase {

	private EntityManagerFactory emf;
	
	public EntityManagerFactory getEmf() {
		return emf;
	}

	public void setEmf(EntityManagerFactory emf) {
		this.emf = emf;
	}


	protected void setUp() {
		emf = Persistence.createEntityManagerFactory("org.drools.persistence.jpa");
	}

	protected void tearDown() {
		emf.close();
	}

	protected Properties setupCommonProperties() {
		Properties properties = new Properties();
		properties.setProperty("drools.commandService", "org.drools.persistence.session.SingleSessionCommandService");
		properties.setProperty("drools.processInstanceManagerFactory", "org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory");
		properties.setProperty("drools.workItemManagerFactory", "org.drools.persistence.processinstance.JPAWorkItemManagerFactory");
		properties.setProperty("drools.processSignalManagerFactory", "org.drools.persistence.processinstance.JPASignalManagerFactory");
		//TODO configure this with IOC
		ProcessMarshallerRegistry.INSTANCE.register(OSWorkflowProcess.OSWORKFLOW_TYPE, OSWorkflowProcessInstanceMarshaller.INSTANCE);
		return properties;
	}
	
	/**
	 * Process definition.
	 */
	protected static RuleBase createKnowledgeBase(RuleBaseConfiguration conf, String resourceName) {
		try {
			// create a builder
			PackageBuilder builder = new PackageBuilder();
			// load the process
			Reader source = new InputStreamReader(OSWorkFlowPersistenceTestCase.class
					.getResourceAsStream(resourceName));
			builder.addProcessFromXml(source);
			// create the knowledge base
			Package pkg = builder.getPackage();
			RuleBase ruleBase = RuleBaseFactory.newRuleBase(conf);
			ruleBase.addPackage(pkg);
			return ruleBase;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
			return null;
		}
	}

}
