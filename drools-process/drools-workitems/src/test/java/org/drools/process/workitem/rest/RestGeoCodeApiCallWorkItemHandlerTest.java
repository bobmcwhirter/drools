package org.drools.process.workitem.rest;



import org.drools.process.workitem.rest.*;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.DefaultWorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;




public class RestGeoCodeApiCallWorkItemHandlerTest extends TestCase {

    

    public void testEmpty(){

    }
    public void FIXMEtestYahooGeoCode() throws Exception {
        RestGeoCodeApiCallWorkItemHandler handler = new RestGeoCodeApiCallWorkItemHandler();
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put( "URL","http://local.yahooapis.com/" );
        queryParams.put("Service", "MapsService/V1/");
        queryParams.put("Method", "geocode?");
        queryParams.put("appid","TIpNDenV34Fwcw_x32k1eX6AlQzq4wajFEFvG501Pwc6w9jKEfy2vGnkIn.r5qSQqVvyhPPaTFo-");
        //Real parameters
        queryParams.put("street", "701+First+Ave");
        queryParams.put("city", "Sunnyvale");
        queryParams.put("state", "CA");

        
        WorkItemImpl workItem = new WorkItemImpl();
      
       
        workItem.setParameters(queryParams);
        

        WorkItemManager manager = new DefaultWorkItemManager(null);
        handler.executeWorkItem( workItem, manager );
        assertEquals(HttpURLConnection.HTTP_OK, handler.getHttpResponseCode());
        assertEquals( 1, handler.getResults().size() );
        assertEquals("US", ((ResultGeoCodeApi)handler.getResults().get(0)).getCountry());
       
    }
    
    
}
