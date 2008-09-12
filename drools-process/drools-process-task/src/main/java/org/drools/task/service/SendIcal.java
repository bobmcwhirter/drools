package org.drools.task.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

import javax.activation.CommandInfo;
import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.drools.process.workitem.email.Connection;
import org.drools.task.Deadline;
import org.drools.task.I18NText;
import org.drools.task.Task;
import org.drools.task.TaskData;
import org.drools.task.User;
import org.drools.task.UserInfo;
import org.drools.util.ChainedProperties;

import junit.framework.TestCase;

//import net.fortuna.ical4j.model.Calendar;
//import net.fortuna.ical4j.model.DateTime;
//import net.fortuna.ical4j.model.TimeZone;
//import net.fortuna.ical4j.model.TimeZoneRegistry;
//import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
//import net.fortuna.ical4j.model.component.VEvent;
//import net.fortuna.ical4j.model.component.VTimeZone;
//import net.fortuna.ical4j.model.component.VToDo;
//import net.fortuna.ical4j.model.property.CalScale;
//import net.fortuna.ical4j.model.property.Description;
//import net.fortuna.ical4j.model.property.Method;
//import net.fortuna.ical4j.model.property.Priority;
//import net.fortuna.ical4j.model.property.ProdId;
//import net.fortuna.ical4j.model.property.Uid;
//import net.fortuna.ical4j.model.property.Version;

public class SendIcal {
    private static SimpleDateFormat df              = new SimpleDateFormat(  "yyyyMMdd'T'HHmmss" );
    static {
        df.setTimeZone( TimeZone.getTimeZone("UTC") );
    }

    private Properties              connection;
    private String                  defaultLanguage;
    
    private static SendIcal instance = new SendIcal();
    
    public static SendIcal getInstance() {
        return instance;
    }

    SendIcal() {
        ChainedProperties conf = new ChainedProperties( "process.email.conf" );
        String host = conf.getProperty( "host",
                                        null );
        String port = conf.getProperty( "port",
                                        "25" );
        //        userName = conf.getProperty( "userName", null );
        //        password = conf.getProperty( "password", null );   
        
        connection = new Properties();

        if ( host != null && host.trim().length() > 0 ) {
            connection.put( "mail.smtp.host",
                            host );
        }
        if ( port != null && port.trim().length() > 0 ) {
            connection.put( "mail.smtp.port",
                            Integer.parseInt( port ) );
        }

        defaultLanguage = conf.getProperty( "defaultLanguage",
                                            "en-UK" );
    }

    public void sendIcalForTask(Task task,
                                UserInfo userInfo) {
        TaskData data = task.getTaskData();
        User owner = data.getActualOwner();
        User creator = data.getCreatedBy();
        Date createdOn = data.getCreatedOn();
        
        if ( task.getDeadlines() == null ) {
            return;
        }

        // get earliest start deadline
        List<Deadline> startDeadlines = task.getDeadlines().getStartDeadlines();
        Deadline start = null;
        for ( Deadline deadline : startDeadlines ) {
            if ( start == null || start.getDate().getTime() > deadline.getDate().getTime() ) {
                start = deadline;
            }
        }

        // get latest end deadline
        List<Deadline> endDeadlines = task.getDeadlines().getEndDeadLines();
        Deadline end = null;
        for ( Deadline deadline : endDeadlines ) {
            if ( end == null || end.getDate().getTime() < deadline.getDate().getTime() ) {
                end = deadline;
            }
        }

        String language = userInfo.getLanguageForEntity( owner );
        String name = I18NText.getLocalText( task.getNames(),
                                             language,
                                             defaultLanguage );
        String summary = I18NText.getLocalText( task.getSubjects(),
                                                language,
                                                defaultLanguage );
        String description = I18NText.getLocalText( task.getDescriptions(),
                                                    language,
                                                    defaultLanguage );
        // send ical for start
        if ( start != null ) {
            try {
                sendIcal(task.getId(), name, summary, description, task.getPriority(), start.getDate(), owner, creator, createdOn, userInfo, "Start");
            } catch ( Exception e ) {
                
            }
        }
        
        // send ical for end
        if ( end != null ) {
            try {
                sendIcal(task.getId(), name, summary, description, task.getPriority(), end.getDate(), owner, creator, createdOn, userInfo, "End");
            } catch ( Exception e ) {
                
            }
        }
    }

    public void sendIcal(long taskId,
                         String name,
                         String summary,
                         String description,
                         int priority,
                         Date startDate,                       
                         User owner,
                         User creator,
                         Date createdOn,    
                         UserInfo userInfo,
                         String type) throws Exception {
        MimetypesFileTypeMap mimetypes = (MimetypesFileTypeMap) MimetypesFileTypeMap.getDefaultFileTypeMap();
        mimetypes.addMimeTypes( "text/calendar ics ICS" );

        MailcapCommandMap mailcap = (MailcapCommandMap) MailcapCommandMap.getDefaultCommandMap();
        mailcap.addMailcap( "text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain" );

        System.out.println( connection );
        Session session = Session.getInstance( connection,
                                               null );

        // Define message
        MimeMessage message = new MimeMessage( session );
        message.setHeader( "Content-Class",
                           "urn:content-classes:calendarmessage" );
        message.setHeader( "Content-ID",
                           "calendar_message" );

        String creatorEmail = userInfo.getEmailForEntity( creator );
        message.setFrom( new InternetAddress( creatorEmail ) );
        message.setReplyTo( new InternetAddress[]{new InternetAddress( creatorEmail )} );
        message.addRecipient( Message.RecipientType.TO,
                              new InternetAddress( userInfo.getEmailForEntity( owner ) ) );
        message.setSubject( "Task Assignment " + type + " Event: " + name );
        message.setSentDate( new Date() );

        // Create a Multipart
        Multipart multipart = new MimeMultipart( "alternative" );

        // Add text message
        BodyPart messageBodyPart = new MimeBodyPart();
        String text = "Summary\n-------\n\n" + summary + "\n\nDescription\n-----------\n\n" + description;
        messageBodyPart.setText( text );
        messageBodyPart.setDataHandler( new DataHandler( new ByteArrayDataSource( text,
                                                                                  "text/plain" ) ) );
        multipart.addBodyPart( messageBodyPart );

        // Add ical
        messageBodyPart = new MimeBodyPart();
        String filename = "ical-" + type + "-"+ taskId + ".ics";
        messageBodyPart.setFileName( filename );
        messageBodyPart.setHeader( "Content-Class",
                                   "urn:content-classes:calendarmessage" );
        messageBodyPart.setHeader( "Content-ID",
                                   "calendar_message" );
        String icalStr = getIcal( summary,
                                  description,
                                  startDate,
                                  priority,
                                  userInfo.getDisplayName( creator ),
                                  creatorEmail,
                                  type );
        System.out.println( icalStr );
        messageBodyPart.setDataHandler( new DataHandler( new ByteArrayDataSource( icalStr,
                                                                                  "text/calendar; charset=US-ASCII; " ) ) );
        multipart.addBodyPart( messageBodyPart );

        message.setContent( multipart );

        Transport.send( message );
    }

    private String getIcal(String summary,
                           String description,
                           Date date,
                           int priority,
                           String organizerDisplayName,
                           String organizerEmail,
                           String type) {
        StringBuilder builder = new StringBuilder();
        builder.append( "BEGIN:VCALENDAR\n" );
        builder.append( "PRODID:-//iCal4j 1.0//EN\n" );
        builder.append( "CALSCALE:GREGORIAN\n" );
        builder.append( "VERSION:2.0\n" );
        builder.append( "METHOD:REQUEST\n" );
        builder.append( "BEGIN:VEVENT\n" );
        builder.append( "DTSTART;TZID=UTC:" + df.format( date ) + "\n" );
        builder.append( "UID:" + UUID.randomUUID().toString() + "\n" );
        builder.append( "ORGANIZER;CN=\"" + organizerDisplayName + "\":mailto:" + organizerEmail + "\n" );
        builder.append( "DTSTAMP;TZID=UTC:" + df.format( new Date() ) + "\n" );
        builder.append( "SUMMARY:\"Task " + type + " : " + summary + "\"\n" );
        builder.append( "DESCRIPTION:\"" + description + "\"\n" );
        builder.append( "PRIORITY:" + priority + "\n" );
        builder.append( "END:VEVENT\n" );
        builder.append( "END:VCALENDAR\n" );
        return builder.toString();
//        String str = "";
//        str += "BEGIN:VCALENDAR\n";
//        str += "PRODID:-//Apple Inc.//iCal 3.0//EN\n";
//        str += "CALSCALE:GREGORIAN\n";
//        str += "VERSION:2.0\n";
//        str += "METHOD:REQUEST\n";
//        str += "BEGIN:VTIMEZONE\n";
//        str += "TZID:US/Mountain\n";
//        str += "BEGIN:DAYLIGHT\n";
//        str += "TZOFFSETFROM:-0700\n";
//        str += "TZOFFSETTO:-0600\n";
//        str += "DTSTART:20070311T020000\n";
//        str += "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=2SU\n";
//        str += "TZNAME:MDT\n";
//        str += "END:DAYLIGHT\n";
//        str += "BEGIN:STANDARD\n";
//        str += "TZOFFSETFROM:-0600\n";
//        str += "TZOFFSETTO:-0700\n";
//        str += "DTSTART:20071104T020000\n";
//        str += "RRULE:FREQ=YEARLY;BYMONTH=11;BYDAY=1SU\n";
//        str += "TZNAME:MST\n";
//        str += "END:STANDARD\n";
//        str += "END:VTIMEZONE\n";
//        str += "BEGIN:VEVENT\n";
//        //        str += "SEQUENCE:4\n";
//        str += "DTSTART;TZID=US/Mountain:20080911T180000\n";
//        //        str += "DURATION:PT1H\n";
//        str += "UID:EC585E23-E48D-435A-B351-C6B7ABA9A949\n";
//        str += "ORGANIZER;CN=\"Zachery Jensen\":mailto:zjensen@edustructures.com\n";
//        str += "DTSTAMP:20080910T235058Z\n";
//        str += "SUMMARY:Test Todo\n";
//        //        str += "ATTENDEE;CUTYPE=INDIVIDUAL;PARTSTAT=NEEDS-ACTION;ROLE=REQ-PARTICIPANT;VP=TRUE:mailto:Mproctor@redhat.com\n";
//        //        str += "ATTENDEE;CN=\"Zachery Jensen\";PARTSTAT=ACCEPTED:mailto:zjensen@edustructures.com\n";
//        //        str += "CREATED:20080910T235039Z\n";
//        //        str += "LOCATION:Test Location\n";
//        str += "END:VEVENT\n";
//        str += "END:VCALENDAR\n";
//        return str;  
    }
}
