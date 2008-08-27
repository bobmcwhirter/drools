package org.drools.task.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.drools.task.Group;
import org.drools.task.Task;
import org.drools.task.TaskSummary;
import org.drools.task.User;

public class TaskService {
    EntityManager em;
    
    Query allOpenTasks;
    
    public TaskService(EntityManager em) {
        this.em = em;     
        
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "AllOpenTasks.txt" ) );
        try {
            allOpenTasks = em.createQuery( toString( reader ) );
        } catch (IOException e) {
            throw new RuntimeException( "Unable to inialize TaskService, could not load query 'AllOpenTasks'" );
        }        
    }
    
    public void addUser(User user) {
        em.getTransaction().begin();
        em.persist( user );
        em.getTransaction().commit();
    }
    
    public void addUser(Group group) {
        em.getTransaction().begin();
        em.persist( group );
        em.getTransaction().commit();
    }    
    
    public void addTask(Task task) {
        em.getTransaction().begin();
        em.persist( task );
        em.getTransaction().commit();
    }
    
    public Task getTask(long taskId) {
        return em.find( Task.class, taskId );
    }
    
    public List<TaskSummary> getAllOpenTasks(long userId, String language) {
        allOpenTasks.setParameter( "currentUser", userId );
        allOpenTasks.setParameter( "language", "en-UK" );
        List<TaskSummary> list = ( List<TaskSummary> ) allOpenTasks.getResultList();
        return list;
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
}
