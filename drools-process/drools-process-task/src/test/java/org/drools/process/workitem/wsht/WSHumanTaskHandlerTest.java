package org.drools.process.workitem.wsht;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemHandler;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.task.BaseTest;
import org.drools.task.Status;
import org.drools.task.query.TaskSummary;
import org.drools.task.service.BlockingTaskOperationResponseHandler;
import org.drools.task.service.BlockingTaskSummaryResponseHandler;
import org.drools.task.service.MinaTaskClient;
import org.drools.task.service.MinaTaskServer;
import org.drools.task.service.TaskClientHandler;

public class WSHumanTaskHandlerTest extends BaseTest {
	
    MinaTaskServer server;
    MinaTaskClient client;
    WSHumanTaskHandler handler;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server = new MinaTaskServer( taskService );
        Thread thread = new Thread( server );
        thread.start();
        Thread.sleep( 500 );
        client = new MinaTaskClient("client 1", new TaskClientHandler());
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
		operationResponseHandler.waitTillDone(5000);
		System.out.println("Started task " + task.getId());
		
		System.out.println("Completing task " + task.getId());
		operationResponseHandler = new BlockingTaskOperationResponseHandler();
		client.complete(task.getId(), "Darth Vader", operationResponseHandler);
		operationResponseHandler.waitTillDone(5000);
		System.out.println("Completed task " + task.getId());
		
		assertTrue(manager.isCompleted());
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
		operationResponseHandler.waitTillDone(5000);
		System.out.println("Claimed task " + task.getId());
		
		System.out.println("Starting task " + task.getId());
		operationResponseHandler = new BlockingTaskOperationResponseHandler();
		client.start(task.getId(), "Darth Vader", operationResponseHandler);
		operationResponseHandler.waitTillDone(5000);
		System.out.println("Started task " + task.getId());
		
		System.out.println("Completing task " + task.getId());
		operationResponseHandler = new BlockingTaskOperationResponseHandler();
		client.complete(task.getId(), "Darth Vader", operationResponseHandler);
		operationResponseHandler.waitTillDone(5000);
		System.out.println("Completed task " + task.getId());
		
		assertTrue(manager.isCompleted());
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
		operationResponseHandler.waitTillDone(5000);
		System.out.println("Started task " + task.getId());
		
		System.out.println("Failing task " + task.getId());
		operationResponseHandler = new BlockingTaskOperationResponseHandler();
		client.fail(task.getId(), "Darth Vader", operationResponseHandler);
		operationResponseHandler.waitTillDone(5000);
		System.out.println("Failed task " + task.getId());
		
		assertTrue(manager.isAborted());
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
		operationResponseHandler.waitTillDone(5000);
		System.out.println("Skipped task " + task.getId());
		
		assertTrue(manager.isAborted());
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
	
	private class TestWorkItemManager implements WorkItemManager  {
		
		private boolean completed;
		private boolean aborted;
		
		public void abortWorkItem(long id) {
			aborted = true;
		}
		
		public boolean isAborted() {
			return aborted;
		}

		public void completeWorkItem(long id, Map<String, Object> results) {
			completed = true;
		}
		
		public boolean isCompleted() {
			return completed;
		}

		public WorkItem getWorkItem(long id) {
			return null;
		}

		public Set<WorkItem> getWorkItems() {
			return null;
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
