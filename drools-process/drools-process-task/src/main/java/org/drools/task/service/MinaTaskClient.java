package org.drools.task.service;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.codec.serialization.ObjectSerializationEncoder;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.task.AccessType;
import org.drools.task.Task;
import org.drools.task.service.TaskClientHandler.TaskSummaryResponseHandler;

public class MinaTaskClient
{
    private final TaskClientHandler handler;

    private final String name;

    private IoSession session;
    
    private int counter;
    
    public static void main(String[] args) throws Exception {
        MinaTaskClient client = new MinaTaskClient("test client", new TaskClientHandler() );
        NioSocketConnector connector = new NioSocketConnector();
        SocketAddress address =  new InetSocketAddress( "127.0.0.1", 9123 );
        client.connect( connector, address );
    }

    public MinaTaskClient(String name, TaskClientHandler handler) {
        if (name == null) {
            throw new IllegalArgumentException("Name can not be null");
        }
        this.name = name;
        this.handler = handler;
    }

    public boolean connect(SocketConnector connector, SocketAddress address) {
        if (session != null && session.isConnected()) {
            throw new IllegalStateException(
                    "Already connected. Disconnect first.");
        }

        connector.setHandler( this.handler );
 
        try {
//            SocketConnectorConfig config = new SocketConnectorConfig();
//            if (useSsl) {
//                SSLContext sslContext = BogusSSLContextFactory
//                        .getInstance(false);
//                SSLFilter sslFilter = new SSLFilter(sslContext);
//                sslFilter.setUseClientMode(true);
//                config.getFilterChain().addLast("sslFilter", sslFilter);
//            }
            
            //connector.setHandler( arg0 );
            
            connector.getFilterChain().addLast(
                                               "codec",
                                               new ProtocolCodecFilter(
                                                       new ObjectSerializationCodecFactory()));

            ConnectFuture future1 = connector.connect( address );
            future1.join();
            if (!future1.isConnected()) {
                return false;
            }
            session = future1.getSession();
            //session.write("LOGIN " + name);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void disconnect() {
        if ( session!= null && session.isConnected() ) {
            session.close();
            session.getCloseFuture().join();
        }
    }
    
    public void addTask(Task task) {
        List args = new ArrayList( 1 );
        args.add( task );
        Command cmd = new Command( counter++, CommandName.AddTaskRequest, args);        
        
        session.write( cmd );
    }
    
    public void addAttachment(long taskId, String name, long userId, Date attachedAt, AccessType accessType, String contentType,  byte[] content ) {
        List args = new ArrayList( 7 );
        args.add( taskId );
        args.add( name );
        args.add( userId );
        args.add( attachedAt );
        args.add( accessType );
        args.add( contentType );
        args.add( content );
        Command cmd = new Command( counter++, CommandName.AddAttachment, args);        
        
        session.write( cmd );        
    }
    
    public void addComment(long taskId, long userId, Date addedAt, String text) {
        List args = new ArrayList( 4 );
        args.add( taskId );
        args.add( name );
        args.add( userId );
        args.add( addedAt );
        args.add( text );
        Command cmd = new Command( counter++, CommandName.AddComment, args);        
        
        session.write( cmd );         
    }
    
    public void getTasksOwned(long userId, String language, TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter++, CommandName.Query_TasksOwned, args);
        handler.addResponseHandler( cmd.getId(), responseHandler );        
        session.write( cmd );
    }
        
    public void getTasksAssignedAsBusinessAdministrator(long userId, String language, TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter++, CommandName.Query_TasksAssignedAsBusinessAdministrator, args);
        handler.addResponseHandler( cmd.getId(), responseHandler );        
        session.write( cmd );
    }    
    
    public void getTasksAssignedAsExcludedOwner(long userId, String language, TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter++, CommandName.Query_TasksAssignedAsExcludedOwner, args);
        handler.addResponseHandler( cmd.getId(), responseHandler );        
        session.write( cmd );
    }     
        
    public void getTasksAssignedAsPotentialOwner(long userId, String language, TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter++, CommandName.Query_TasksAssignedAsPotentialOwner, args);
        handler.addResponseHandler( cmd.getId(), responseHandler );        
        session.write( cmd );
    }      
    
    public void getTasksAssignedAsRecipient(long userId, String language, TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter++, CommandName.Query_TasksAssignedAsRecipient, args);
        handler.addResponseHandler( cmd.getId(), responseHandler );        
        session.write( cmd );
    } 
    
    public void getTasksAssignedAsTaskInitiator(long userId, String language, TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter++, CommandName.Query_TasksAssignedAsTaskInitiator, args);
        handler.addResponseHandler( cmd.getId(), responseHandler );        
        session.write( cmd );
    }    
           
    public void getTasksAssignedAsTaskStakeholder(long userId, String language, TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter++, CommandName.Query_TasksAssignedAsTaskStakeholder, args);
        handler.addResponseHandler( cmd.getId(), responseHandler );        
        session.write( cmd );
    }     

}