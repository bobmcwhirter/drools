package org.drools.task.service.hornetq;

import java.util.Properties;

import org.drools.SystemEventListenerFactory;
import org.drools.task.service.TaskClient;
import org.drools.task.service.TaskServiceDeadlinesBaseTest;
import org.subethamail.wiser.Wiser;

public class TaskServiceDeadlinesHornetQTest extends TaskServiceDeadlinesBaseTest {

	@Override
	protected void setUp() throws Exception {        
		super.setUp();

		setConf(new Properties());
		getConf().setProperty("mail.smtp.host", "localhost");
		getConf().setProperty("mail.smtp.port", "2345");
		getConf().setProperty("from", "from@domain.com");
		getConf().setProperty("replyTo", "replyTo@domain.com");
		getConf().setProperty("defaultLanguage", "en-UK");

		server = new HornetQTaskServer(taskService, 5446);
		Thread thread = new Thread(server);
		thread.start();
		Thread.sleep(500);

		client = new TaskClient(new HornetQTaskClientConnector("client 1",
								new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
		client.connect("127.0.0.1", 5446);

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
