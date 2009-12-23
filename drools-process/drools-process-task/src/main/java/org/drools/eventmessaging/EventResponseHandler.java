package org.drools.eventmessaging;

import org.drools.task.service.ResponseHandler;

public interface EventResponseHandler extends ResponseHandler {
    public void execute(Payload payload);
}
