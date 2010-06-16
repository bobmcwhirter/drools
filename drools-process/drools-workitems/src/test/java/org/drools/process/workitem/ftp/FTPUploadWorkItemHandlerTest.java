package org.drools.process.workitem.ftp;



import junit.framework.TestCase;



public class FTPUploadWorkItemHandlerTest extends TestCase {
//    FtpServerFactory serverFactory;
//    FtpServer server;
//    String ftpHost;
//    String ftpPort;
//    
//    @Override
//    protected void setUp() throws Exception {
//        ChainedProperties props = new ChainedProperties( "ftp.conf" );
//        ftpHost = props.getProperty( "host", "localhost" );
//        ftpPort = props.getProperty( "port", "2121" );
//        serverFactory = new FtpServerFactory();
//
//
//
//        //Set properties to the server factory
//        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
//        //Users information
//        userManagerFactory.setFile(new File("myusers.properties"));
//        
//        //We are using clear text passwords
//        userManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());
//        UserManager userManager = userManagerFactory.createUserManager();
//        serverFactory.setUserManager(userManager);
//
//        ListenerFactory factory = new ListenerFactory();
//
//        // set the port of the listener
//        factory.setPort(Integer.valueOf(ftpPort));
//        factory.setServerAddress(ftpHost);
//        // replace the default listener
//        serverFactory.addListener("default", factory.createListener());
//
//
//
//        server = serverFactory.createServer();
//        
//        // start the server
//        server.start();
//        
//        
//    }
    
//    @Override
//    protected void tearDown() throws Exception {
//        server.stop();
//    }
    
    public void testEmpty() {
    	
    }
    
//    public void testUploadFile() throws Exception {
//        FTPUploadWorkItemHandler handler = new FTPUploadWorkItemHandler();
//        handler.setConnection( ftpHost, ftpPort, null, null );
//        
//        WorkItemImpl workItem = new WorkItemImpl();
//        //File to upload
//        workItem.setParameter( "FilePath","drools.ftp.conf" );
//        //User and password. look in the myusers.properties to change this values
//        workItem.setParameter( "User", "anon" );
//        workItem.setParameter( "Password", "anon" );
//
//        WorkItemManager manager = new DefaultWorkItemManager(null);
//        handler.executeWorkItem( workItem, manager );
//        assertEquals( true, handler.isResult() );
//        
//       
//    }
    
    
}
