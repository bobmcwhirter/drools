package org.drools.task.service.mina;

import org.drools.SystemEventListenerFactory;
import org.drools.task.MockUserInfo;
import org.drools.task.service.TaskClient;
import org.drools.task.service.TaskServiceEventMessagingBaseTest;
import org.drools.task.service.mina.MinaTaskClientConnector;
import org.drools.task.service.mina.MinaTaskServer;
import org.drools.task.service.mina.MinaTaskClientHandler;

public class TaskServiceEventMessagingMinaTest extends TaskServiceEventMessagingBaseTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server = new MinaTaskServer(taskService);
        Thread thread = new Thread(server);
        thread.start();
        Thread.sleep(500);
        
        client = new TaskClient(new MinaTaskClientConnector("client 1",
                                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        client.connect("127.0.0.1", 9123);
        
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
