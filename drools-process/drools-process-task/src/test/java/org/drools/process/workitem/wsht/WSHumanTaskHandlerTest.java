package org.drools.process.workitem.wsht;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.SystemEventListenerFactory;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.task.AccessType;
import org.drools.task.BaseTest;
import org.drools.task.Status;
import org.drools.task.Task;
import org.drools.task.query.TaskSummary;
import org.drools.task.service.*;
import org.drools.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.drools.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.drools.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.drools.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WSHumanTaskHandlerTest extends BaseTest {
    private static final int DEFAULT_WAIT_TIME = 5000;
    private static final int MANAGER_COMPLETION_WAIT_TIME = DEFAULT_WAIT_TIME;
    private static final int MANAGER_ABORT_WAIT_TIME = DEFAULT_WAIT_TIME;

    MinaTaskServer server;
    MinaTaskClient client;
    WSHumanTaskHandler handler;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server = new MinaTaskServer(taskService);
        Thread thread = new Thread(server);
        thread.start();
        Thread.sleep(500);
        client = new MinaTaskClient("client 1", new TaskClientHandler(SystemEventListenerFactory.getSystemEventListener()));
        NioSocketConnector connector = new NioSocketConnector();
        SocketAddress address = new InetSocketAddress("127.0.0.1", 9123);
        client.connect(connector, address);
        handler = new WSHumanTaskHandler();
    }

    protected void tearDown() throws Exception {
        handler.dispose();
        client.disconnect();
        server.stop();
        super.tearDown();
    }

    public void testTask() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        System.out.println("Completing task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.complete(task.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(15000);
        System.out.println("Completed task " + task.getId());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    public void testTaskMultipleActors() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader, Dalai Lama");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Ready, task.getStatus());

        System.out.println("Claiming task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.claim(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Claimed task " + task.getId());

        System.out.println("Starting task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        System.out.println("Completing task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.complete(task.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed task " + task.getId());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    public void testTaskGroupActors() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner(null, "Crusaders", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Ready, taskSummary.getStatus());

        System.out.println("Claiming task " + taskSummary.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.claim(taskSummary.getId(), "Darth Vader", operationResponseHandler);
        PermissionDeniedException denied = null;
        try {
            operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        } catch (PermissionDeniedException e) {
            denied = e;
        }

        assertNotNull("Should get permissed denied exception", denied);
        System.out.println("Claimed task " + taskSummary.getId());

        //Check if the parent task is InProgress
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskSummary.getId(), getTaskResponseHandler);
        Task task = getTaskResponseHandler.getTask();
        assertEquals(Status.Ready, task.getTaskData().getStatus());
    }

    public void testTaskSingleAndGroupActors() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task One");
        workItem.setParameter("TaskName", "TaskNameOne");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        workItem = new WorkItemImpl();
        workItem.setName("Human Task Two");
        workItem.setParameter("TaskName", "TaskNameTwo");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("Darth Vader", "Crusaders", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(2, tasks.size());
    }

    public void testTaskFail() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        System.out.println("Failing task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.fail(task.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Failed task " + task.getId());

        assertTrue(manager.waitTillAborted(MANAGER_ABORT_WAIT_TIME));
    }

    public void testTaskSkip() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        System.out.println("Skipping task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.skip(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Skipped task " + task.getId());

        assertTrue(manager.waitTillAborted(MANAGER_ABORT_WAIT_TIME));
    }

    public void testTaskAbortSkippable() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        handler.abortWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(0, tasks.size());
    }

    public void testTaskAbortNotSkippable() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("Skippable", "false");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());

        handler.abortWorkItem(workItem, manager);

        Thread.sleep(500);

        responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
    }

    public void testTaskData() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("Content", "This is the content");
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Reserved, taskSummary.getStatus());
        assertEquals("Darth Vader", taskSummary.getActualOwner().getId());

        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskSummary.getId(), getTaskResponseHandler);
        Task task = getTaskResponseHandler.getTask();
        assertEquals(AccessType.Inline, task.getTaskData().getDocumentAccessType());
        long contentId = task.getTaskData().getDocumentContentId();
        assertTrue(contentId != -1);
        BlockingGetContentResponseHandler getContentResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(contentId, getContentResponseHandler);
        ByteArrayInputStream bis = new ByteArrayInputStream(getContentResponseHandler.getContent().getContent());
        ObjectInputStream in = new ObjectInputStream(bis);
        Object data = in.readObject();
        in.close();
        assertEquals("This is the content", data);

        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        System.out.println("Completing task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        ContentData result = new ContentData();
        result.setAccessType(AccessType.Inline);
        result.setType("java.lang.String");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject("This is the result");
        out.close();
        result.setContent(bos.toByteArray());
        client.complete(task.getId(), "Darth Vader", result, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed task " + task.getId());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
        Map<String, Object> results = manager.getResults();
        assertNotNull(results);
        assertEquals("Darth Vader", results.get("ActorId"));
        assertEquals("This is the result", results.get("Result"));
    }

    public void testOnAllSubTasksEndParentEndStrategy() throws Exception {

        TestWorkItemManager manager = new TestWorkItemManager();
        //Create the parent task
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskNameParent");
        workItem.setParameter("Comment", "CommentParent");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        //Set the subtask policy
        workItem.setParameter("SubTaskStrategies", "OnAllSubTasksEndParentEnd");
        handler.executeWorkItem(workItem, manager);


        Thread.sleep(500);

        //Test if the task is succesfully created
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskNameParent", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("CommentParent", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        //Create the child task
        workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskNameChild1");
        workItem.setParameter("Comment", "CommentChild1");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        //Create the child task2
        workItem = new WorkItemImpl();
        workItem.setName("Human Task2");
        workItem.setParameter("TaskName", "TaskNameChild2");
        workItem.setParameter("Comment", "CommentChild2");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        //Start the parent task
        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        //Check if the parent task is InProgress
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(task.getId(), getTaskResponseHandler);
        Task parentTask = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, parentTask.getTaskData().getStatus());
        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());

        //Get all the subtask created for the parent task based on the potential owner
        responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getSubTasksAssignedAsPotentialOwner(parentTask.getId(), "Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> subTasks = responseHandler.getResults();
        assertEquals(2, subTasks.size());
        TaskSummary subTaskSummary1 = subTasks.get(0);
        TaskSummary subTaskSummary2 = subTasks.get(1);
        assertNotNull(subTaskSummary1);
        assertNotNull(subTaskSummary2);

        //Starting the sub task 1
        System.out.println("Starting sub task " + subTaskSummary1.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(subTaskSummary1.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started sub task " + subTaskSummary1.getId());

        //Starting the sub task 2
        System.out.println("Starting sub task " + subTaskSummary2.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(subTaskSummary2.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started sub task " + subTaskSummary2.getId());

        //Check if the child task 1 is InProgress
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(subTaskSummary1.getId(), getTaskResponseHandler);
        Task subTask1 = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is InProgress
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(subTaskSummary2.getId(), getTaskResponseHandler);
        Task subTask2 = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        // Complete the child task 1
        System.out.println("Completing sub task " + subTask1.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.complete(subTask1.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed sub task " + subTask1.getId());

        // Complete the child task 2
        System.out.println("Completing sub task " + subTask2.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.complete(subTask2.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed sub task " + subTask2.getId());

        //Check if the child task 1 is Completed

        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(subTask1.getId(), getTaskResponseHandler);
        subTask1 = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is Completed

        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(subTask2.getId(), getTaskResponseHandler);
        subTask2 = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        // Check is the parent task is Complete
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(parentTask.getId(), getTaskResponseHandler);
        parentTask = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, parentTask.getTaskData().getStatus());
        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    public void testOnParentAbortAllSubTasksEndStrategy() throws Exception {

        TestWorkItemManager manager = new TestWorkItemManager();
        //Create the parent task
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskNameParent");
        workItem.setParameter("Comment", "CommentParent");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        //Set the subtask policy
        workItem.setParameter("SubTaskStrategies", "OnParentAbortAllSubTasksEnd");
        handler.executeWorkItem(workItem, manager);


        Thread.sleep(500);

        //Test if the task is succesfully created
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskNameParent", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("CommentParent", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        //Create the child task
        workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskNameChild1");
        workItem.setParameter("Comment", "CommentChild1");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        //Create the child task2
        workItem = new WorkItemImpl();
        workItem.setName("Human Task2");
        workItem.setParameter("TaskName", "TaskNameChild2");
        workItem.setParameter("Comment", "CommentChild2");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        //Start the parent task
        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        //Check if the parent task is InProgress
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(task.getId(), getTaskResponseHandler);
        Task parentTask = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, parentTask.getTaskData().getStatus());
        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());

        //Get all the subtask created for the parent task based on the potential owner
        responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getSubTasksAssignedAsPotentialOwner(parentTask.getId(), "Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> subTasks = responseHandler.getResults();
        assertEquals(2, subTasks.size());
        TaskSummary subTaskSummary1 = subTasks.get(0);
        TaskSummary subTaskSummary2 = subTasks.get(1);
        assertNotNull(subTaskSummary1);
        assertNotNull(subTaskSummary2);

        //Starting the sub task 1
        System.out.println("Starting sub task " + subTaskSummary1.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(subTaskSummary1.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started sub task " + subTaskSummary1.getId());

        //Starting the sub task 2
        System.out.println("Starting sub task " + subTaskSummary2.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(subTaskSummary2.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started sub task " + subTaskSummary2.getId());

        //Check if the child task 1 is InProgress
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(subTaskSummary1.getId(), getTaskResponseHandler);
        Task subTask1 = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is InProgress
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(subTaskSummary2.getId(), getTaskResponseHandler);
        Task subTask2 = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        // Complete the parent task
        System.out.println("Completing parent task " + parentTask.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.skip(parentTask.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed parent task " + parentTask.getId());

        //Check if the child task 1 is Completed
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(subTaskSummary1.getId(), getTaskResponseHandler);
        subTask1 = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is Completed
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(subTaskSummary2.getId(), getTaskResponseHandler);
        subTask2 = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    private class TestWorkItemManager implements WorkItemManager {

        private volatile boolean completed;
        private volatile boolean aborted;
        private volatile Map<String, Object> results;

        public synchronized boolean waitTillCompleted(long time) {
            if (!isCompleted()) {
                try {
                    wait(time);
                } catch (InterruptedException e) {
                    // swallow and return state of completed
                }
            }

            return isCompleted();
        }

        public synchronized boolean waitTillAborted(long time) {
            if (!isAborted()) {
                try {
                    wait(time);
                } catch (InterruptedException e) {
                    // swallow and return state of aborted
                }
            }

            return isAborted();
        }

        public void abortWorkItem(long id) {
            setAborted(true);
        }

        public synchronized boolean isAborted() {
            return aborted;
        }

        private synchronized void setAborted(boolean aborted) {
            this.aborted = aborted;
            notifyAll();
        }

        public void completeWorkItem(long id, Map<String, Object> results) {
            this.results = results;
            setCompleted(true);
        }

        private synchronized void setCompleted(boolean completed) {
            this.completed = completed;
            notifyAll();
        }

        public synchronized boolean isCompleted() {
            return completed;
        }

        public WorkItem getWorkItem(long id) {
            return null;
        }

        public Set<WorkItem> getWorkItems() {
            return null;
        }

        public Map<String, Object> getResults() {
            return results;
        }

        public void internalAbortWorkItem(long id) {
        }

        public void internalAddWorkItem(WorkItem workItem) {
        }

        public void internalExecuteWorkItem(WorkItem workItem) {
        }

        public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
        }

    }
}
