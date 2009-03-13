/**
 *
 */
package org.drools.task.service.responsehandlers;

import org.drools.task.service.TaskClientHandler.DeleteAttachmentResponseHandler;

public class BlockingDeleteAttachmentResponseHandler extends AbstractBlockingResponseHandler implements DeleteAttachmentResponseHandler {
    public void setIsDone(boolean done) {
        setDone(done);
    }
}