package org.drools.task.service.mina;

import java.util.Properties;

import org.drools.SystemEventListenerFactory;
import org.drools.task.service.TaskClient;
import org.drools.task.service.TaskServiceDeadlinesBaseTest;
import org.drools.task.service.mina.MinaTaskClientConnector;
import org.drools.task.service.mina.MinaTaskServer;
import org.drools.task.service.mina.MinaTaskClientHandler;
import org.subethamail.wiser.Wiser;

public class TaskServiceDeadlinesMinaQTest extends TaskServiceDeadlinesBaseTest {

	private MinaTaskServer server;

	@Override
	protected void setUp() throws Exception {        
		super.setUp();

		setConf(new Properties());
		getConf().setProperty("mail.smtp.host", "localhost");
		getConf().setProperty("mail.smtp.port", "2345");
		getConf().setProperty("from", "from@domain.com");
		getConf().setProperty("replyTo", "replyTo@domain.com");
		getConf().setProperty("defaultLanguage", "en-UK");

		server = new MinaTaskServer(taskService);
		Thread thread = new Thread(server);
		thread.start();
		Thread.sleep(500);

		client = new TaskClient(new MinaTaskClientConnector("client 1",
								new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
		client.connect("127.0.0.1", 9123);

		setWiser(new Wiser());
		getWiser().setHostname(getConf().getProperty("mail.smtp.host"));
		getWiser().setPort(Integer.parseInt(getConf().getProperty("mail.smtp.port")));        
		getWiser().start();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		client.disconnect();
		server.stop();
		getWiser().stop();
	}

}
