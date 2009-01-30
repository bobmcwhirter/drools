package org.drools.suite;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.drools.osworkflow.test.Simple2ProcessTest;
import org.drools.osworkflow.test.SimpleProcessTest;

public class SimpleTestSuite extends TestCase {
	
	public static TestSuite suite()
    {
        TestSuite suite = new TestSuite();       
        suite.addTestSuite(SimpleProcessTest.class);
        suite.addTestSuite(Simple2ProcessTest.class);
        return suite;
    }

}
