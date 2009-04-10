package org.drools.task.service;

/**
 * Exception that is thrown when a <code>User</code> try to perform an <code>Operation</code> on a <code>Task</code>
 *
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 * @see org.drools.task.service.TaskServiceSession#taskOperation(Operation, long, String, String, ContentData)  
 */
public class PermissionDeniedException extends TaskException{
    public PermissionDeniedException(String message) {
        super(message);
    }
}
