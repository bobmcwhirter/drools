package org.drools.task.service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.task.BaseTest;
import org.drools.task.Task;
import org.drools.task.TaskSummary;
import org.drools.task.TestModelPersistence;
import org.drools.task.service.TaskClientHandler.AllOpenTasksForUseResponseHandler;
import org.drools.task.utils.CollectionUtils;

import junit.framework.TestCase;

public class TaskServiceTest extends BaseTest  {
    MinaTaskServer server;
    MinaTaskClient client;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server = new MinaTaskServer( taskService );
        Thread thread = new Thread( server );
        thread.start();     
        
        client = new MinaTaskClient( "client 1", new TaskClientHandler() );
        NioSocketConnector connector = new NioSocketConnector();
        SocketAddress address =  new InetSocketAddress( "127.0.0.1", 9123 );
        client.connect( connector, address );        
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        client.disconnect();
        server.stop();
    }
    
    public void testQuery() throws Exception {
        Map  vars = new HashedMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        
        //Reader reader;
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "../AllOpenTasksData.mvel" ) );
        List<Task> tasks = ( List<Task> ) eval( reader, vars );
        for ( Task task : tasks) {
            client.addTask( task );
        }
        
        // Test UK I18N  
        reader = new InputStreamReader( getClass().getResourceAsStream( "../QueryResultsInEnglish.mvel" ) );
        Map<String, List<TaskSummary>> expected = ( Map<String, List<TaskSummary>> ) eval( reader, vars );
           

        AllOpenTasksForUseResponseHandler responseHandler = new BlockingAllOpenTasksForUseResponseHandler();       
        client.getAllOpenTasksForUser( users.get( "peter" ).getId(), "en-UK", responseHandler );
        List<TaskSummary> actual = responseHandler.getResults();       
        assertEquals( 3, actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "peter" ), actual ) );

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();  
        client.getAllOpenTasksForUser( users.get( "steve" ).getId(), "en-UK", responseHandler );
        actual = responseHandler.getResults();           
        assertEquals( 2, actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "steve" ), actual ) );
        
        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();  
        client.getAllOpenTasksForUser( users.get( "darth" ).getId(), "en-UK", responseHandler );
        actual = responseHandler.getResults();        
        assertEquals( 1, actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "darth" ), actual ) );
        
        // Test DK I18N 
        reader = new InputStreamReader( getClass().getResourceAsStream( "../QueryResultsInGerman.mvel" ) );
        expected = ( Map<String, List<TaskSummary>> ) eval( reader, vars );            
        
        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();  
        client.getAllOpenTasksForUser(  users.get( "peter" ).getId(), "en-DK",  responseHandler   );
        actual = responseHandler.getResults();        
        assertEquals( 3, actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "peter" ), actual ) );

        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();  
        client.getAllOpenTasksForUser(  users.get( "steve" ).getId(), "en-DK",  responseHandler   );
        actual = responseHandler.getResults();            
        assertEquals( 2, actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "steve" ), actual ) );
        
        responseHandler = new BlockingAllOpenTasksForUseResponseHandler();  
        client.getAllOpenTasksForUser(  users.get( "darth" ).getId(), "en-DK",  responseHandler   );
        actual = responseHandler.getResults();        
        assertEquals( 1, actual.size() );
        assertTrue( CollectionUtils.equals( expected.get( "darth" ), actual ) );              
    }
    
    
    public static class BlockingAllOpenTasksForUseResponseHandler implements AllOpenTasksForUseResponseHandler {
        private volatile List<TaskSummary> results;

        public synchronized void execute(List<TaskSummary> results) {
            this.results = results;
            notifyAll();                
        }
        
        public synchronized List<TaskSummary> getResults() {
            if ( results == null ) {                  
                try {
                    wait( 3000 );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
            
            if ( results == null ) {
                throw new RuntimeException("Timeout : unable to retrieve results" );
            }
            
            return results;

        }
        
    };    
    
    
}
