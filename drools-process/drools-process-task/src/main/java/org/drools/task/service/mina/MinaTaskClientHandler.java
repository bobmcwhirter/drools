package org.drools.task.service.mina;

import org.apache.mina.core.session.IoSession;
import org.drools.SystemEventListener;
import org.drools.task.service.TaskClient;
import org.drools.task.service.TaskClientHandler;

public class MinaTaskClientHandler extends BaseMinaHandler {

	private TaskClientHandler handler;
	
    public MinaTaskClientHandler(SystemEventListener systemEventListener) {
        this.handler = new TaskClientHandler(responseHandlers, systemEventListener);
    }

    public TaskClient getClient() {
        return handler.getClient();
    }

    public void setClient(TaskClient client) {
        handler.setClient(client);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    	handler.exceptionCaught(new MinaSessionWriter(session), cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
    	handler.messageReceived(new MinaSessionWriter(session), message);
    }

}