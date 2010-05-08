/**
 *
 */
package org.drools.process.workitem.wsht;

import org.drools.eventmessaging.EventResponseHandler;
import org.drools.eventmessaging.Payload;
import org.drools.task.service.responsehandlers.AbstractBlockingResponseHandler;

public class BlockingEventResponseHandler extends AbstractBlockingResponseHandler implements EventResponseHandler {
    // todo why is this timeout different from the others?? - also, if this should be the same
    // they can be collasped into the base class
    private static final int DEFAULT_WAIT_TIME = 1000000;

    private volatile Payload payload;

    public synchronized void execute(Payload payload) {
        this.payload = payload;
        setDone(true);
    }

    public Payload getPayload() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means payload is available 
        boolean done = waitTillDone(DEFAULT_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve event payload");
        }

        return payload;
    }
    
    public boolean isRemove() {
    	return true;
    }

}