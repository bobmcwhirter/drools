package org.drools.task.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.drools.task.Content;
import org.drools.task.Task;
import org.drools.task.query.TaskSummary;

public class BaseMinaHandler extends IoHandlerAdapter
{       
    protected Map<Integer, ResponseHandler> responseHandlers;
    
    public BaseMinaHandler() {
        responseHandlers = new HashMap<Integer, ResponseHandler>();
    }
    
    public void addResponseHandler(int id, ResponseHandler responseHandler) {
        responseHandlers.put( id, responseHandler );
    }
    
    public static interface ResponseHandler {
        
    }
}