package org.drools.task.service;

import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.commons.collections.map.HashedMap;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.task.BaseTest;
import org.drools.task.MockUserInfo;
import org.drools.task.Task;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public class IcalTest extends BaseTest {
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
    
    public void test1() throws Exception {
        Map  vars = new HashedMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, createdBy = users['tony'], activationTime = now}), ";        
        str += "peopleAssignments = (with ( new PeopleAssignments() ) {potentialOwners = [users['steve' ], users['tony' ]]}), ";        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')],";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my task subject')],";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my task description')],";
        str += "deadlines = (with (new Deadlines() ) {";
        str += "    startDeadlines = [ ";
        str += "       (with (new Deadline()) {";
        str += "           date = now";
        str += "       } ) ],";
        str += "    endDeadlines = ["; 
        str += "        (with (new Deadline()) {";
        str += "             date = new Date( now.time + ( 1000 * 60 * * 60 * 24 ) )"; // set to tomorrow
        str += "        } ) ]";
        str += "} ) })";
        
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put( users.get("tony"), "tony@domain.com" );
        userInfo.getEmails().put( users.get("steve"), "steve@domain.com" );
        
        userInfo.getLanguages().put(  users.get("tony"), "en-UK" );
        userInfo.getLanguages().put(  users.get("steve"), "en-UK" );
        taskService.setUserinfo( userInfo );         
      
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();        
        
        client.claim( taskId, users.get( "steve" ).getId() );
        
        Thread.sleep( 3000 );
        
        assertEquals(2, wiser.getMessages().size() );  
        
        List<String> list = new ArrayList<String>(2);
        list.add( wiser.getMessages().get( 0 ).getEnvelopeReceiver() );
        list.add( wiser.getMessages().get( 1 ).getEnvelopeReceiver() );
        
        assertTrue( list.contains("steve@domain.com"));
        assertTrue( list.contains("steve@domain.com"));     
        MimeMessage msg = (( WiserMessage  ) wiser.getMessages().get( 0 )).getMimeMessage();
        
//        assertEquals( "My Body", msg.getContent() );
//        assertEquals( "My Subject", msg.getSubject() );
        assertEquals( "tony@domain.com", ((InternetAddress)msg.getFrom()[0]).getAddress() );
        assertEquals( "tony@domain.com", ((InternetAddress)msg.getReplyTo()[0]).getAddress() );
        assertEquals( "steve@domain.com", ((InternetAddress)msg.getRecipients( RecipientType.TO )[0]).getAddress() );        
    }
}
