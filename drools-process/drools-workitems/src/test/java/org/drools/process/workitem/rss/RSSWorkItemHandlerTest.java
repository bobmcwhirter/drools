package org.drools.process.workitem.rss;



import junit.framework.TestCase;

import org.drools.process.instance.impl.DefaultWorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.process.WorkItemManager;


public class RSSWorkItemHandlerTest extends TestCase {
    
  
    public void testReadRSSFeed() throws Exception {
        RSSWorkItemHandler handler = new RSSWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "URL", "http://feeds.athico.com/DroolsRSS;http://salaboy.wordpress.com/feed/" );

        
        WorkItemManager manager = new DefaultWorkItemManager(null);
        handler.executeWorkItem( workItem, manager );

        assertEquals(2, handler.getFeeds().size());

        //En el caso real deberia registrar el workitem handler en el workitemmanager
        // workingMemory.getWorkItemManager()
        //.registerWorkItemHandler("Notification", new NotificationWorkItemHandler());

        
    }    
}
