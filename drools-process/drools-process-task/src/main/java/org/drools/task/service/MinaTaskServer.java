package org.drools.task.service;

import org.drools.SystemEventListenerFactory;


public class MinaTaskServer extends BaseMinaServer
    implements
    Runnable {
    public MinaTaskServer(TaskService service) {
        super( new TaskServerHandler( service, SystemEventListenerFactory.getSystemEventListener() ),
               9123 );
    }
}