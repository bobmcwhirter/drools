package org.drools.task.service.mina;

import org.drools.SystemEventListenerFactory;
import org.drools.task.service.IcalBaseTest;
import org.drools.task.service.TaskClient;
import org.drools.task.service.mina.MinaTaskClientConnector;
import org.drools.task.service.mina.MinaTaskServer;
import org.drools.task.service.mina.MinaTaskClientHandler;
import org.drools.util.ChainedProperties;
import org.drools.util.ClassLoaderUtil;
import org.subethamail.wiser.Wiser;

public class IcalMinaTest extends IcalBaseTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ChainedProperties props = new ChainedProperties("process.email.conf", ClassLoaderUtil.getClassLoader(null, getClass(), false) );
		setEmailHost(props.getProperty("host", "locahost"));
		setEmailPort(props.getProperty("port", "2345"));        

		server = new MinaTaskServer(taskService);
		Thread thread = new Thread(server);
		thread.start();
		Thread.sleep(500);

		client = new TaskClient(new MinaTaskClientConnector("client 1",
						new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
		client.connect("127.0.0.1", 9123);

		setWiser(new Wiser());
		getWiser().setHostname(getEmailHost());
		getWiser().setPort( Integer.parseInt(getEmailPort()));         
		getWiser().start();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		client.disconnect();
		server.stop();
		getWiser().stop();
	}

}
