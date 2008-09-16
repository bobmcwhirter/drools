package org.drools.task.service;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.drools.eventmessaging.EventKey;
import org.drools.task.AccessType;
import org.drools.task.Attachment;
import org.drools.task.Content;
import org.drools.task.Comment;
import org.drools.task.Task;
import org.drools.task.query.TaskSummary;

public class TaskServerHandler extends IoHandlerAdapter {
    private TaskService            service;
    private Map<String, IoSession> clients;

    public TaskServerHandler(TaskService service) {
        this.service = service;
        this.clients = new HashMap<String, IoSession>();
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
        TaskServiceSession taskSession = service.createSession();
        try {
            switch ( cmd.getName() ) {
                case ClaimRequest : {
                    long taskId = (Long) cmd.getArguments().get( 0 );
                    String userId = (String) cmd.getArguments().get( 1 );
                    taskSession.claim( taskId,
                                       userId );
                    break;
                }
                case StartRequest : {
                    long taskId = (Long) cmd.getArguments().get( 0 );
                    String userId = (String) cmd.getArguments().get( 1 );
                    taskSession.start( taskId,
                                       userId );
                    break;
                }
                case StopRequest : {
                    long taskId = (Long) cmd.getArguments().get( 0 );
                    String userId = (String) cmd.getArguments().get( 1 );
                    taskSession.stop( taskId,
                                      userId );
                    break;
                }
                case ReleaseRequest : {
                    long taskId = (Long) cmd.getArguments().get( 0 );
                    String userId = (String) cmd.getArguments().get( 1 );
                    taskSession.release( taskId,
                                         userId );
                    break;
                }
                case SuspendRequest : {
                    long taskId = (Long) cmd.getArguments().get( 0 );
                    String userId = (String) cmd.getArguments().get( 1 );
                    taskSession.suspend( taskId,
                                         userId );
                    break;
                }
                case ResumeRequest : {
                    long taskId = (Long) cmd.getArguments().get( 0 );
                    String userId = (String) cmd.getArguments().get( 1 );
                    taskSession.resume( taskId,
                                        userId );
                    break;
                }
                case SkipRequest : {
                    long taskId = (Long) cmd.getArguments().get( 0 );
                    String userId = (String) cmd.getArguments().get( 1 );
                    taskSession.skip( taskId,
                                      userId );
                    break;
                }
                case CompleteRequest : {
                    long taskId = (Long) cmd.getArguments().get( 0 );
                    String userId = (String) cmd.getArguments().get( 1 );
                    taskSession.complete( taskId,
                                          userId );
                    break;
                }
                case FailRequest : {
                    long taskId = (Long) cmd.getArguments().get( 0 );
                    String userId = (String) cmd.getArguments().get( 1 );
                    taskSession.fail( taskId,
                                      userId );
                    break;
                }
                case GetTaskRequest : {
                    long taskId = (Long) cmd.getArguments().get( 0 );
                    EntityManager em = service.getEntityManagerFactory().createEntityManager();
                    try {
                        Task task = taskSession.getTask( taskId );
                        List args = new ArrayList( 1 );
                        args.add( task );
                        Command resultsCmnd = new Command( cmd.getId(),
                                                           CommandName.GetTaskResponse,
                                                           args );
                        session.write( resultsCmnd );
                    } finally {
                        em.close();
                    }
                    break;
                }
                case AddTaskRequest : {
                    Task task = (Task) cmd.getArguments().get( 0 );
                    taskSession.addTask( task );

                    List args = new ArrayList( 1 );
                    args.add( task.getId() );
                    Command resultsCmnd = new Command( cmd.getId(),
                                                       CommandName.AddTaskResponse,
                                                       args );
                    session.write( resultsCmnd );
                    break;
                }
                case AddCommentRequest : {
                    Comment comment = (Comment) cmd.getArguments().get( 1 );
                    taskSession.addComment( (Long) cmd.getArguments().get( 0 ),
                                            comment );

                    List args = new ArrayList( 1 );
                    args.add( comment.getId() );
                    Command resultsCmnd = new Command( cmd.getId(),
                                                       CommandName.AddCommentResponse,
                                                       args );
                    session.write( resultsCmnd );
                    break;
                }
                case DeleteCommentRequest : {
                    long taskId = (Long) cmd.getArguments().get( 0 );
                    long commentId = (Long) cmd.getArguments().get( 1 );
                    taskSession.deleteComment( taskId,
                                               commentId );
                    break;
                }
                case AddAttachmentRequest : {
                    Attachment attachment = (Attachment) cmd.getArguments().get( 1 );
                    Content content = (Content) cmd.getArguments().get( 2 );
                    taskSession.addAttachment( (Long) cmd.getArguments().get( 0 ),
                                               attachment,
                                               content );

                    List args = new ArrayList( 2 );
                    args.add( attachment.getId() );
                    args.add( content.getId() );
                    Command resultsCmnd = new Command( cmd.getId(),
                                                       CommandName.AddAttachmentResponse,
                                                       args );
                    session.write( resultsCmnd );
                    break;
                }
                case DeleteAttachmentRequest : {
                    long taskId = (Long) cmd.getArguments().get( 0 );
                    long attachmentId = (Long) cmd.getArguments().get( 1 );
                    long contentId = (Long) cmd.getArguments().get( 2 );
                    taskSession.deleteAttachment( taskId,
                                                  attachmentId,
                                                  contentId );
                    break;
                }
                case SetDocumentContentRequest : {
                    long taskId = (Long) cmd.getArguments().get( 0 );
                    Content content = (Content) cmd.getArguments().get( 1 );
                    taskSession.setDocumentContent( taskId,
                                                    content );

                    List args = new ArrayList( 1 );
                    args.add( content.getId() );
                    Command resultsCmnd = new Command( cmd.getId(),
                                                       CommandName.SetDocumentContentResponse,
                                                       args );
                    session.write( resultsCmnd );
                    break;
                }
                case GetContentRequest : {
                    long contentId = (Long) cmd.getArguments().get( 0 );
                    Content content = taskSession.getContent( contentId );
                    List args = new ArrayList( 1 );
                    args.add( content );
                    Command resultsCmnd = new Command( cmd.getId(),
                                                       CommandName.GetContentResponse,
                                                       args );
                    session.write( resultsCmnd );
                    break;
                }
                case QueryTasksOwned : {
                    List<TaskSummary> results = taskSession.getTasksOwned( (String) cmd.getArguments().get( 0 ),
                                                                           (String) cmd.getArguments().get( 1 ) );
                    List args = new ArrayList( 1 );
                    args.add( results );
                    Command resultsCmnd = new Command( cmd.getId(),
                                                       CommandName.QueryTaskSummaryResponse,
                                                       args );
                    session.write( resultsCmnd );
                    break;
                }
                case QueryTasksAssignedAsBusinessAdministrator : {
                    List<TaskSummary> results = taskSession.getTasksAssignedAsBusinessAdministrator( (String) cmd.getArguments().get( 0 ),
                                                                                                     (String) cmd.getArguments().get( 1 ) );
                    List args = new ArrayList( 1 );
                    args.add( results );
                    Command resultsCmnd = new Command( cmd.getId(),
                                                       CommandName.QueryTaskSummaryResponse,
                                                       args );
                    session.write( resultsCmnd );
                    break;
                }
                case QueryTasksAssignedAsPotentialOwner : {
                    List<TaskSummary> results = taskSession.getTasksAssignedAsPotentialOwner( (String) cmd.getArguments().get( 0 ),
                                                                                              (String) cmd.getArguments().get( 1 ) );
                    List args = new ArrayList( 1 );
                    args.add( results );
                    Command resultsCmnd = new Command( cmd.getId(),
                                                       CommandName.QueryTaskSummaryResponse,
                                                       args );
                    session.write( resultsCmnd );
                    break;
                }
                case QueryTasksAssignedAsTaskInitiator : {
                    List<TaskSummary> results = taskSession.getTasksAssignedAsTaskInitiator( (String) cmd.getArguments().get( 0 ),
                                                                                             (String) cmd.getArguments().get( 1 ) );
                    List args = new ArrayList( 1 );
                    args.add( results );
                    Command resultsCmnd = new Command( cmd.getId(),
                                                       CommandName.QueryTaskSummaryResponse,
                                                       args );
                    session.write( resultsCmnd );
                    break;
                }
                case QueryTasksAssignedAsExcludedOwner : {
                    List<TaskSummary> results = taskSession.getTasksAssignedAsExcludedOwner( (String) cmd.getArguments().get( 0 ),
                                                                                             (String) cmd.getArguments().get( 1 ) );
                    List args = new ArrayList( 1 );
                    args.add( results );
                    Command resultsCmnd = new Command( cmd.getId(),
                                                       CommandName.QueryTaskSummaryResponse,
                                                       args );
                    session.write( resultsCmnd );
                    break;
                }
                case QueryTasksAssignedAsRecipient : {
                    List<TaskSummary> results = taskSession.getTasksAssignedAsRecipient( (String) cmd.getArguments().get( 0 ),
                                                                                         (String) cmd.getArguments().get( 1 ) );
                    List args = new ArrayList( 1 );
                    args.add( results );
                    Command resultsCmnd = new Command( cmd.getId(),
                                                       CommandName.QueryTaskSummaryResponse,
                                                       args );
                    session.write( resultsCmnd );
                    break;
                }
                case QueryTasksAssignedAsTaskStakeholder : {
                    List<TaskSummary> results = taskSession.getTasksAssignedAsTaskStakeholder( (String) cmd.getArguments().get( 0 ),
                                                                                               (String) cmd.getArguments().get( 1 ) );
                    List args = new ArrayList( 1 );
                    args.add( results );
                    Command resultsCmnd = new Command( cmd.getId(),
                                                       CommandName.QueryTaskSummaryResponse,
                                                       args );
                    session.write( resultsCmnd );
                    break;
                }
                case RegisterForEventRequest : {
                    EventKey key = (EventKey) cmd.getArguments().get( 0 );
                    boolean remove = (Boolean) cmd.getArguments().get( 1 );
                    String uuid = (String) cmd.getArguments().get( 2 );
                    clients.put( uuid,
                                 session );
                    MinaEventTransport transport = new MinaEventTransport( uuid,
                                                                           cmd.getId(),
                                                                           clients,
                                                                           remove );
                    service.getEventKeys().register( key,
                                                     transport );
                    break;
                }
                case RegisterClient : {
                    String uuid = (String) cmd.getArguments().get( 0 );
                    clients.put( uuid,
                                 session );
                    break;
                }
                default : {
                    // unknown command
                }
            }

            Date date = new Date();
            System.out.println( "Message written : " + cmd.getName() + " : " + date );
        } finally {
            taskSession.dispose();
        }
    }

    @Override
    public void sessionIdle(IoSession session,
                            IdleStatus status) throws Exception {
        System.out.println( "IDLE " + session.getIdleCount( status ) );
    }
}