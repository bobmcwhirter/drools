package org.drools.task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.collections.map.HashedMap;
import org.drools.task.Attachment;
import org.drools.task.BooleanExpression;
import org.drools.task.Comment;
import org.drools.task.Deadline;
import org.drools.task.Deadlines;
import org.drools.task.Delegation;
import org.drools.task.Escalation;
import org.drools.task.Group;
import org.drools.task.I18NText;
import org.drools.task.Notification;
import org.drools.task.OrganizationalEntity;
import org.drools.task.PeopleAssignments;
import org.drools.task.Reassignment;
import org.drools.task.Status;
import org.drools.task.Task;
import org.drools.task.TaskData;
import org.drools.task.User;
import org.drools.task.query.TaskSummary;
import org.drools.task.service.TaskService;
import org.drools.task.utils.CollectionUtils;
import org.mvel.MVEL;
import org.mvel.ParserContext;
import org.mvel.compiler.ExpressionCompiler;
import org.mvel.util.MVELClassLoader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

import junit.framework.TestCase;

public abstract class BaseTest extends TestCase {
    protected EntityManagerFactory emf;
    protected EntityManager em;
    
    protected Map<String, User> users;
    protected Map<String, Group> groups;
    
    protected TaskService taskService;
    
    protected void setUp() throws Exception {
        // Use persistence.xml configuration
        emf = Persistence.createEntityManagerFactory( "org.drools.task" );
        em = emf.createEntityManager(); // Retrieve an application managed entity manager        
        
        taskService = new TaskService( em );
        
        Map  vars = new HashedMap();
        //vars.put( "em", em );   
        
        Reader reader = new InputStreamReader( BaseTest.class.getResourceAsStream( "LoadUsers.mvel" ) );     
        users = ( Map<String, User> ) eval( reader, vars );   
        for ( User user : users.values() ) {
            taskService.addUser( user );
        }           
        
        reader = new InputStreamReader( BaseTest.class.getResourceAsStream( "LoadGroups.mvel" ) );      
        groups = ( Map<String, Group> ) eval( reader, vars );     
        for ( Group group : groups.values() ) {
            taskService.addGroup( group );
        }                      
    }
    
    protected void tearDown() throws Exception {
        em.close();
        emf.close();
    }    

    public Object eval(Reader reader, Map vars) {
        try {
            return eval( toString( reader ), vars );
        } catch ( IOException e ) {
            throw new RuntimeException( "Exception Thrown",
                                        e );
        }
    }
    
    public String toString(Reader reader) throws IOException {
        int charValue = 0;
        StringBuffer sb = new StringBuffer( 1024 );
        while ( (charValue = reader.read()) != -1 ) {
            //result = result + (char) charValue;
            sb.append( (char) charValue );
        }
        return sb.toString();
    }

    public Object eval(String str, Map vars) {
        ExpressionCompiler compiler = new ExpressionCompiler( str.trim() );

        ParserContext context = new ParserContext();
        context.addPackageImport( "org.drools.task" );
        context.addPackageImport( "java.util" );
        
        context.addImport( "AccessType", AccessType.class );
        context.addImport( "Allowed", Allowed.class );
        context.addImport( "Attachment", Attachment.class );
        context.addImport( "BooleanExpression", BooleanExpression.class );
        context.addImport( "Comment", Comment.class );
        context.addImport( "Deadline", Deadline.class );
        context.addImport( "Deadlines", Deadlines.class );
        context.addImport( "Delegation", Delegation.class );
        context.addImport( "Escalation", Escalation.class );
        context.addImport( "Group", Group.class );
        context.addImport( "I18NText", I18NText.class );
        context.addImport( "Notification", Notification.class );
        context.addImport( "OrganizationalEntity", OrganizationalEntity.class );
        context.addImport( "PeopleAssignments", PeopleAssignments.class );
        context.addImport( "Reassignment", Reassignment.class );
        context.addImport( "Status", Status.class );
        context.addImport( "Task", Task.class );
        context.addImport( "TaskData", TaskData.class );
        context.addImport( "TaskSummary", TaskSummary.class );
        context.addImport( "User", User.class );

        return MVEL.executeExpression( compiler.compile( context ), vars );
    }
}
