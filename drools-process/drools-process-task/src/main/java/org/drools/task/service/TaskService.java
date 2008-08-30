package org.drools.task.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.drools.task.Deadline;
import org.drools.task.Group;
import org.drools.task.Task;
import org.drools.task.User;
import org.drools.task.query.TaskSummary;

public class TaskService {
    EntityManagerFactory             emf;
        
    EntityManager                    em;

    Query                            openOwnedTasksForUser;
    Query                            unescalatedDeadlines;

    ScheduledThreadPoolExecutor      scheduler;

    private EscalatedDeadlineHandler escalatedDeadlineHandler;

    public TaskService(EntityManagerFactory emf) {
        this.emf = emf;
        em = emf.createEntityManager();
        
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "OpenOwnedTasksForUser.txt" ) );
        try {
            openOwnedTasksForUser = em.createQuery( toString( reader ) );
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to inialize TaskService, could not load query 'AllOpenTasks'",
                                        e );
        }
        scheduler = new ScheduledThreadPoolExecutor( 3 );

        try {
            reader = new InputStreamReader( getClass().getResourceAsStream( "UnescalatedDeadlines.txt" ) );
            unescalatedDeadlines = em.createQuery( toString( reader ) );
            long now = System.currentTimeMillis();
            for ( Object object : unescalatedDeadlines.getResultList() ) {
                Object[] objects = (Object[]) object;
                long taskId = ((Long) objects[0]).longValue();
                long deadlineId = ((Long) objects[1]).longValue();
                Date date = (Date) objects[2];
                scheduler.schedule( new ScheduledTaskDeadline( taskId,
                                                               deadlineId,
                                                               this ),
                                    date.getTime() - now,
                                    TimeUnit.MILLISECONDS );
            }
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to inialize TaskService, could not load and schedule oustanding deadlines",
                                        e );
        }
    }
    
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
    
    public EntityManager getEntityManager() {
        return em;
    }    

    public void setEscalatedDeadlineHandler(EscalatedDeadlineHandler escalatedDeadlineHandler) {
        this.escalatedDeadlineHandler = escalatedDeadlineHandler;
    }

    public void addUser(User user) {
        em.getTransaction().begin();
        em.persist( user );
        em.getTransaction().commit();
    }

    public void addGroup(Group group) {
        em.getTransaction().begin();
        em.persist( group );
        em.getTransaction().commit();
    }

    public void addTask(Task task) {
        em.getTransaction().begin();
        em.persist( task );
        em.getTransaction().commit();
        long now = System.currentTimeMillis();
        // schedule after it's been persisted, otherwise the id's won't be assigned
        if ( task.getDeadlines() != null ) {
            if ( task.getDeadlines().getStartDeadlines() != null ) {
                for ( Deadline deadline : task.getDeadlines().getStartDeadlines() ) {
                    if ( !deadline.isEscalated() ) {
                        // only escalate when true - typically this would only be true
                        // if the user is requested that the notification should never be escalated
                        Date date = deadline.getDate();
                        scheduler.schedule( new ScheduledTaskDeadline( task.getId(),
                                                                       deadline.getId(),
                                                                       this ),
                                            date.getTime() - now,
                                            TimeUnit.MILLISECONDS );
                    }
                }
            }

            if ( task.getDeadlines().getEndDeadLines() != null ) {
                for ( Deadline deadline : task.getDeadlines().getEndDeadLines() ) {
                    // only escalate when true - typically this would only be true
                    // if the user is requested that the notification should never be escalated
                    if ( !deadline.isEscalated() ) {
                        Date date = deadline.getDate();
                        scheduler.schedule( new ScheduledTaskDeadline( task.getId(),
                                                                       deadline.getId(),
                                                                       this ),
                                            date.getTime() - now,
                                            TimeUnit.MILLISECONDS );
                    }
                }
            }
       }
    }

    public Task getTask(long taskId) {
        Task task = em.find( Task.class,
                        taskId );
        return task;
    }

    public List<Object[]> getUnescalatedDeadlines() {
        return unescalatedDeadlines.getResultList();
    }

    public List<TaskSummary> getOpenOwnedTasksForUser(long userId,
                                                      String language) {
        openOwnedTasksForUser.setParameter( "currentUser",
                                            userId );
        openOwnedTasksForUser.setParameter( "language",
                                            language );
        List<TaskSummary> list = (List<TaskSummary>) openOwnedTasksForUser.getResultList();
        return list;
    }

    public void executeEscalatedDeadline(long taskId,
                                         long deadlineId) {
        EntityManager localEm = emf.createEntityManager();
        Task task = localEm.find( Task.class,
                             taskId );
        Deadline deadline = localEm.find( Deadline.class,
                                     deadlineId );

        if ( escalatedDeadlineHandler == null ) {
            escalatedDeadlineHandler = new DefaultEscalatedDeadlineHandler();
        }

        escalatedDeadlineHandler.executeEscalatedDeadline( task,
                                                           deadline,
                                                           localEm );
        localEm.close();
    }

    public static String toString(Reader reader) throws IOException {
        int charValue = 0;
        StringBuffer sb = new StringBuffer( 1024 );
        while ( (charValue = reader.read()) != -1 ) {
            //result = result + (char) charValue;
            sb.append( (char) charValue );
        }
        return sb.toString();
    }

    public static class ScheduledTaskDeadline
        implements
        Callable {
        private long        taskId;
        private long        deadlineId;
        private TaskService service;

        public ScheduledTaskDeadline(long taskId,
                                     long deadlineId,
                                     TaskService service) {
            this.taskId = taskId;
            this.deadlineId = deadlineId;
            this.service = service;
        }

        public long getTaskId() {
            return taskId;
        }

        public long getDeadlineId() {
            return deadlineId;
        }

        public Object call() throws Exception {
            service.executeEscalatedDeadline( taskId,
                                              deadlineId );
            return null;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (deadlineId ^ (deadlineId >>> 32));
            result = prime * result + (int) (taskId ^ (taskId >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( !(obj instanceof ScheduledTaskDeadline) ) return false;
            ScheduledTaskDeadline other = (ScheduledTaskDeadline) obj;
            if ( deadlineId != other.deadlineId ) return false;
            if ( taskId != other.taskId ) return false;
            return true;
        }

    }
}
