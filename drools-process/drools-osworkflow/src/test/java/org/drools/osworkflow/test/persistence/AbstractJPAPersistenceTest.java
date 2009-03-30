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
import org.drools.marshalling.impl.ProcessMarshallerRegistry;
import org.drools.osworkflow.core.OSWorkflowProcess;
import org.drools.osworkflow.persistence.marshaller.OSWorkflowProcessInstanceMarshaller;
import org.drools.rule.Package;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.util.ArrayList;
import java.util.List;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.builder.impl.KnowledgeBuilderImpl;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;

public abstract class AbstractJPAPersistenceTest extends TestCase {

    PoolingDataSource            ds1;
    private EntityManagerFactory emf;

    protected void setUp() {
        ds1 = new PoolingDataSource();
        ds1.setUniqueName( "jdbc/testDS1" );
        ds1.setClassName( "org.h2.jdbcx.JdbcDataSource" );
        ds1.setMaxPoolSize( 3 );
        ds1.setAllowLocalTransactions( true );
        ds1.getDriverProperties().put( "user",
                                       "sa" );
        ds1.getDriverProperties().put( "password",
                                       "sasa" );
        ds1.getDriverProperties().put( "URL",
                                       "jdbc:h2:mem:mydb" );
        ds1.init();

        emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
    }

    protected void tearDown() {
        emf.close();
        ds1.close();
    }

	public EntityManagerFactory getEmf() {
		return emf;
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
	protected static KnowledgeBase createKnowledgeBase(RuleBaseConfiguration conf, String resourceName) {
		try {
			// create a builder
			
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

            kbuilder.add(ResourceFactory.newClassPathResource(resourceName), ResourceType.DRF);

            KnowledgeBuilderErrors errors = kbuilder.getErrors();
            if (errors.size() > 0) {
                for (KnowledgeBuilderError error: errors) {
                    System.err.println(error);
                }
                throw new IllegalArgumentException("Could not parse knowledge.");
            }
            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
			
			// create the knowledge base
			KnowledgeBase ruleBase = KnowledgeBaseFactory.newKnowledgeBase(conf);
            
			ruleBase.addKnowledgePackages(kbuilder.getKnowledgePackages());
			return ruleBase;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
			return null;
		}
	}

}
