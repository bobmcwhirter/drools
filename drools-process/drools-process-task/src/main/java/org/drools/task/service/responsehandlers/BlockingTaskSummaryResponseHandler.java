package org.drools.task.service.responsehandlers;

import org.drools.task.query.TaskSummary;
import org.drools.task.service.TaskClientHandler.TaskSummaryResponseHandler;

import java.util.List;

public class BlockingTaskSummaryResponseHandler extends AbstractBlockingResponseHandler implements TaskSummaryResponseHandler {

    private static final int RESULTS_WAIT_TIME = 10000;

    private volatile List<TaskSummary> results;

    public synchronized void execute(List<TaskSummary> results) {
        this.results = results;
        setDone(true);
    }

    public List<TaskSummary> getResults() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means results is available 
        boolean done = waitTillDone(RESULTS_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve results");
        }

        return results;
    }

}

