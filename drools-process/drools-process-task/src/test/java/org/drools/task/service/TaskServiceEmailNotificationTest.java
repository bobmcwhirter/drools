package org.drools.task.service;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.commons.collections.map.HashedMap;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.DefaultWorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.process.workitem.email.EmailWorkItemHandler;
import org.drools.task.BaseTest;
import org.drools.task.Content;
import org.drools.task.EmailNotification;
import org.drools.task.MockUserInfo;
import org.drools.task.Task;
import org.drools.task.User;
import org.drools.task.service.DefaultEscalatedDeadlineHandler;
import org.mvel.MVEL;
import org.mvel.compiler.ExpressionCompiler;
import org.mvel.templates.CompiledTemplate;
import org.mvel.templates.TemplateCompiler;
import org.mvel.templates.TemplateRuntime;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public class TaskServiceEmailNotificationTest extends BaseTest {
    MinaTaskServer server;
    MinaTaskClient client;
    
    Wiser wiser;

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
        
        wiser = new Wiser();
        wiser.start();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        client.disconnect();
        server.stop();
        
        wiser.stop();
    }
    
    public void testDelayedEmailNotificationOnDeadline() throws Exception {
        Map  vars = new HashedMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );
        vars.put( "now", new Date() ); 
        
        DefaultEscalatedDeadlineHandler notificationHandler = new DefaultEscalatedDeadlineHandler();
        notificationHandler.getHandler().setConnection( "localhost", "25", null, null );
        WorkItemManager manager = new DefaultWorkItemManager( null );
        notificationHandler.setManager( manager );
        
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put( users.get("tony"), "tony@domain.com" );
        userInfo.getEmails().put( users.get("darth"), "darth@domain.com" );
        
        userInfo.getLanguages().put(  users.get("tony"), "en-UK" );
        userInfo.getLanguages().put(  users.get("darth"), "en-UK" );
        notificationHandler.setUserInfo( userInfo );    
        
        taskService.setEscalatedDeadlineHandler( notificationHandler );
        
        String string = toString( new InputStreamReader( getClass().getResourceAsStream( "Notification1.mvel" ) ) );
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( string ), vars );
        client.addTask( task, addTaskResponseHandler );
        long taskId = addTaskResponseHandler.getTaskId();    
                                        
        Content content = new Content();
        content.setContent( "['subject' : 'My Subject', 'body' : 'My Body']".getBytes() );
        BlockingSetContentResponseHandler setContentResponseHandler  = new BlockingSetContentResponseHandler();
        client.setDocumentContent( taskId, content, setContentResponseHandler );
        long contentId = setContentResponseHandler.getContentId();
        
        // emails should not be set yet
        assertEquals(0, wiser.getMessages().size() );             
        Thread.sleep( 1000 );
        
        // nor yet
        assertEquals(0, wiser.getMessages().size() );     
        
        Thread.sleep( 6000 );
        
        // 1 email with two recipients should now exist
        assertEquals(2, wiser.getMessages().size() );        
        
        List<String> list = new ArrayList<String>(2);
        list.add( wiser.getMessages().get( 0 ).getEnvelopeReceiver() );
        list.add( wiser.getMessages().get( 1 ).getEnvelopeReceiver() );
        
        assertTrue( list.contains("tony@domain.com"));
        assertTrue( list.contains("darth@domain.com"));
        
        
        MimeMessage msg = (( WiserMessage  ) wiser.getMessages().get( 0 )).getMimeMessage();
        assertEquals( "My Body", msg.getContent() );
        assertEquals( "My Subject", msg.getSubject() );
        assertEquals( "from@domain.com", ((InternetAddress)msg.getFrom()[0]).getAddress() );
        assertEquals( "replyTo@domain.com", ((InternetAddress)msg.getReplyTo()[0]).getAddress() );
        assertEquals( "tony@domain.com", ((InternetAddress)msg.getRecipients( RecipientType.TO )[0]).getAddress() );
        assertEquals( "darth@domain.com", ((InternetAddress)msg.getRecipients( RecipientType.TO )[1]).getAddress() );        
    }
}
