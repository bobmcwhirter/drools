/**
 * 
 */
package org.drools.task.service;

import org.drools.eventmessaging.EventResponseHandler;
import org.drools.eventmessaging.Payload;

public class BlockingEventResponseHandler implements EventResponseHandler {
    private volatile Payload payload;
    private volatile boolean wait;

    public synchronized void execute(Payload payload) {
        this.payload = payload;
        wait = true;
        notifyAll();                
    }
    
    public synchronized Payload getPayload() {
        if ( !wait ) {                  
            try {
                wait( 300000 );
            } catch ( InterruptedException e ) {
                // swallow as this is just a notification
            }
        }
        
        if ( !wait ) {
            throw new RuntimeException("Timeout : unable to retrieve event payload" );
        }
        
        return payload;
    }       
}