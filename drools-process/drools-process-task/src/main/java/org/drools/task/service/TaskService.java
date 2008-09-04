package org.drools.task.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.drools.task.AccessType;
import org.drools.task.Attachment;
import org.drools.task.AttachmentContent;
import org.drools.task.Comment;
import org.drools.task.Deadline;
import org.drools.task.Group;
import org.drools.task.OrganizationalEntity;
import org.drools.task.PeopleAssignments;
import org.drools.task.Status;
import org.drools.task.Task;
import org.drools.task.TaskData;
import org.drools.task.User;
import org.drools.task.query.DeadlineSummary;
import org.drools.task.query.TaskSummary;

public class TaskService {
    EntityManagerFactory             emf;

    //@TODO we should not be re-using the same em, create as needed (but have issues with closing the em while returned objects are still in use
    EntityManager                    em;

    //@TODO these should all be named querries
    Query                            tasksAssignedAsBusinessAdministrator;
    Query                            tasksAssignedAsExcludedOwner;
    Query                            tasksAssignedAsPotentialOwner;
    Query                            tasksAssignedAsRecipient;
    Query                            tasksAssignedAsTaskInitiator;
    Query                            tasksAssignedAsTaskStakeholder;
    Query                            tasksOwned;
    Query                            unescalatedDeadlines;

    ScheduledThreadPoolExecutor      scheduler;

    private EscalatedDeadlineHandler escalatedDeadlineHandler;

    public TaskService(EntityManagerFactory emf) {
        this.emf = emf;
        em = emf.createEntityManager();

        try {
            Reader reader = new InputStreamReader( getClass().getResourceAsStream( "TasksOwned.txt" ) );
            tasksOwned = em.createQuery( toString( reader ) );

            reader = new InputStreamReader( getClass().getResourceAsStream( "TasksAssignedAsBusinessAdministrator.txt" ) );
            tasksAssignedAsBusinessAdministrator = em.createQuery( toString( reader ) );

            reader = new InputStreamReader( getClass().getResourceAsStream( "TasksAssignedAsExcludedOwner.txt" ) );
            tasksAssignedAsExcludedOwner = em.createQuery( toString( reader ) );

            reader = new InputStreamReader( getClass().getResourceAsStream( "TasksAssignedAsPotentialOwner.txt" ) );
            tasksAssignedAsPotentialOwner = em.createQuery( toString( reader ) );

            reader = new InputStreamReader( getClass().getResourceAsStream( "TasksAssignedAsRecipient.txt" ) );
            tasksAssignedAsRecipient = em.createQuery( toString( reader ) );

            reader = new InputStreamReader( getClass().getResourceAsStream( "TasksAssignedAsTaskInitiator.txt" ) );
            tasksAssignedAsTaskInitiator = em.createQuery( toString( reader ) );

            reader = new InputStreamReader( getClass().getResourceAsStream( "TasksAssignedAsTaskStakeholder.txt" ) );
            tasksAssignedAsTaskStakeholder = em.createQuery( toString( reader ) );
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to inialize TaskService.",
                                        e );
        }
        scheduler = new ScheduledThreadPoolExecutor( 3 );

        try {
            Reader reader = new InputStreamReader( getClass().getResourceAsStream( "UnescalatedDeadlines.txt" ) );
            unescalatedDeadlines = em.createQuery( toString( reader ) );
            long now = System.currentTimeMillis();
            for ( Object object : unescalatedDeadlines.getResultList() ) {
                DeadlineSummary summary = (DeadlineSummary) object;
                scheduler.schedule( new ScheduledTaskDeadline( summary.getTaskId(),
                                                               summary.getDeadlineId(),
                                                               this ),
                                    summary.getDate().getTime() - now,
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

        TaskData taskData = task.getTaskData();
        // new tasks start off with status created
        taskData.setStatus( Status.Created );

        if ( task.getPeopleAssignments() != null ) {
            List potentialOwners = task.getPeopleAssignments().getPotentialOwners();
            if ( potentialOwners.size() == 1 ) {
                // if there is a single potential owner, assign and set status to Reserved
                taskData.setActualOwner( (User) potentialOwners.get( 0 ) );
                taskData.setStatus( Status.Reserved );
            } else if ( potentialOwners.size() > 1 ) {
                // multiple potential owners, so set to Ready so one can claim.
                taskData.setStatus( Status.Ready );
            } else {
                //@TODO we have no potential owners
            }
        } else {
            //@TODO we have no potential owners
        }

        // set the CreatedOn date if it's not already set
        if ( taskData.getCreatedOn() == null ) {
            taskData.setCreatedOn( new Date() );
        }

        //@FIXME for now we activate on creation, unless date is supplied
        if ( taskData.getActivationTime() == null ) {
            taskData.setActivationTime( taskData.getCreatedOn() );
        }

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

    public void claim(long taskId,
                      long userId) {
        Task task = em.find( Task.class,
                             taskId );
        
        User user = em.find( User.class, userId );
        
        TaskData taskData = task.getTaskData();

        //task must be in status Ready
        if ( taskData.getStatus() == Status.Ready ) {
            // check permissions
            PeopleAssignments people = task.getPeopleAssignments();
            if ( isAllowed( user, new List[] { people.getPotentialOwners(), people.getBusinessAdministrators() } ) ) {
                em.getTransaction().begin();
                // only potential owners and business admin can claim a task
                taskData.setStatus( Status.Reserved );
                taskData.setActualOwner( user );
                em.getTransaction().commit();
            } else {
                // @TODO Error
            }
        } else {
            // @TODO Error
        }
    }    

    public void start(long taskId,
                      long userId) {
        Task task = em.find( Task.class,
                             taskId );
        
        User user = em.find( User.class, userId );
        
        TaskData taskData = task.getTaskData();
        

        // Status must be Read or Reserved
        if ( taskData.getStatus() == Status.Ready ) {
            // if Ready must be potentialOwner
            PeopleAssignments people = task.getPeopleAssignments();
            if ( isAllowed( user, new List[] { people.getPotentialOwners() } ) ) {
                em.getTransaction().begin();
                taskData.setActualOwner( user );
                taskData.setStatus( Status.InProgress );
                em.getTransaction().commit();              
            } else {
                // @TODO Error
            }
        } else if ( taskData.getStatus() == Status.Reserved ) {
            // if Reserved must be actual owner
            if ( taskData.getActualOwner().equals( user ) ) {
                em.getTransaction().begin();
                taskData.setStatus( Status.InProgress );
                em.getTransaction().commit();
            } else {
                // @TODO Error
            }
        } else {
            // @TODO Error
            return;            
        }        
    }
    
    public void stop(long taskId,
                      long userId) {
        Task task = em.find( Task.class,
                             taskId );
        
        User user = em.find( User.class, userId );
        
        TaskData taskData = task.getTaskData();
        
        PeopleAssignments people = task.getPeopleAssignments();        
        if ( taskData.getStatus() == Status.InProgress && ( taskData.getActualOwner().equals( user ) || isAllowed( user, new List[] { people.getBusinessAdministrators() } ) ) ) {
            // Status must be InProgress and actual owner, switch to Reserved
            em.getTransaction().begin();
            taskData.setStatus( Status.Reserved );
            em.getTransaction().commit();
        } else {
            // @TODO Error
            return;            
        }  
    }
    
    public void complete(long taskId, long userId) {
        Task task = em.find( Task.class,
                             taskId );
        
        User user = em.find( User.class, userId );
        
        TaskData taskData = task.getTaskData();
        
        if ( taskData.getStatus() == Status.InProgress && taskData.getActualOwner().equals( user ) ) {
            // Status must be InProgress and actual owner, switch to Reserved
            em.getTransaction().begin();
            taskData.setStatus( Status.Completed );
            em.getTransaction().commit();
        } else {
            // @TODO Error
            return;            
        }  
    }    

    public void release(long taskId, long userId) {
        Task task = em.find( Task.class,
                             taskId );
        
        User user = em.find( User.class, userId );
        
        TaskData taskData = task.getTaskData();
        
        // task must be reserved or in progress and owned by user
        PeopleAssignments people = task.getPeopleAssignments();        
        if ( (taskData.getStatus() == Status.Reserved || taskData.getStatus() == Status.InProgress) && ( taskData.getActualOwner().equals( user ) || isAllowed( user, new List[] { people.getBusinessAdministrators() } ) ) ) {
            em.getTransaction().begin();
            taskData.setStatus( Status.Ready );
            taskData.setActualOwner( null );
            em.getTransaction().commit();
        } else {
            //@TODO Error
        } 
    }

    public void suspend(long taskId, long userId) {
        Task task = em.find( Task.class,
                             taskId );
        
        User user = em.find( User.class, userId );
        
        TaskData taskData = task.getTaskData();
        
        List[] allowed;
        PeopleAssignments people = task.getPeopleAssignments();
        if ( taskData.getStatus() == Status.Ready ) {
            // If it's ready then potential owners can suspect too
            allowed = new List[] { people.getPotentialOwners(), people.getBusinessAdministrators() };
        } else {
            allowed = new List[] { people.getBusinessAdministrators() };
        }
        
        if ( (taskData.getStatus() != Status.Completed && taskData.getStatus() != Status.Failed && taskData.getStatus() != Status.Created) && ( ( taskData.getActualOwner() != null && taskData.getActualOwner().equals( user ) ) || isAllowed( user, allowed ) ) ) {
            em.getTransaction().begin();
            taskData.setStatus( Status.Suspended );
            em.getTransaction().commit();
        } else {
            //@TODO Error            
        }
    }
    
    public void resume(long taskId, long userId) {
        Task task = em.find( Task.class,
                             taskId );
        
        User user = em.find( User.class, userId );
        
        TaskData taskData = task.getTaskData();
        
        List[] allowed;
        PeopleAssignments people = task.getPeopleAssignments();
        if ( taskData.getPreviousStatus() == Status.Ready ) {
            // If it's ready then potential owners can suspect too
            allowed = new List[] { people.getPotentialOwners(), people.getBusinessAdministrators() };
        } else {
            allowed = new List[] { people.getBusinessAdministrators() };
        }
        
        if ( (taskData.getStatus() == Status.Suspended) && ( ( taskData.getActualOwner() != null && taskData.getActualOwner().equals( user ) ) || isAllowed( user, allowed ) ) ) {
            em.getTransaction().begin();
            taskData.setStatus( taskData.getPreviousStatus() );
            em.getTransaction().commit();
        } else {
            //@TODO Error            
        }
    }    
    
    public void fail(long taskId, long userId) {
        Task task = em.find( Task.class,
                             taskId );
        
        User user = em.find( User.class, userId );
        
        TaskData taskData = task.getTaskData();
        
        if ( taskData.getStatus() == Status.InProgress && taskData.getActualOwner().equals( user ) ) {
            // Status must be InProgress and actual owner, switch to Reserved
            em.getTransaction().begin();
            taskData.setStatus( Status.Failed );
            em.getTransaction().commit();
        } else {
            // @TODO Error
            return;            
        }  
    }

    public void addComment(long taskId,
                           Comment comment) {
        Task task = em.find( Task.class,
                             taskId );
        if ( task == null ) {
            // throw some exception
        }

        em.getTransaction().begin();

        List<Comment> list = task.getTaskData().getComments();
        if ( list == null || list == Collections.<Comment> emptyList() ) {
            list = new ArrayList<Comment>( 1 );
            task.getTaskData().setComments( list );
        }

        list.add( comment );

        em.getTransaction().commit();
    }

    public void addAttachment(long taskId,
                              Attachment attachment,
                              AttachmentContent content) {
        Task task = em.find( Task.class,
                             taskId );

        if ( task == null ) {
            // throw some exception
        }

        em.getTransaction().begin();

        em.persist( content );
        attachment.setSize( content.getContent().length );
        attachment.setContentId( content.getId() );

        List<Attachment> list = task.getTaskData().getAttachments();
        if ( list == null || list == Collections.<Attachment> emptyList() ) {
            list = new ArrayList<Attachment>( 1 );
            task.getTaskData().setAttachments( list );
        }

        list.add( attachment );
        em.getTransaction().commit();
    }

    public AttachmentContent getAttachmentContent(long contentId) {
        AttachmentContent content = em.find( AttachmentContent.class,
                                             contentId );
        return content;
    }

    public void deleteAttachment(long taskId,
                                 long attachmentId,
                                 long attachmentContentId) {
        // @TODO I can't get this to work with HQL deleting the Attachment. Hibernate needs both the item removed from the collection
        // and also the item deleted, so for now have to load the entire Task, I suspect that this is due to using the same EM which 
        // is caching things.
        Task task = em.find( Task.class,
                             taskId );

        em.getTransaction().begin();
        for ( Iterator<Attachment> it = task.getTaskData().getAttachments().iterator(); it.hasNext(); ) {
            Attachment attachment = it.next();
            if ( attachment.getId() == attachmentId ) {
                it.remove();
                em.remove( attachment ); // need to do this otherwise it just removes the link id, without removing the attachment
                break;
            }
        }

        // we do this as HQL to avoid streaming in the entire HQL
        String deleteContent = "delete from AttachmentContent where id = :id";
        em.createQuery( deleteContent ).setParameter( "id",
                                                      attachmentContentId ).executeUpdate();

        em.getTransaction().commit();
    }

    public void deleteComment(long taskId,
                              long commentId) {
        // @TODO I can't get this to work with HQL deleting the Comment. Hibernate needs both the item removed from the collection
        // and also the item deleted, so for now have to load the entire Task, I suspect that this is due to using the same EM which 
        // is caching things.
        Task task = em.find( Task.class,
                             taskId );
        em.getTransaction().begin();
        for ( Iterator<Comment> it = task.getTaskData().getComments().iterator(); it.hasNext(); ) {
            Comment comment = it.next();
            if ( comment.getId() == commentId ) {
                it.remove();
                em.remove( comment ); // need to do this otherwise it just removes the link id, without removing the comment
                break;
            }
        }
        em.getTransaction().commit();
    }

    public Task getTask(long taskId) {
        Task task = em.find( Task.class,
                             taskId );
        return task;
    }

    public List<DeadlineSummary> getUnescalatedDeadlines() {
        return (List<DeadlineSummary>) unescalatedDeadlines.getResultList();
    }

    public List<TaskSummary> getTasksOwned(long userId,
                                           String language) {
        tasksOwned.setParameter( "userId",
                                 userId );
        tasksOwned.setParameter( "language",
                                 language );
        List<TaskSummary> list = (List<TaskSummary>) tasksOwned.getResultList();
        return list;
    }

    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(long userId,
                                                                     String language) {
        tasksAssignedAsBusinessAdministrator.setParameter( "userId",
                                                           userId );
        tasksAssignedAsBusinessAdministrator.setParameter( "language",
                                                           language );
        List<TaskSummary> list = (List<TaskSummary>) tasksAssignedAsBusinessAdministrator.getResultList();
        return list;
    }

    public List<TaskSummary> getTasksAssignedAsExcludedOwner(long userId,
                                                             String language) {
        tasksAssignedAsExcludedOwner.setParameter( "userId",
                                                   userId );
        tasksAssignedAsExcludedOwner.setParameter( "language",
                                                   language );
        List<TaskSummary> list = (List<TaskSummary>) tasksAssignedAsExcludedOwner.getResultList();
        return list;
    }

    public List<TaskSummary> getTasksAssignedAsPotentialOwner(long userId,
                                                              String language) {
        tasksAssignedAsPotentialOwner.setParameter( "userId",
                                                    userId );
        tasksAssignedAsPotentialOwner.setParameter( "language",
                                                    language );
        List<TaskSummary> list = (List<TaskSummary>) tasksAssignedAsPotentialOwner.getResultList();
        return list;
    }

    public List<TaskSummary> getTasksAssignedAsRecipient(long userId,
                                                         String language) {
        tasksAssignedAsRecipient.setParameter( "userId",
                                               userId );
        tasksAssignedAsRecipient.setParameter( "language",
                                               language );
        List<TaskSummary> list = (List<TaskSummary>) tasksAssignedAsRecipient.getResultList();
        return list;
    }

    public List<TaskSummary> getTasksAssignedAsTaskInitiator(long userId,
                                                             String language) {
        tasksAssignedAsTaskInitiator.setParameter( "userId",
                                                   userId );
        tasksAssignedAsTaskInitiator.setParameter( "language",
                                                   language );
        List<TaskSummary> list = (List<TaskSummary>) tasksAssignedAsTaskInitiator.getResultList();
        return list;
    }

    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(long userId,
                                                               String language) {
        tasksAssignedAsTaskStakeholder.setParameter( "userId",
                                                     userId );
        tasksAssignedAsTaskStakeholder.setParameter( "language",
                                                     language );
        List<TaskSummary> list = (List<TaskSummary>) tasksAssignedAsTaskStakeholder.getResultList();
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
    
    public boolean isAllowed(User user,  List<OrganizationalEntity>[] people) {
        for ( List<OrganizationalEntity> list : people ) {
            if ( isAllowed( user, list) ) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAllowed(User user, List<OrganizationalEntity> entities) {
        // for now just do a contains, I'll figure out group membership later.
        return entities.contains( user );
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
