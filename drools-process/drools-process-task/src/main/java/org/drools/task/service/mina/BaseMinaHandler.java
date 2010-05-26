package org.drools.task.service.mina;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.drools.task.service.BaseHandler;
import org.drools.task.service.ResponseHandler;

public class BaseMinaHandler extends IoHandlerAdapter implements BaseHandler
{       
    protected Map<Integer, ResponseHandler> responseHandlers;
    
    public BaseMinaHandler() {
        responseHandlers = new HashMap<Integer, ResponseHandler>();
    }
    
    public void addResponseHandler(int id, ResponseHandler responseHandler) {
        responseHandlers.put( id, responseHandler );
    }
    
}