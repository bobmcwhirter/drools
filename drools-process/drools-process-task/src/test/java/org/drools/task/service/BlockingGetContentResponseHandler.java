/**
 * 
 */
package org.drools.task.service;

import org.drools.task.Content;
import org.drools.task.service.TaskClientHandler.GetContentResponseHandler;

public class BlockingGetContentResponseHandler implements GetContentResponseHandler {
    private volatile Content content;

    public synchronized void execute(Content content) {
        this.content = content;
        notifyAll();                
    }
    
    public synchronized Content getContent() {
        if ( content == null ) {                  
            try {
                wait( 3000 );
            } catch ( InterruptedException e ) {
                // swallow as this is just a notifiation
            }
        }
        
        if ( content == null ) {
            throw new RuntimeException("Timeout : unable to retrieve Attachment Content" );
        }
        
        return content;
    }       
}