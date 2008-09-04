/**
 * 
 */
package org.drools.task.service;

import org.drools.task.AttachmentContent;
import org.drools.task.service.TaskClientHandler.GetAttachmentContentResponseHandler;

public class BlockingGetAttachmentContentResponseHandler implements GetAttachmentContentResponseHandler {
    private volatile AttachmentContent attachmentContent;

    public synchronized void execute(AttachmentContent attachmentContent) {
        this.attachmentContent = attachmentContent;
        notifyAll();                
    }
    
    public synchronized AttachmentContent getAttachmentContent() {
        if ( attachmentContent == null ) {                  
            try {
                wait( 3000 );
            } catch ( InterruptedException e ) {
                // swallow as this is just a notifiation
            }
        }
        
        if ( attachmentContent == null ) {
            throw new RuntimeException("Timeout : unable to retrieve Attachment Content" );
        }
        
        return attachmentContent;
    }       
}