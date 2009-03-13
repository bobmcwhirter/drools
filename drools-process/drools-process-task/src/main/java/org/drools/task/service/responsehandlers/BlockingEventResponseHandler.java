/**
 *
 */
package org.drools.task.service.responsehandlers;

import org.drools.eventmessaging.EventResponseHandler;
import org.drools.eventmessaging.Payload;

public class BlockingEventResponseHandler extends AbstractBlockingResponseHandler implements EventResponseHandler {
    private static final int PAYLOAD_WAIT_TIME = 10000;

    private volatile Payload payload;

    public synchronized void execute(Payload payload) {
        this.payload = payload;
        setDone(true);
    }

    public Payload getPayload() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means payload is available 
        boolean done = waitTillDone(PAYLOAD_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve event payload");
        }

        return payload;
    }
}