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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.drools.task.query.DeadlineSummary;
import org.drools.task.query.TaskSummary;
import org.drools.task.service.EscalatedDeadlineHandler;
import org.drools.task.service.TaskService;
import org.drools.task.utils.CollectionUtils;
import org.mvel.MVEL;
import org.mvel.ParserContext;
import org.mvel.compiler.ExpressionCompiler;
import org.mvel.util.MVELClassLoader;

import sun.print.resources.serviceui_zh_TW;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

import junit.framework.TestCase;

public class QueryTest extends BaseTest {

    public void testUnescalatedDeadlines() throws Exception {
        Map vars = new HashedMap();
        vars.put( "users",
                  users );
        vars.put( "groups",
                  groups );
        long now = System.currentTimeMillis();
        vars.put( "now",
                  now );

        //Reader reader;
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "QueryData_UnescalatedDeadlines.mvel" ) );
        List<Task> tasks = (List<Task>) eval( reader,
                                              vars );
        for ( Task task : tasks ) {
            taskService.addTask( task );
        }

        // should be three, one is marked as escalated
        List<DeadlineSummary> list = taskService.getUnescalatedDeadlines();
        assertEquals( 3,
                      list.size() );

        DeadlineSummary result = list.get( 0 );
        assertEquals( result.getDate().getTime(),
                      now + 4000 );

        result = list.get( 1 );
        assertEquals( result.getDate().getTime(),
                      now + 4500 );

        result = list.get( 2 );
        assertEquals( result.getDate().getTime(),
                      now + 5000 );    
    }
    

}
