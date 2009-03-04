/**
 * 
 */
package org.drools.task.service.responsehandlers;

import org.drools.task.service.TaskClientHandler.TaskOperationResponseHandler;

public class BlockingTaskOperationResponseHandler extends AbstractBlockingResponseHandler implements TaskOperationResponseHandler {
      public void setIsDone(boolean done) {
          synchronized ( this.done ) {
            this.done = done;
        }
      }
}