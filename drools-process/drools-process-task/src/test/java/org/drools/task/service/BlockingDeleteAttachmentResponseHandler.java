/**
 * 
 */
package org.drools.task.service;

import org.drools.eventmessaging.EventResponseHandler;
import org.drools.eventmessaging.Payload;
import org.drools.task.service.TaskClientHandler.DeleteAttachmentResponseHandler;
import org.drools.task.service.TaskClientHandler.TaskOperationResponseHandler;

public class BlockingDeleteAttachmentResponseHandler extends AbstractBlockingResponseHandler implements DeleteAttachmentResponseHandler {
      public void setIsDone(boolean done) {
          synchronized ( this.done ) {
            this.done = done;
        }
      }
}