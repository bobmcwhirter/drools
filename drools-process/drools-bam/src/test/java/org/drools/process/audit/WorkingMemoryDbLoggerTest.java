package org.drools.process.audit;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.drools.rule.Package;

public class WorkingMemoryDbLoggerTest extends TestCase {

	public void testLogger() {
        // load the process
        RuleBase ruleBase = createKnowledgeBase();
        // create a new session
        StatefulSession session = ruleBase.newStatefulSession();
        new WorkingMemoryDbLogger(session);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // start process instance
        session.startProcess("com.sample.ruleflow");
        session.startProcess("com.sample.ruleflow");
        
        System.out.println("Checking process instances for process 'com.sample.ruleflow'");
        List<ProcessInstanceLog> processInstances
        	= ProcessInstanceDbLog.findProcessInstances("com.sample.ruleflow");
        for (Object result: processInstances) {
            System.out.println(result);
        }
        assertEquals(2, processInstances.size());
	}
	
    private static RuleBase createKnowledgeBase() {
        // create a builder
        PackageBuilder builder = new PackageBuilder();
        // load the process
        Reader source = new InputStreamReader(
            ProcessInstanceExecutor.class.getResourceAsStream("/ruleflow.rf"));
        builder.addProcessFromXml(source);
       // create the knowledge base 
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(pkg);
        return ruleBase;
    }
}
