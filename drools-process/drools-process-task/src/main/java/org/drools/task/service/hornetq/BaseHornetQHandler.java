package org.drools.task.service.hornetq;

import java.util.HashMap;
import java.util.Map;

import org.drools.task.service.BaseHandler;
import org.drools.task.service.ResponseHandler;

public class BaseHornetQHandler implements BaseHandler {

	protected Map<Integer, ResponseHandler> responseHandlers;

	public BaseHornetQHandler() {
		responseHandlers = new HashMap<Integer, ResponseHandler>();
	}

	public void addResponseHandler(int id, ResponseHandler responseHandler) {
		responseHandlers.put( id, responseHandler );
	}

}