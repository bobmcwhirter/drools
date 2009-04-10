package org.drools.task.service;

/**
 * Exception thrown from the <code>TaskServiceSession</code> when there is a problem adding task. This exception
 * is specififically thrown when custom logic in rules has not allowed the <code>Task</code> to be added.
 *
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 * @see org.drools.task.service.TaskServiceSession#addTask(org.drools.task.Task, ContentData)
 */
public class CannotAddTaskException extends TaskException {
    public CannotAddTaskException(String message) {
        super(message);
    }

    public CannotAddTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
