package org.drools.process.workitem.wsht.hornetq;

import org.drools.SystemEventListenerFactory;
import org.drools.process.workitem.wsht.WSHumanTaskHandler;
import org.drools.process.workitem.wsht.WSHumanTaskHandlerBaseTest;
import org.drools.task.service.TaskClient;
import org.drools.task.service.TaskServer;
import org.drools.task.service.hornetq.HornetQTaskClientConnector;
import org.drools.task.service.hornetq.HornetQTaskClientHandler;
import org.drools.task.service.hornetq.HornetQTaskServer;

public class WSHumanTaskHandlerHornetQTest extends WSHumanTaskHandlerBaseTest {

	private TaskServer server;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		server = new HornetQTaskServer(taskService, 5446);
		Thread thread = new Thread(server);
		thread.start();
		Thread.sleep(500);
		setClient(new TaskClient(new HornetQTaskClientConnector("client 1",
								new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener()))));
		getClient().connect("127.0.0.1", 5446);
		WSHumanTaskHandler handler = new WSHumanTaskHandler();
		handler.setClient(getClient());
		setHandler(handler);
	}

	protected void tearDown() throws Exception {
		getHandler().dispose();
		getClient().disconnect();
		server.stop();
		super.tearDown();
	}

}
