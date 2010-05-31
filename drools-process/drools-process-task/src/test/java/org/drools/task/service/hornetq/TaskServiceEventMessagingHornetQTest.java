package org.drools.task.service.hornetq;

import org.drools.SystemEventListenerFactory;
import org.drools.task.MockUserInfo;
import org.drools.task.service.TaskClient;
import org.drools.task.service.TaskServiceEventMessagingBaseTest;

public class TaskServiceEventMessagingHornetQTest extends TaskServiceEventMessagingBaseTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server = new HornetQTaskServer(taskService, 5446);
        Thread thread = new Thread(server);
        thread.start();
        Thread.sleep(500);
        
        client = new TaskClient(new HornetQTaskClientConnector("client 1",
                                new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        client.connect("127.0.0.1", 5446);
        
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(users.get("tony"), "tony@domain.com");
        userInfo.getEmails().put(users.get("steve"), "steve@domain.com");

        userInfo.getLanguages().put(users.get("tony"), "en-UK");
        userInfo.getLanguages().put(users.get("steve"), "en-UK");
        taskService.setUserinfo(userInfo);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        client.disconnect();
        server.stop();
    }    
    
}
