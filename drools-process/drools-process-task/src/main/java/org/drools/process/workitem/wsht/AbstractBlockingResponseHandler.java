/**
 *
 */
package org.drools.process.workitem.wsht;

import org.drools.task.service.BaseMinaHandler.ResponseHandler;

public abstract class AbstractBlockingResponseHandler implements ResponseHandler {

    private volatile boolean done;
    private String error;

    public boolean hasError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public synchronized boolean isDone() {
        return done;
    }

    protected synchronized void setDone(boolean done) {
        this.done = done;
        notifyAll();
    }

    /**
     * This method will wait the specified amount of time in milliseconds for the response to
     * be completed. Completed is determined via the <field>done</field>. Returns true if the
     * reponse was completed in time, false otherwise
     *
     * @param time max time to wait
     * @return true if response is available, false otherwise
     */
    public synchronized boolean waitTillDone(long time) {

        if (!isDone()) {
            try {
                wait(time);
            } catch (InterruptedException e) {
                // swallow and return state of done
            }
        }

        return isDone();
    }
}