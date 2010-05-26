package org.drools.task.service.hornetq;

import org.drools.SystemEventListenerFactory;
import org.drools.task.service.TaskService;
import org.hornetq.core.config.Configuration;

public class HornetQTaskServer extends BaseHornetQTaskServer implements Runnable {

	public HornetQTaskServer(TaskService service, int port) {
		super(new HornetQTaskServerHandler(service, SystemEventListenerFactory.getSystemEventListener()), port, false);
	}

//	public HornetQTaskServer(TaskService service, int port, boolean standalone) {
//		super(new HornetQTaskServerHandler(service, SystemEventListenerFactory.getSystemEventListener()), port, standalone);
//	}

	public HornetQTaskServer(TaskService service, int port, Configuration configuration) {
		super(new HornetQTaskServerHandler(service, SystemEventListenerFactory.getSystemEventListener()), port, configuration, false);
	}

//	public HornetQTaskServer(TaskService service, int port, Configuration configuration, boolean standalone) {
//		super(new HornetQTaskServerHandler(service, SystemEventListenerFactory.getSystemEventListener()), port, configuration, standalone);
//	}
}