package org.drools.process.workitem.jabber;



import junit.framework.TestCase;

import org.drools.process.instance.impl.DefaultWorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.process.WorkItemManager;

// @Author: salaboy@gmail.com
public class JabberWorkItemHandlerTest extends TestCase {
    
  
    public void testSendJabberMessage() throws Exception {
        JabberWorkItemHandler handler = new JabberWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        //The contact in To must be added as a contact in the chat of gtalk
        workItem.setParameter( "To", "drools.demo@gmail.com" );
        workItem.setParameter( "Text", "Hello from Ruleflow WorkItem" );
        //workItem.setParameter( "Server", "talk.google.com" );
        //workItem.setParameter( "Port", "5222" );
        workItem.setParameter( "Service", "gmail.com" );
        workItem.setParameter( "User", "drools.demo" );
        workItem.setParameter( "Password", "pa$$word" );
        
        WorkItemManager manager = new DefaultWorkItemManager(null);
        handler.executeWorkItem( workItem, manager );

        //In a real case i must register the WorkItemHandler:
        // workingMemory.getWorkItemManager()
        //.registerWorkItemHandler("Notification", new NotificationWorkItemHandler());

        
    }    
}
