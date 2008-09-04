/**
 * 
 */
package org.drools.task.service;

import org.drools.task.Task;
import org.drools.task.service.TaskClientHandler.GetTaskResponseHandler;

public class BlockingGetTaskResponseHandler implements GetTaskResponseHandler {
    private volatile Task task;

    public synchronized void execute(Task task) {
        this.task = task;
        notifyAll();                
    }
    
    public synchronized Task getTask() {
        if ( task == null ) {                  
            try {
                wait( 3000 );
            } catch ( InterruptedException e ) {
                // swallow as this is just a notifiation
            }
        }
        
        if ( task == null ) {
            throw new RuntimeException("Timeout : unable to retrieve Task Id" );
        }
        
        return task;
    }       
}