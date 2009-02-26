package org.drools.osworkflow.test;

import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
 
public class SimpleDroolsFlowTest extends TestCase {
 
	public void testSimple() {
		try {
			// load the process
			RuleBase ruleBase = createKnowledgeBase();
			// create a new session
			StatefulSession session = ruleBase.newStatefulSession();
			// start a new process instance
			session.startProcess("simple");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private RuleBase createKnowledgeBase() throws Exception {
		// create a builder
		PackageBuilder builder = new PackageBuilder();
		// load the process
		Reader source = new InputStreamReader(SimpleDroolsFlowTest.class
				.getResourceAsStream("/simple.rf"));
		builder.addProcessFromXml(source);
		// create the knowledge base
		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(pkg);
		return ruleBase;
	}

}
