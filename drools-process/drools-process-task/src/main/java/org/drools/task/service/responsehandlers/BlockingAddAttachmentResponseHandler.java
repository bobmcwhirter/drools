/**
 *
 */
package org.drools.task.service.responsehandlers;

import org.drools.task.service.TaskClientHandler.AddAttachmentResponseHandler;

public class BlockingAddAttachmentResponseHandler extends AbstractBlockingResponseHandler implements AddAttachmentResponseHandler {
    private static final int ATTACHMENT_ID_WAIT_TIME = 10000;
    private static final int CONTENT_ID_WAIT_TIME = 3000;

    private volatile long attachmentId;
    private volatile long contentId;

    public synchronized void execute(long attachmentId, long contentId) {
        this.attachmentId = attachmentId;
        this.contentId = contentId;
        setDone(true);
    }

    public  long getAttachmentId() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means attachmentId is available
        boolean done = waitTillDone(ATTACHMENT_ID_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve Attachment Id");
        }

        return attachmentId;
    }

    public long getContentId() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means contentId is available
        boolean done = waitTillDone(CONTENT_ID_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve Attachment Content Id");
        }

        return contentId;
    }
}