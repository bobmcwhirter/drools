/**
 * 
 */
package org.drools.task.service;

import org.drools.task.service.TaskClientHandler.AddTaskResponseHandler;

public class BlockingAddTaskResponseHandler implements AddTaskResponseHandler {
    private volatile long taskId;
    private volatile boolean wait = true;

    public synchronized void execute(long taskId) {
        this.taskId = taskId;
        wait = false;
        notifyAll();                
    }
    
    public synchronized long getTaskId() {
        if ( wait ) {                  
            try {
                wait( 3000 );
            } catch ( InterruptedException e ) {
                // swallow as this is just a notifiation
            }
        }
        
        if ( wait ) {
            throw new RuntimeException("Timeout : unable to retrieve Task Id" );
        }
        
        return taskId;
    }       
}