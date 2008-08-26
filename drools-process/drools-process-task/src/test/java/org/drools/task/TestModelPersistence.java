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
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.collections.map.HashedMap;
import org.drools.task.Attachment;
import org.drools.task.BooleanExpression;
import org.drools.task.Comment;
import org.drools.task.Deadline;
import org.drools.task.Deadlines;
import org.drools.task.Delegation;
import org.drools.task.Escalation;
import org.drools.task.Group;
import org.drools.task.IL8NText;
import org.drools.task.Notification;
import org.drools.task.NotificationPresentationElements;
import org.drools.task.OrganizationalEntity;
import org.drools.task.PeopleAssignments;
import org.drools.task.Reassignment;
import org.drools.task.TaskPresentationElements;
import org.drools.task.Status;
import org.drools.task.Task;
import org.drools.task.TaskData;
import org.drools.task.User;
import org.mvel.MVEL;
import org.mvel.ParserContext;
import org.mvel.compiler.ExpressionCompiler;
import org.mvel.util.MVELClassLoader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

import junit.framework.TestCase;

public class TestModelPersistence extends TestCase {

    public Object eval(Reader reader, Map vars) {
        try {
            int charValue = 0;
            StringBuffer sb = new StringBuffer( 1024 );
            while ( (charValue = reader.read()) != -1 ) {
                //result = result + (char) charValue;
                sb.append( (char) charValue );
            }
            return eval( sb.toString(), vars );
        } catch ( IOException e ) {
            throw new RuntimeException( "Exception Thrown",
                                        e );
        }
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
        context.addImport( "IL8NText", IL8NText.class );
        context.addImport( "Notification", Notification.class );
        context.addImport( "NotificationPresentationElements", NotificationPresentationElements.class );
        context.addImport( "OrganizationalEntity", OrganizationalEntity.class );
        context.addImport( "PeopleAssignments", PeopleAssignments.class );
        context.addImport( "Reassignment", Reassignment.class );
        context.addImport( "Status", Status.class );
        context.addImport( "Task", Task.class );
        context.addImport( "TaskData", TaskData.class );
        context.addImport( "TaskPresentationElements", TaskPresentationElements.class );
        context.addImport( "User", User.class );

        return MVEL.executeExpression( compiler.compile( context ), vars );
    }

    public void fullHibernateRoundtripWithAdditionalMVELCheck() throws Exception {
        // Use persistence.xml configuration
        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.drools.task" );
        EntityManager em = emf.createEntityManager(); // Retrieve an application managed entity manager

        em.getTransaction().begin();

        Task task1 = new Task();
        task1.setPriority( 100 );

        PeopleAssignments peopleAssignments = new PeopleAssignments();
        task1.setPeopleAssignments( peopleAssignments );

        User darth = new User( "Darth",
                               "Vader" );
        peopleAssignments.setTaskInitiator( darth );
        em.persist( darth );

        List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
        peopleAssignments.setPotentialOwners( potentialOwners );
        User bobba = new User( "Bobba",
                               "Fet" );
        potentialOwners.add( bobba );
        em.persist( bobba );

        User jabba = new User( "Jabba",
                               "Hutt" );
        potentialOwners.add( jabba );
        em.persist( jabba );

        List<OrganizationalEntity> excludedOwners = new ArrayList<OrganizationalEntity>();
        peopleAssignments.setExcludedOwners( excludedOwners );
        User dalai = new User( "Dalai",
                               "Lama" );
        excludedOwners.add( dalai );
        em.persist( dalai );

        User christoper = new User( "Christoper",
                                    "Columbus" );
        excludedOwners.add( christoper );
        em.persist( christoper );

        List<OrganizationalEntity> stakeholders = new ArrayList<OrganizationalEntity>();
        peopleAssignments.setTaskStakeholders( stakeholders );
        User stuart = new User( "Stuart",
                              "Little" );
        stakeholders.add( stuart );
        em.persist( stuart );

        User jane = new User( "Jane",
                              "Austin" );
        stakeholders.add( jane );
        em.persist( jane );

        List<OrganizationalEntity> businessAdmin = new ArrayList<OrganizationalEntity>();
        peopleAssignments.setBusinessAdministrators( businessAdmin );
        User peter = new User( "Peter",
                               "Parker" );
        businessAdmin.add( peter );
        em.persist( peter );

        User steve = new User( "Steve",
                               "Rogers" );
        businessAdmin.add( steve );
        em.persist( steve );

        List<OrganizationalEntity> recipients = new ArrayList<OrganizationalEntity>();
        peopleAssignments.setRecipients( recipients );
        User sly = new User( "Sly",
                             "Stalone" );
        recipients.add( sly );
        em.persist( sly );

        User liz = new User( "Elizabeth",
                             "Windsor" );
        recipients.add( liz );
        em.persist( liz );

        TaskData taskData = new TaskData();
        task1.setTaskData( taskData );

        taskData.setActivationTime( new Date( 10000000 ) );
        taskData.setCreatedOn( new Date( 10000000 ) );
        taskData.setExpirationTime( new Date( 10000000 ) );
        taskData.setStatus( Status.Created );

        List<Attachment> attachments = new ArrayList<Attachment>();
        taskData.setAttachments( attachments );

        Attachment attachment = new Attachment();
        attachment.setAccessType( AccessType.Inline );
        attachment.setAttachedAt( new Date( 10000000 ) );
        attachment.setAttachedBy( liz );
        attachment.setContentType( "text" );
        attachment.setName( "file.txt" );
        attachment.setAttachment( new byte[]{1, 0, 0, 1} );
        attachments.add( attachment );

        attachment = new Attachment();
        attachment.setAccessType( AccessType.Url );
        attachment.setAttachedAt( new Date( 10000000 ) );
        attachment.setAttachedBy( liz );
        attachment.setContentType( "text" );
        attachment.setName( "file2.txt" );
        attachment.setAttachment( new String( "http://domain.org/file.txt" ).getBytes() );
        attachments.add( attachment );

        List<Comment> comments = new ArrayList<Comment>();
        taskData.setComments( comments );
        Comment comment = new Comment();
        comment.setAddedBy( peter );
        comment.setAddedDate( new Date( 10000000 ) );
        comment.setText( "this is a short comment" );
        comments.add( comment );

        comment = new Comment();
        comment.setAddedBy( steve );
        comment.setAddedDate( new Date( 10000000 ) );
        comment.setText( "this is a loooooooooooooooooooooooooooooooooooooooooooooooong comment" );
        comments.add( comment );

        TaskPresentationElements tpresentationElements = new TaskPresentationElements();
        task1.setTaskPresentationElements( tpresentationElements );
        List<IL8NText> names = new ArrayList<IL8NText>();
        tpresentationElements.setNames( names );
        List<IL8NText> subjects = new ArrayList<IL8NText>();
        tpresentationElements.setSubjects( subjects );
        List<IL8NText> descriptions = new ArrayList<IL8NText>();
        tpresentationElements.setDescriptions( descriptions );

        names.add( new IL8NText( "en-UK",
                                 "This is my task name" ) );
        names.add( new IL8NText( "en-DK",
                                 "Dies ist mein task Name" ) );

        subjects.add( new IL8NText( "en-UK",
                                    "This is my task subject" ) );
        subjects.add( new IL8NText( "en-DK",
                                    "Das ist mein task Thema" ) );

        descriptions.add( new IL8NText( "en-UK",
                                        "This is my task description" ) );
        descriptions.add( new IL8NText( "en-DK",
                                        "Das ist mein task Beschreibung" ) );

        Delegation delegation = new Delegation();
        task1.setDelegation( delegation );
        delegation.setAllowed( Allowed.PotentialOwners );

        List<OrganizationalEntity> groups = new ArrayList<OrganizationalEntity>();
        delegation.setDelegates( groups );
        Group crusaders = new Group( "Crusaders" );
        em.persist( crusaders );
        groups.add( crusaders );

        Group knightsTempler = new Group( "Knights Templer" );
        em.persist( knightsTempler );
        groups.add( knightsTempler );

        Deadlines deadlines = new Deadlines();
        task1.setDeadlines( deadlines );

        List<Deadline> startDeadlines = new ArrayList<Deadline>();
        deadlines.setStartDeadlines( startDeadlines );
        Deadline deadline = new Deadline();
        startDeadlines.add( deadline );
        deadline.setDate( new Date( 10000000 ) );
        List<IL8NText> docs = new ArrayList<IL8NText>();
        deadline.setDocumentation( docs );
        docs.add( new IL8NText( "en-UK",
                                "Start Deadline documentation" ) );
        docs.add( new IL8NText( "en-DK",
                                "Start Termin Dokumentation" ) );

        List<Escalation> escalations = new ArrayList<Escalation>();
        deadline.setEscalations( escalations );
        Escalation escalation = new Escalation();
        escalations.add( escalation );
        escalation.setName( "My Start Escalation" );

        List<BooleanExpression> constraints = new ArrayList<BooleanExpression>();
        escalation.setConstraints( constraints );
        constraints.add( new BooleanExpression( "mvel",
                                                "true" ) );

        List<Notification> notifications = new ArrayList<Notification>();
        escalation.setNotifications( notifications );

        Notification notification = new Notification();
        notifications.add( notification );
        notification.setPriority( 1000 );
        docs = new ArrayList<IL8NText>();
        notification.setDocumentation( docs );
        docs.add( new IL8NText( "en-UK",
                                "Start Notification documentation" ) );
        docs.add( new IL8NText( "en-DK",
                                "Start Anmeldung Dokumentation" ) );

        businessAdmin = new ArrayList<OrganizationalEntity>();
        notification.setBusinessAdministrators( businessAdmin );
        User bruce = new User( "Bruce",
                               "Wayne " );
        businessAdmin.add( bruce );
        em.persist( bruce );
        businessAdmin.add( peter );

        recipients = new ArrayList<OrganizationalEntity>();
        notification.setRecipients( recipients );
        User tony = new User( "Tony",
                              "Stark" );
        recipients.add( tony );
        em.persist( tony );
        recipients.add( darth );

        NotificationPresentationElements npresentationElements = new NotificationPresentationElements();
        notification.setNotificationPresentationElements( npresentationElements );
        names = new ArrayList<IL8NText>();
        npresentationElements.setNames( names );
        subjects = new ArrayList<IL8NText>();
        npresentationElements.setSubjects( subjects );
        descriptions = new ArrayList<IL8NText>();
        npresentationElements.setDescriptions( descriptions );

        names.add( new IL8NText( "en-UK",
                                 "This is my start notification name" ) );
        names.add( new IL8NText( "en-DK",
                                 "Dies ist mein start anmeldung Name" ) );

        subjects.add( new IL8NText( "en-UK", "This is my start notification subject" ) );
        subjects.add( new IL8NText( "en-DK", "Das ist mein start anmeldung Thema" ) );

        descriptions.add( new IL8NText( "en-UK", "This is my start notification description" ) );
        descriptions.add( new IL8NText( "en-DK", "Das ist mein start anmeldung Beschreibung" ) );

        List<Reassignment> reassignments = new ArrayList<Reassignment>();
        escalation.setReassignments( reassignments );
        Reassignment reassignment = new Reassignment();
        reassignments.add( reassignment );

        docs = new ArrayList<IL8NText>();
        reassignment.setDocumentation( docs );
        docs.add( new IL8NText( "en-UK", "Start Reassignment documentation" ) );
        docs.add( new IL8NText( "en-DK", "Start Neuzuweisung Dokumentation" ) );

        potentialOwners = new ArrayList<OrganizationalEntity>();
        reassignment.setPotentialOwners( potentialOwners );
        potentialOwners.add( bobba );
        User cage = new User( "Luke",
                              "Cage " );
        potentialOwners.add( cage );
        em.persist( cage );
        
////////////////
        List<Deadline> endDeadlines = new ArrayList<Deadline>();
        deadlines.setEndDeadlines( endDeadlines );
        deadline = new Deadline();
        endDeadlines.add( deadline );
        deadline.setDate( new Date( 10000000 ) );
        docs = new ArrayList<IL8NText>();
        deadline.setDocumentation( docs );
        docs.add( new IL8NText( "en-UK",
                                "End Deadline documentation" ) );
        docs.add( new IL8NText( "en-DK",
                                "Ende Termin Dokumentation" ) );

        escalations = new ArrayList<Escalation>();
        deadline.setEscalations( escalations );
        escalation = new Escalation();
        escalations.add( escalation );
        escalation.setName( "My End Escalation" );

        constraints = new ArrayList<BooleanExpression>();
        escalation.setConstraints( constraints );
        constraints.add( new BooleanExpression( "mvel",
                                                "true" ) );

        notifications = new ArrayList<Notification>();
        escalation.setNotifications( notifications );

        notification = new Notification();
        notifications.add( notification );
        notification.setPriority( 1000 );
        docs = new ArrayList<IL8NText>();
        notification.setDocumentation( docs );
        docs.add( new IL8NText( "en-UK",
                                "End Notification documentation" ) );
        docs.add( new IL8NText( "en-DK",
                                "Ende Anmeldung Dokumentation" ) );

        businessAdmin = new ArrayList<OrganizationalEntity>();
        notification.setBusinessAdministrators( businessAdmin );
        businessAdmin.add( bobba );
        businessAdmin.add( darth );

        recipients = new ArrayList<OrganizationalEntity>();
        notification.setRecipients( recipients );
        recipients.add( liz );
        recipients.add( jane );

        npresentationElements = new NotificationPresentationElements();
        notification.setNotificationPresentationElements( npresentationElements );
        names = new ArrayList<IL8NText>();
        npresentationElements.setNames( names );
        subjects = new ArrayList<IL8NText>();
        npresentationElements.setSubjects( subjects );
        descriptions = new ArrayList<IL8NText>();
        npresentationElements.setDescriptions( descriptions );

        names.add( new IL8NText( "en-UK",
                                 "This is my end notification name" ) );
        names.add( new IL8NText( "en-DK",
                                 "Dies ist mein ende anmeldung Name" ) );

        subjects.add( new IL8NText( "en-UK", "This is my end notification subject" ) );
        subjects.add( new IL8NText( "en-DK", "Das ist mein ende anmeldung Thema" ) );

        descriptions.add( new IL8NText( "en-UK", "This is my end notification description" ) );
        descriptions.add( new IL8NText( "en-DK", "Das ist mein ende anmeldung Beschreibung" ) );

        reassignments = new ArrayList<Reassignment>();
        escalation.setReassignments( reassignments );
        reassignment = new Reassignment();
        reassignments.add( reassignment );

        docs = new ArrayList<IL8NText>();
        reassignment.setDocumentation( docs );
        docs.add( new IL8NText( "en-UK", "End Reassignment documentation" ) );
        docs.add( new IL8NText( "en-DK", "Ende Neuzuweisung Dokumentation" ) );

        potentialOwners = new ArrayList<OrganizationalEntity>();
        reassignment.setPotentialOwners( potentialOwners );
        potentialOwners.add( stuart );
        potentialOwners.add( dalai );
////////////////////        


        em.persist( task1 );

        em.flush();
        em.clear();

        Task task2 = em.find( Task.class,
                              task1.getId() );
        assertNotSame( task1,
                       task2 );
//        assertEquals( task1,
//                      task2 );

//        XStream xstream = new XStream();
//        String xml = xstream.toXML( task1 );
//
//        System.out.println( xml );
//
//        Task task3 = (Task) xstream.fromXML( new InputStreamReader( getClass().getResourceAsStream( "FullyPopulatedTask.xml" ) ) );
        
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "FullyPopulatedTask.mvel" ) );
        Map  vars = new HashedMap();
        vars.put( "bytes1", new byte[]{1, 0, 0, 1} );        
        Task task3= (Task) eval( reader, vars );               
        
        assertNotSame( task1,
                       task3 );
        assertEquals( task1,
                      task3 );

        em.close();
        emf.close();
    }

}
