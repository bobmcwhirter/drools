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

public class TaskServiceTaskUpdate extends BaseTest {
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

    public void testAddComment() {
        Map  vars = new HashedMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        
        String str = "(with (new Task()) { priority = 55, taskData = new TaskData(),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        Comment comment = new Comment();
        Date addedAt = new Date( System.currentTimeMillis() ); 
        comment.setAddedAt( addedAt );
        comment.setAddedBy( users.get( "luke" ) );
        comment.setText( "This is my comment1!!!!!" );
        
        BlockingAddCommentResponseHandler addCommentResponseHandler = new BlockingAddCommentResponseHandler();
        client.addComment( taskId, comment,addCommentResponseHandler );      
        assertTrue( addCommentResponseHandler.getCommentId() != comment.getId() );
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertNotSame(task, task1);
        assertFalse(  task.equals( task1) );
       
        List<Comment> comments1 = task1.getTaskData().getComments();
        assertEquals(1, comments1.size() );
        Comment returnedComment = comments1.get( 0 );        
        assertEquals( "This is my comment1!!!!!", returnedComment.getText() );
        assertEquals( addedAt, returnedComment.getAddedAt() );
        assertEquals( users.get( "luke" ), returnedComment.getAddedBy() );
        
        assertEquals( (long)addCommentResponseHandler.getCommentId(), (long) returnedComment.getId() );
        
        task.getTaskData().setComments( comments1 );
        assertEquals(task, task1);       
        
        // test we can have multiple comments
        comment = new Comment();
        addedAt = new Date( System.currentTimeMillis() ); 
        comment.setAddedAt( addedAt );
        comment.setAddedBy( users.get( "tony" ) );
        comment.setText( "This is my comment2!!!!!" );
        
        addCommentResponseHandler = new BlockingAddCommentResponseHandler();
        client.addComment( taskId, comment, addCommentResponseHandler );
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        task1 = getTaskResponseHandler.getTask();     
        List<Comment> comments2 = task1.getTaskData().getComments();
        assertEquals(2, comments2.size() );       
        
        // make two collections the same and compare
        comments1.add( comment );
        assertTrue( CollectionUtils.equals( comments1, comments2 ) );
        
        client.deleteComment( taskId, addCommentResponseHandler.getCommentId() );
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        task1 = getTaskResponseHandler.getTask(); 
        comments2 = task1.getTaskData().getComments();
        assertEquals(1, comments2.size() );   
        
        assertEquals( "This is my comment1!!!!!", comments2.get( 0 ).getText() );
    }
    
    public void testAddRemoveAttachment() throws Exception {
        Map  vars = new HashedMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        
        String str = "(with (new Task()) { priority = 55, taskData = new TaskData(),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        Attachment attachment = new Attachment();
        Date attachedAt = new Date( System.currentTimeMillis() ); 
        attachment.setAttachedAt( attachedAt);
        attachment.setAttachedBy( users.get( "luke" ) );
        attachment.setName( "file1.txt" );
        attachment.setAccessType( AccessType.Inline );
        attachment.setContentType( "txt" );
        
        byte[] bytes = "Ths is my attachment text1".getBytes();
        AttachmentContent content = new AttachmentContent();
        content.setContent( bytes );
        
        BlockingAddAttachmentResponseHandler addAttachmentResponseHandler = new BlockingAddAttachmentResponseHandler();
        client.addAttachment( taskId, attachment, content, addAttachmentResponseHandler);
        assertTrue( addAttachmentResponseHandler.getAttachmentId() != attachment.getId() );
        assertTrue( addAttachmentResponseHandler.getContentId() != attachment.getContentId() );
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertNotSame(task, task1);
        assertFalse(  task.equals( task1) );
       
        List<Attachment> attachments1 = task1.getTaskData().getAttachments();
        assertEquals(1, attachments1.size() );
        Attachment returnedAttachment = attachments1.get( 0 );        
        assertEquals( attachedAt, returnedAttachment.getAttachedAt() );
        assertEquals( users.get( "luke" ), returnedAttachment.getAttachedBy() );
        assertEquals( AccessType.Inline, returnedAttachment.getAccessType() );
        assertEquals( "txt", returnedAttachment.getContentType() );
        assertEquals( "file1.txt", returnedAttachment.getName() );        
        assertEquals( bytes.length, returnedAttachment.getSize() );
        
        assertEquals( (long) addAttachmentResponseHandler.getAttachmentId(), (long) returnedAttachment.getId() );
        assertEquals( (long) addAttachmentResponseHandler.getContentId(),  (long) returnedAttachment.getContentId() );        
        
        task.getTaskData().setAttachments( attachments1 );
        assertEquals(task, task1);            
        
        BlockingGetAttachmentContentResponseHandler  getAttachmentContentResponseHandler = new BlockingGetAttachmentContentResponseHandler();
        client.getAttachmentContent( returnedAttachment.getContentId(), getAttachmentContentResponseHandler );
        AttachmentContent attachmentContent = getAttachmentContentResponseHandler.getAttachmentContent();
        assertEquals( "Ths is my attachment text1", new String( attachmentContent.getContent() ) );
        
        // test we can have multiple attachments
        
        attachment = new Attachment();
        attachedAt = new Date( System.currentTimeMillis() ); 
        attachment.setAttachedAt( attachedAt);
        attachment.setAttachedBy( users.get( "tony" ) );
        attachment.setName( "file2.txt" );
        attachment.setAccessType( AccessType.Inline );
        attachment.setContentType( "txt" );
        
        bytes = "Ths is my attachment text2".getBytes();
        content = new AttachmentContent();
        content.setContent( bytes );
        
        addAttachmentResponseHandler = new BlockingAddAttachmentResponseHandler();
        client.addAttachment( taskId, attachment, content, addAttachmentResponseHandler);   
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        task1 = getTaskResponseHandler.getTask();
        assertNotSame(task, task1);
        assertFalse(  task.equals( task1) );
       
        List<Attachment> attachments2 = task1.getTaskData().getAttachments();
        assertEquals(2, attachments2.size() );
        
        getAttachmentContentResponseHandler = new BlockingGetAttachmentContentResponseHandler();
        client.getAttachmentContent( addAttachmentResponseHandler.getContentId(), getAttachmentContentResponseHandler );
        attachmentContent = getAttachmentContentResponseHandler.getAttachmentContent();
        assertEquals( "Ths is my attachment text2", new String( attachmentContent.getContent() ) );        
        
        // make two collections the same and compare
        attachment.setSize( 26 );
        attachment.setContentId( addAttachmentResponseHandler.getContentId() );
        attachments1.add( attachment );
        assertTrue( CollectionUtils.equals( attachments2, attachments1 ) );      
        
        client.deleteAttachment( taskId, addAttachmentResponseHandler.getAttachmentId(), addAttachmentResponseHandler.getContentId() );
        
        Thread.sleep( 3000 );
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        task1 = getTaskResponseHandler.getTask();
        attachments2 = task1.getTaskData().getAttachments();
        assertEquals(1, attachments2.size() );        
        
        assertEquals( "file1.txt", attachments2.get( 0 ).getName());
    }    

    public static class BlockingGetTaskResponseHandler implements GetTaskResponseHandler {
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
    
    public static class BlockingAddTaskResponseHandler implements AddTaskResponseHandler {
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
    
    public static class BlockingAddCommentResponseHandler implements AddCommentResponseHandler {
        private volatile long commentId;
        private volatile boolean wait = true;

        public synchronized void execute(long commentId) {
            this.commentId = commentId;
            wait = false;
            notifyAll();                
        }
        
        public synchronized long getCommentId() {
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
            
            return commentId;
        }       
    }       
    
    public static class BlockingAddAttachmentResponseHandler implements AddAttachmentResponseHandler {
        private volatile long attachmentId ;
        private volatile long contentId;
        private volatile boolean wait = true;

        public synchronized void execute(long attachmentId, long contentId) {
            this.attachmentId = attachmentId;
            this.contentId = contentId;
            wait = false;
            notifyAll();                
        }
        
        public synchronized long getAttachmentId() {
            if ( wait ) {                  
                try {
                    wait( 3000 );
                } catch ( InterruptedException e ) {
                    // swallow as this is just a notifiation
                }
            }
            
            if ( wait ) {
                throw new RuntimeException("Timeout : unable to retrieve Attachment Id" );
            }
            
            return attachmentId;
        }       
        
        public synchronized long getContentId() {
            if ( wait ) {                  
                try {
                    wait( 3000 );
                } catch ( InterruptedException e ) {
                    // swallow as this is just a notifiation
                }
            }
            
            if ( wait ) {
                throw new RuntimeException("Timeout : unable to retrieve Attachment Content Id" );
            }
            
            return contentId;
        }
    }        
    
    public static class BlockingGetAttachmentContentResponseHandler implements GetAttachmentContentResponseHandler {
        private volatile AttachmentContent attachmentContent;

        public synchronized void execute(AttachmentContent attachmentContent) {
            this.attachmentContent = attachmentContent;
            notifyAll();                
        }
        
        public synchronized AttachmentContent getAttachmentContent() {
            if ( attachmentContent == null ) {                  
                try {
                    wait( 3000 );
                } catch ( InterruptedException e ) {
                    // swallow as this is just a notifiation
                }
            }
            
            if ( attachmentContent == null ) {
                throw new RuntimeException("Timeout : unable to retrieve Attachment Content" );
            }
            
            return attachmentContent;
        }       
    } 
}
