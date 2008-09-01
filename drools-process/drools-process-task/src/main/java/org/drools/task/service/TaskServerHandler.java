package org.drools.task.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.drools.task.Task;
import org.drools.task.query.TaskSummary;

public class TaskServerHandler extends IoHandlerAdapter {
    private TaskService service;

    public TaskServerHandler(TaskService service) {
        this.service = service;
    }

    @Override
    public void exceptionCaught(IoSession session,
                                Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session,
                                Object message) throws Exception {
        Command cmd = (Command) message;
        switch ( cmd.getName() ) {
            case AddTaskRequest : {
                service.addTask( (Task) cmd.getArguments().get( 0 ) );
                break;
            }
            case Query_TasksOwned : {
                List<TaskSummary> results = service.getTasksOwned( (Long) cmd.getArguments().get( 0 ),
                                                                   (String) cmd.getArguments().get( 1 ) );
                List args = new ArrayList( 1 );
                args.add( results );
                Command resultsCmnd = new Command( cmd.getId(),
                                                   CommandName.Query_TaskSummaryResponse,
                                                   args );
                session.write( resultsCmnd );
                break;
            }
            case Query_TasksAssignedAsBusinessAdministrator : {
                List<TaskSummary> results = service.getTasksAssignedAsBusinessAdministrator( (Long) cmd.getArguments().get( 0 ),
                                                                                             (String) cmd.getArguments().get( 1 ) );
                List args = new ArrayList( 1 );
                args.add( results );
                Command resultsCmnd = new Command( cmd.getId(),
                                                   CommandName.Query_TaskSummaryResponse,
                                                   args );
                session.write( resultsCmnd );
                break;
            }
            case Query_TasksAssignedAsPotentialOwner : {
                List<TaskSummary> results = service.getTasksAssignedAsPotentialOwner( (Long) cmd.getArguments().get( 0 ),
                                                                                      (String) cmd.getArguments().get( 1 ) );
                List args = new ArrayList( 1 );
                args.add( results );
                Command resultsCmnd = new Command( cmd.getId(),
                                                   CommandName.Query_TaskSummaryResponse,
                                                   args );
                session.write( resultsCmnd );
                break;
            }
            case Query_TasksAssignedAsTaskInitiator : {
                List<TaskSummary> results = service.getTasksAssignedAsTaskInitiator( (Long) cmd.getArguments().get( 0 ),
                                                                                     (String) cmd.getArguments().get( 1 ) );
                List args = new ArrayList( 1 );
                args.add( results );
                Command resultsCmnd = new Command( cmd.getId(),
                                                   CommandName.Query_TaskSummaryResponse,
                                                   args );
                session.write( resultsCmnd );
                break;
            }
            case Query_TasksAssignedAsExcludedOwner : {
                List<TaskSummary> results = service.getTasksAssignedAsExcludedOwner( (Long) cmd.getArguments().get( 0 ),
                                                                                     (String) cmd.getArguments().get( 1 ) );
                List args = new ArrayList( 1 );
                args.add( results );
                Command resultsCmnd = new Command( cmd.getId(),
                                                   CommandName.Query_TaskSummaryResponse,
                                                   args );
                session.write( resultsCmnd );
                break;
            }
            case Query_TasksAssignedAsRecipient : {
                List<TaskSummary> results = service.getTasksAssignedAsRecipient( (Long) cmd.getArguments().get( 0 ),
                                                                                 (String) cmd.getArguments().get( 1 ) );
                List args = new ArrayList( 1 );
                args.add( results );
                Command resultsCmnd = new Command( cmd.getId(),
                                                   CommandName.Query_TaskSummaryResponse,
                                                   args );
                session.write( resultsCmnd );
                break;
            }
            case Query_TasksAssignedAsTaskStakeholder : {
                List<TaskSummary> results = service.getTasksAssignedAsTaskStakeholder( (Long) cmd.getArguments().get( 0 ),
                                                                                       (String) cmd.getArguments().get( 1 ) );
                List args = new ArrayList( 1 );
                args.add( results );
                Command resultsCmnd = new Command( cmd.getId(),
                                                   CommandName.Query_TaskSummaryResponse,
                                                   args );
                session.write( resultsCmnd );
                break;
            }
            default : {
                // unknown command
            }
        }

        Date date = new Date();
        System.out.println( "Message written : " + cmd.getName() + " : " + date );
    }

    @Override
    public void sessionIdle(IoSession session,
                            IdleStatus status) throws Exception {
        System.out.println( "IDLE " + session.getIdleCount( status ) );
    }
}