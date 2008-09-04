/**
 * 
 */
package org.drools.task.service;

import org.drools.task.service.TaskClientHandler.AddCommentResponseHandler;

public class BlockingAddCommentResponseHandler implements AddCommentResponseHandler {
    private volatile long commentId;
    private volatile boolean wait = true;

    public synchronized void execute(long commentId) {
        this.commentId = commentId;
        wait = false;
        notifyAll();                
    }
    
    public synchronized long getCommentId() {
        if ( wait ) {                  
            try {
                wait( 3000 );
            } catch ( InterruptedException e ) {
                // swallow as this is just a notifiation
            }
        }
        
        if ( wait ) {
            throw new RuntimeException("Timeout : unable to retrieve Task Id" );
        }
        
        return commentId;
    }       
}