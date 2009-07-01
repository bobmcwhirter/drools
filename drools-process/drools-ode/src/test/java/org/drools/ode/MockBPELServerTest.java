package org.drools.ode;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.ode.bpel.evt.BpelEvent;
import org.apache.ode.bpel.iapi.BpelEventListener;
import org.apache.ode.utils.DOMUtils;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.compiler.PackageBuilder;
import org.drools.ode.instance.EmbeddedBPELServer;

public class MockBPELServerTest extends TestCase {
	
	private EmbeddedBPELServer server;
	
    protected void setUp() throws Exception {
    	PackageBuilder kbuilder = new PackageBuilder();
    	RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackages(kbuilder.getPackages());
		
        server = new EmbeddedBPELServer(ruleBase.newStatefulSession());
        server.addEventListener(new BpelEventListener() {
			public void onEvent(BpelEvent e) {
				System.out.println(e);
			}
			public void startup(Properties arg0) {
			}
			public void shutdown() {
			}
        });
        server.deploy(new File(new URI(this.getClass().getResource("/HelloWorld3").toString())));
    }

    protected void tearDown() throws Exception {
        server.shutdown();
        server = null;
    }
    
    public void testStartProcess() throws Exception {
    	String bodyString = 
    		"<ns1:hello xmlns:ns1=\"http://ode/bpel/unit-test.wsdl\">\n" +
    		"  <TestPart xmlns=\"\">Hello</TestPart>\n" +
    		"</ns1:hello>";
    	final String nameSpace = "http://ode/bpel/unit-test.wsdl";
    	QName processQName = new QName(nameSpace, "HelloService");
    	String operation = "hello";
    	System.out.println("operation = " + operation);
		System.out.println("body = " + bodyString);
		System.out.println("service = " + processQName);
		server.invoke(processQName, operation, DOMUtils.stringToDOM(bodyString));
        server.waitForBlocking();
    }

}
