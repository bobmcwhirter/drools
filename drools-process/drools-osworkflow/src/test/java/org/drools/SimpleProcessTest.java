package org.drools;

import junit.framework.TestCase;

import org.drools.osworkflow.DroolsWorkflow;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.config.DefaultConfiguration;

public class SimpleProcessTest extends TestCase {
	
	public void testSimpleProcess() {
		Workflow workflow = new DroolsWorkflow();
		DefaultConfiguration config = new DefaultConfiguration();
		workflow.setConfiguration(config);
		try {
			workflow.initialize("simple", 1, null);
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}

}
