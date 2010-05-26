package org.drools.task.service;

import org.drools.task.BaseTest;
import org.drools.task.service.hornetq.HornetQTaskServer;
import org.drools.task.service.mina.MinaTaskServer;

public class TaskServerTest extends BaseTest {

	private HornetQTaskServer hornetQServer;
	private MinaTaskServer minaTaskServer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testHorneQTaskServer() throws Exception {
		hornetQServer = new HornetQTaskServer(taskService, 10101);
		Thread t = new Thread(hornetQServer);
		t.start();
		Thread.sleep(5000);
		hornetQServer.stop();
	}
	
	public void testMinaTaskServer() throws Exception {
		minaTaskServer = new MinaTaskServer(taskService);
		Thread t = new Thread(minaTaskServer);
		t.start();
		Thread.sleep(5000);
		minaTaskServer.stop();
	}

}
