/**
 * 
 */
package org.drools.task.service;

import org.drools.task.service.TaskClientHandler.SetDocumentResponseHandler;

public class BlockingSetContentResponseHandler implements SetDocumentResponseHandler {
    private volatile long contentId;
    private volatile boolean wait = true;

    public synchronized void execute(long contentId) {
        this.contentId = contentId;
        wait = false;
        notifyAll();                
    }    
    
    public synchronized long getContentId() {
        if ( wait ) {                  
            try {
                wait( 3000 );
            } catch ( InterruptedException e ) {
                // swallow as this is just a notifiation
            }
        }
        
        if ( wait ) {
            throw new RuntimeException("Timeout : unable to retrieve Content Id" );
        }
        
        return contentId;
    }
}