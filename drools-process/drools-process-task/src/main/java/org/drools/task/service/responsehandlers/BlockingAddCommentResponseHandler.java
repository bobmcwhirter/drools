/**
 *
 */
package org.drools.task.service.responsehandlers;

import org.drools.task.service.TaskClientHandler.AddCommentResponseHandler;

public class BlockingAddCommentResponseHandler extends AbstractBlockingResponseHandler
        implements AddCommentResponseHandler {

    private static final int COMMENT_ID_WAIT_TIME = 10000;

    private volatile long commentId;

    public synchronized void execute(long commentId) {
        this.commentId = commentId;
        setDone(true);
    }

    public long getCommentId() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means commentId is available 
        boolean done = waitTillDone(COMMENT_ID_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve Comment Id");
        }

        return commentId;
    }
}