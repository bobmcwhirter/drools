package org.drools.task.service.hornetq;

import java.util.HashMap;
import java.util.Map;

import org.drools.SystemEventListener;
import org.drools.task.service.TaskServerHandler;
import org.drools.task.service.TaskService;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;

public class HornetQTaskServerHandler {
	
	private TaskServerHandler handler;
	private Map<String, ClientProducer> producers;
	
    public HornetQTaskServerHandler(TaskService service, SystemEventListener systemEventListener) {
        this.handler = new TaskServerHandler(service, systemEventListener);
        this.producers = new HashMap<String, ClientProducer>();
    }

    public void messageReceived(ClientSession session, Object message, String destination) throws Exception {
    	ClientProducer producer = producers.get(destination);
    	if (producer==null) {
    		producer = session.createProducer(destination);
    		producers.put(destination, producer);
    	}
		handler.messageReceived(new HornetQSessionWriter(session, producer), message);
    }

}