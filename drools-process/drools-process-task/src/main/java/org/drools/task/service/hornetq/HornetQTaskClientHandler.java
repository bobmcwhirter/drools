package org.drools.task.service.hornetq;

import java.util.HashMap;
import java.util.Map;

import org.drools.SystemEventListener;
import org.drools.task.service.TaskClient;
import org.drools.task.service.TaskClientHandler;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;

public class HornetQTaskClientHandler extends BaseHornetQHandler {
    
	private TaskClientHandler handler;
	private Map<String, ClientProducer> producers;
	
    public HornetQTaskClientHandler(SystemEventListener systemEventListener) {
        this.handler = new TaskClientHandler(responseHandlers, systemEventListener);
        this.producers = new HashMap<String, ClientProducer>();
    }

    public TaskClient getClient() {
        return handler.getClient();
    }

    public void setClient(TaskClient client) {
        handler.setClient(client);
    }

    public void exceptionCaught(ClientSession session, Throwable cause) throws Exception {
//    	handler.exceptionCaught(new HornetQSessionWriter(session, message), cause);
    }

    public void messageReceived(ClientSession session, Object message, String producerId) throws Exception {
    	ClientProducer producer = producers.get(producerId);
    	if (producer==null) {
    		producer = session.createProducer(producerId);
    	}
		handler.messageReceived(new HornetQSessionWriter(session, producer), message);
    }

}