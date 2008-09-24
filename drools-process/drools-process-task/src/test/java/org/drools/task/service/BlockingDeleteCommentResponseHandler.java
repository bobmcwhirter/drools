/**
 * 
 */
package org.drools.task.service;

import org.drools.eventmessaging.EventResponseHandler;
import org.drools.eventmessaging.Payload;
import org.drools.task.service.TaskClientHandler.DeleteCommentResponseHandler;
import org.drools.task.service.TaskClientHandler.TaskOperationResponseHandler;

public class BlockingDeleteCommentResponseHandler extends AbstractBlockingResponseHandler implements DeleteCommentResponseHandler {
      public void setIsDone(boolean done) {
          synchronized ( this.done ) {
            this.done = done;
        }
      }
}