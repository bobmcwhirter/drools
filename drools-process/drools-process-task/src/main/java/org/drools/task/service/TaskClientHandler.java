package org.drools.task.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.drools.task.TaskSummary;

public class TaskClientHandler extends IoHandlerAdapter
{    
    private MinaTaskClient client;        
    private Map<Integer, ResponseHandler> responseHandlers;
    
    public TaskClientHandler() {
        responseHandlers = new HashMap<Integer, ResponseHandler>();
    }
    
    public void addResponseHandler(int id, ResponseHandler responseHandler) {
        responseHandlers.put( id, responseHandler );
    }
    
    public MinaTaskClient getClient() {
        return client;
    }

    public void setClient(MinaTaskClient client) {
        this.client = client;
    }

    @Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
    {
        cause.printStackTrace();
    }
    
    @Override
    public void messageReceived(IoSession session,
                                Object message) throws Exception {
        Command cmd = (Command) message;
        switch ( cmd.getName() ) {
            case AllOpenTasksForUserResponse : {
                List<TaskSummary> results = ( List<TaskSummary> ) cmd.getArguments().get( 0 );
                AllOpenTasksForUseResponseHandler responseHandler = ( AllOpenTasksForUseResponseHandler ) responseHandlers.remove( cmd.getId() );
                if ( responseHandler != null ) {
                    responseHandler.execute( results );
                }               
            }
        }
    }

    @Override
    public void messageSent(IoSession session, Object message)
    {
        String str = message.toString();
        if( str.trim().equalsIgnoreCase("quit") ) {
            session.close();
            return;
        }

        Date date = new Date();
        //session.write( date.toString() );
        System.out.println("Message written : " + str + " : " + date);
    }

    @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception
    {
        System.out.println( "IDLE " + session.getIdleCount( status ));
    }
    
    
    public static interface ResponseHandler {
        
    }
    
    public static interface AllOpenTasksForUseResponseHandler extends ResponseHandler {
        public void execute(List<TaskSummary> results);
        
        public List<TaskSummary> getResults();
    }
}