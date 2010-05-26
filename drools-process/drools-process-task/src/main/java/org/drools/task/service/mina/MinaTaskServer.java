package org.drools.task.service.mina;

import org.drools.SystemEventListenerFactory;
import org.drools.task.service.TaskService;


public class MinaTaskServer extends BaseMinaTaskServer {
    public MinaTaskServer(TaskService service) {
        super( new MinaTaskServerHandler( service, SystemEventListenerFactory.getSystemEventListener() ),
               9123 );
    }
    
    public MinaTaskServer(TaskService service, int port) {
        super( new MinaTaskServerHandler( service, SystemEventListenerFactory.getSystemEventListener() ),
               port );
    }
}