package org.drools.task.service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.collections.map.HashedMap;
import org.drools.task.BaseTest;
import org.drools.task.Deadline;
import org.drools.task.Task;
import org.drools.task.service.TaskServiceEscalationTest.MockEscalatedDeadlineHandler.Item;

public class TaskServiceEscalationTest extends BaseTest {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUnescalatedDeadlines() throws Exception {
        Map vars = new HashedMap();
        vars.put( "users",
                  users );
        vars.put( "groups",
                  groups );
        long now = System.currentTimeMillis();
        vars.put( "now",
                  now );

        MockEscalatedDeadlineHandler handler = new MockEscalatedDeadlineHandler();
        taskService.setEscalatedDeadlineHandler( handler );

        //Reader reader;
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "../UnescalatedDeadlines.mvel" ) );
        List<Task> tasks = (List<Task>) eval( reader,
                                              vars );
        for ( Task task : tasks ) {
            taskService.addTask( task );
        }

        Thread.sleep( 4000 );
        
        assertEquals( 3, handler.list.size() );

        Item item0 = handler.list.get( 0 );
        assertEquals( item0.getDeadline().getDate().getTime(),
                      now + 4000 );
        
        Item item1 = handler.list.get( 1 );
        assertEquals( item1.getDeadline().getDate().getTime(),
                      now + 4500 );
        
        Item item2 = handler.list.get( 2 );
        assertEquals( item2.getDeadline().getDate().getTime(),
                      now + 5000 );        
    }
    
    public void testUnescalatedDeadlinesOnStartup() throws Exception {
        Map vars = new HashedMap();
        vars.put( "users",
                  users );
        vars.put( "groups",
                  groups );
        long now = System.currentTimeMillis();
        vars.put( "now",
                  now );



        //Reader reader;
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "../UnescalatedDeadlines.mvel" ) );
        List<Task> tasks = (List<Task>) eval( reader,
                                              vars );
        
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        for ( Task task : tasks ) {
            // for this one we put the task in directly;
            em.persist( task );
        }
        em.getTransaction().commit();

        // now create a new service, to see if it initiates from the DB correctly
        TaskService local = new TaskService(emf);      
        
        MockEscalatedDeadlineHandler handler = new MockEscalatedDeadlineHandler();
        local.setEscalatedDeadlineHandler( handler );
        
        Thread.sleep( 4000 );
        
        assertEquals( 3, handler.list.size() );

        Item item0 = handler.list.get( 0 );
        assertEquals( item0.getDeadline().getDate().getTime(),
                      now + 4000 );
        
        Item item1 = handler.list.get( 1 );
        assertEquals( item1.getDeadline().getDate().getTime(),
                      now + 4500 );
        
        Item item2 = handler.list.get( 2 );
        assertEquals( item2.getDeadline().getDate().getTime(),
                      now + 5000 );            
    }

    public static class MockEscalatedDeadlineHandler
        implements
        EscalatedDeadlineHandler {

        List<Item> list = new ArrayList<Item>();

        public void executeEscalatedDeadline(Task task,
                                             Deadline deadline,
                                             EntityManager em) {
            list.add( new Item( task,
                                deadline,
                                em ) );
        }
        
        public List<Item> getList() {
            return this.list;
        }

        public static class Item {
            Task          task;
            Deadline      deadline;
            EntityManager em;

            public Item(Task task,
                        Deadline deadline,
                        EntityManager em) {
                this.deadline = deadline;
                this.em = em;
                this.task = task;
            }

            public Task getTask() {
                return task;
            }

            public void setTask(Task task) {
                this.task = task;
            }

            public Deadline getDeadline() {
                return deadline;
            }

            public void setDeadline(Deadline deadline) {
                this.deadline = deadline;
            }

            public EntityManager getEntityManager() {
                return em;
            }

            public void setEntityManager(EntityManager em) {
                this.em = em;
            }
        }
    }
}
