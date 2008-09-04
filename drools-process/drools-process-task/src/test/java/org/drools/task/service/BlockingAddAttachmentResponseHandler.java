/**
 * 
 */
package org.drools.task.service;

import org.drools.task.service.TaskClientHandler.AddAttachmentResponseHandler;

public class BlockingAddAttachmentResponseHandler implements AddAttachmentResponseHandler {
    private volatile long attachmentId ;
    private volatile long contentId;
    private volatile boolean wait = true;

    public synchronized void execute(long attachmentId, long contentId) {
        this.attachmentId = attachmentId;
        this.contentId = contentId;
        wait = false;
        notifyAll();                
    }
    
    public synchronized long getAttachmentId() {
        if ( wait ) {                  
            try {
                wait( 3000 );
            } catch ( InterruptedException e ) {
                // swallow as this is just a notifiation
            }
        }
        
        if ( wait ) {
            throw new RuntimeException("Timeout : unable to retrieve Attachment Id" );
        }
        
        return attachmentId;
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
            throw new RuntimeException("Timeout : unable to retrieve Attachment Content Id" );
        }
        
        return contentId;
    }
}