package org.drools.task;

public class TaskOperation {
    public void changeStatus(Task task, Status newStatus, User user) {
        switch (newStatus) {
            case Reserved : {
                // must be business admin/potential owner
                // claim, stop
                break;
            }
            case InProgress : {
                // must be actual owner
                // start
            }
            case Ready : {
                // must be business admin/potential owner
                // release
                break;
            }
            case Suspended : {
                break;
            }
            case Completed : {
                break;
            }
        }
    }
   
}
