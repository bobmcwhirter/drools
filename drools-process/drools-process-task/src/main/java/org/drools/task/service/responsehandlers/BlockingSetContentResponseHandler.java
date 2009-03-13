/**
 *
 */
package org.drools.task.service.responsehandlers;

import org.drools.task.service.TaskClientHandler.SetDocumentResponseHandler;

public class BlockingSetContentResponseHandler extends AbstractBlockingResponseHandler implements SetDocumentResponseHandler {
    private static final int CONTENT_ID_WAIT_TIME = 10000;

    private volatile long contentId;

    public synchronized void execute(long contentId) {
        this.contentId = contentId;
        setDone(true);
    }

    public long getContentId() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means contentId is available 
        boolean done = waitTillDone(CONTENT_ID_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve Content Id");
        }

        return contentId;
    }
}