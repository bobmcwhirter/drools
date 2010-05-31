package org.drools.task.service;

import java.io.StringReader;
import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.drools.eventmessaging.EventKey;
import org.drools.eventmessaging.Payload;
import org.drools.task.BaseTest;
import org.drools.task.Status;
import org.drools.task.Task;
import org.drools.task.event.TaskClaimedEvent;
import org.drools.task.event.TaskEventKey;
import org.drools.task.event.TaskUserEvent;
import org.drools.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.drools.task.service.responsehandlers.BlockingEventResponseHandler;
import org.drools.task.service.responsehandlers.BlockingGetTaskResponseHandler;

public abstract class TaskServiceEventMessagingBaseTest extends BaseTest {

	protected TaskServer server;
    protected TaskClient client;

    public void testClaimEvent() throws Exception {      
        Map  vars = new HashedMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );                

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );         
        
        EventKey key = new TaskEventKey(TaskClaimedEvent.class, taskId );           
        BlockingEventResponseHandler handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, true, handler );
        Thread.sleep( 3000 );
        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        handler.waitTillDone( 5000 );
        
        Payload payload = handler.getPayload();
        TaskUserEvent event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );        
    }

}
