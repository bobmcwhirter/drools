package com.sample;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.process.workitem.archive.ArchiveWorkItemHandler;
import org.drools.process.workitem.email.EmailWorkItemHandler;
import org.drools.process.workitem.exec.ExecWorkItemHandler;
import org.drools.process.workitem.finder.FinderWorkItemHandler;
import org.drools.rule.Package;

/**
 * This is a sample file to launch a ruleflow.
 */
public class RuleFlowTest {

	public static final void main(String[] args) {
		try {
			RuleBase ruleBase = createKnowledgeBase();
			StatefulSession session = ruleBase.newStatefulSession();
			session.getWorkItemManager().registerWorkItemHandler("Finder", new FinderWorkItemHandler());
			session.getWorkItemManager().registerWorkItemHandler("Archive", new ArchiveWorkItemHandler());
			session.getWorkItemManager().registerWorkItemHandler("Exec", new ExecWorkItemHandler());
			EmailWorkItemHandler emailWorkItemHandler = new EmailWorkItemHandler();
			emailWorkItemHandler.setConnection("mail-out.example.com", "25", null, null);
			session.getWorkItemManager().registerWorkItemHandler("Email", emailWorkItemHandler);
			session.startProcess("com.sample.ruleflow");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static RuleBase createKnowledgeBase() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new InputStreamReader(
			RuleFlowTest.class.getResourceAsStream("/FileFinder.rf"));
		builder.addProcessFromXml(source);
		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(pkg);
		return ruleBase;
	}

}