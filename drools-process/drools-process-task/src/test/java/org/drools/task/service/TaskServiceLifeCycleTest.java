package org.drools.task.service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.collections.map.HashedMap;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.task.AccessType;
import org.drools.task.Attachment;
import org.drools.task.AttachmentContent;
import org.drools.task.BaseTest;
import org.drools.task.Comment;
import org.drools.task.Deadline;
import org.drools.task.Status;
import org.drools.task.Task;
import org.drools.task.query.TaskSummary;
import org.drools.task.service.TaskClientHandler.AddAttachmentResponseHandler;
import org.drools.task.service.TaskClientHandler.AddCommentResponseHandler;
import org.drools.task.service.TaskClientHandler.AddTaskResponseHandler;
import org.drools.task.service.TaskClientHandler.GetAttachmentContentResponseHandler;
import org.drools.task.service.TaskClientHandler.GetTaskResponseHandler;
import org.drools.task.service.TaskClientHandler.TaskSummaryResponseHandler;
import org.drools.task.service.TaskServiceEscalationTest.MockEscalatedDeadlineHandler.Item;
import org.drools.task.service.TaskServiceTest.BlockingAllOpenTasksForUseResponseHandler;
import org.drools.task.utils.CollectionUtils;

import junit.framework.TestCase;

public class TaskServiceLifeCycleTest extends BaseTest {
    MinaTaskServer server;
    MinaTaskClient client;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server = new MinaTaskServer( taskService );
        Thread thread = new Thread( server );
        thread.start();
        Thread.sleep( 500 );

        client = new MinaTaskClient( "client 1",
                                     new TaskClientHandler() );
        NioSocketConnector connector = new NioSocketConnector();
        SocketAddress address = new InetSocketAddress( "127.0.0.1",
                                                       9123 );
        client.connect( connector,
                        address );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        client.disconnect();
        server.stop();
    }
    
    public void testNewTaskWithNoPotentialOwners() {
        Map  vars = new HashedMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // Task should remain in Created state with no actual owner
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertNotSame(task, task1);
        assertFalse(  task.equals( task1) );
        assertEquals( task1.getTaskData().getStatus(), Status.Created );     
        assertNull( task1.getTaskData().getActualOwner() );        
    }

    public void testNewTaskWithSinglePotentialOwner() {
        Map  vars = new HashedMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertNotSame(task, task1);
        assertFalse(  task.equals( task1) );
        assertEquals( task1.getTaskData().getStatus(), Status.Reserved );     
        assertEquals( users.get( "bobba" ), task1.getTaskData().getActualOwner() );
    }
    
    public void testClaimWithMultiplePotentialOwners() {
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
        client.addTask( task, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );     
        
        client.claim( taskId, users.get( "darth" ).getId() );
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );
        assertEquals( task2.getTaskData().getActualOwner(), users.get( "darth" ) );
    }

}
