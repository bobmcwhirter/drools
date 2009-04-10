package org.drools.task.service;

/**
 * Base class for all exceptions for the task related activities
 *
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 * @see org.drools.task.service.TaskServiceSession#addTask(org.drools.task.Task, ContentData)
 */
public abstract class TaskException extends RuntimeException {

    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
