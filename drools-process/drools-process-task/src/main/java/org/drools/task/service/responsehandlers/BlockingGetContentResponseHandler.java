/**
 *
 */
package org.drools.task.service.responsehandlers;

import org.drools.task.Content;
import org.drools.task.service.TaskClientHandler.GetContentResponseHandler;

public class BlockingGetContentResponseHandler extends AbstractBlockingResponseHandler implements GetContentResponseHandler {
    private static final int CONTENT_WAIT_TIME = 10000;

    private volatile Content content;

    public synchronized void execute(Content content) {
        this.content = content;
        setDone(true);
    }

    public Content getContent() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means content is available 
        boolean done = waitTillDone(CONTENT_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve Attachment Content");
        }

        return content;
    }
}