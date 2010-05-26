package org.drools.task.service.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.drools.SystemEventListener;
import org.drools.task.service.TaskServerHandler;
import org.drools.task.service.TaskService;

public class MinaTaskServerHandler extends IoHandlerAdapter {
	
    private TaskServerHandler handler;

    public MinaTaskServerHandler(TaskService service, SystemEventListener systemEventListener) {
        this.handler = new TaskServerHandler(service, systemEventListener);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    	handler.exceptionCaught(new MinaSessionWriter(session), cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        handler.messageReceived(new MinaSessionWriter(session), message);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
//    	handler.sessionIdle(session, status);
    }
}