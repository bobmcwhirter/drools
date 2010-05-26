package org.drools.process.workitem.wsht.mina;

import org.drools.SystemEventListenerFactory;
import org.drools.process.workitem.wsht.WSHumanTaskHandler;
import org.drools.process.workitem.wsht.WSHumanTaskHandlerBaseTest;
import org.drools.task.service.TaskClient;
import org.drools.task.service.TaskServer;
import org.drools.task.service.mina.MinaTaskClientConnector;
import org.drools.task.service.mina.MinaTaskClientHandler;
import org.drools.task.service.mina.MinaTaskServer;

public class WSHumanTaskHandlerMinaTest extends WSHumanTaskHandlerBaseTest {

	private TaskServer server;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		server = new MinaTaskServer(taskService);
		Thread thread = new Thread(server);
		thread.start();
		Thread.sleep(500);
		setClient(new TaskClient(new MinaTaskClientConnector("client 1",
								new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener()))));
		getClient().connect("127.0.0.1", 9123);
		setHandler(new WSHumanTaskHandler());
	}

	protected void tearDown() throws Exception {
		getHandler().dispose();
		getClient().disconnect();
		server.stop();
		super.tearDown();
	}

}
