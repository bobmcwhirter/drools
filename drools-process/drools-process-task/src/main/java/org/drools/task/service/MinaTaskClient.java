package org.drools.task.service;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.codec.serialization.ObjectSerializationEncoder;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.eventmessaging.EventKey;
import org.drools.eventmessaging.EventResponseHandler;
import org.drools.task.AccessType;
import org.drools.task.Attachment;
import org.drools.task.Content;
import org.drools.task.Comment;
import org.drools.task.Task;
import org.drools.task.User;
import org.drools.task.service.TaskClientHandler.AddAttachmentResponseHandler;
import org.drools.task.service.TaskClientHandler.AddCommentResponseHandler;
import org.drools.task.service.TaskClientHandler.AddTaskResponseHandler;
import org.drools.task.service.TaskClientHandler.GetContentResponseHandler;
import org.drools.task.service.TaskClientHandler.GetTaskResponseHandler;
import org.drools.task.service.TaskClientHandler.SetDocumentResponseHandler;
import org.drools.task.service.TaskClientHandler.TaskSummaryResponseHandler;

public class MinaTaskClient extends BaseMinaClient {

    public MinaTaskClient(String name,
                          TaskClientHandler handler) {
        super( name,
               handler );
    }

    public void addTask(Task task,
                        AddTaskResponseHandler responseHandler) {
        List args = new ArrayList( 1 );
        args.add( task );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.AddTaskRequest,
                                   args );

        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        session.write( cmd );
    }

    public void getTask(long taskId,
                        GetTaskResponseHandler responseHandler) {
        List args = new ArrayList( 1 );
        args.add( taskId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.GetTaskRequest,
                                   args );

        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        session.write( cmd );

    }

    public void addComment(long taskId,
                           Comment comment,
                           AddCommentResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( taskId );
        args.add( comment );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.AddCommentRequest,
                                   args );

        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        session.write( cmd );
    }

    public void deleteComment(long taskId,
                              long commentId) {
        List args = new ArrayList( 2 );
        args.add( taskId );
        args.add( commentId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.DeleteCommentRequest,
                                   args );

        session.write( cmd );
    }

    public void addAttachment(long taskId,
                              Attachment attachment,
                              Content content,
                              AddAttachmentResponseHandler responseHandler) {
        List args = new ArrayList( 3 );
        args.add( taskId );
        args.add( attachment );
        args.add( content );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.AddAttachmentRequest,
                                   args );

        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        session.write( cmd );
    }

    public void deleteAttachment(long taskId,
                                 long attachmentId,
                                 long contentId) {
        List args = new ArrayList( 3 );
        args.add( taskId );
        args.add( attachmentId );
        args.add( contentId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.DeleteAttachmentRequest,
                                   args );

        session.write( cmd );
    }

    public void setDocumentContent(long taskId,
                                   Content content,
                                   SetDocumentResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( taskId );
        args.add( content );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.SetDocumentContentRequest,
                                   args );

        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        session.write( cmd );
    }

    public void getContent(long contentId,
                           GetContentResponseHandler responseHandler) {
        List args = new ArrayList( 1 );
        args.add( contentId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.GetContentRequest,
                                   args );

        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );

        session.write( cmd );
    }

    public void claim(long taskId,
                      String userId) {
        List args = new ArrayList( 1 );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.ClaimRequest,
                                   args );

        session.write( cmd );
    }

    public void start(long taskId,
                      String userId) {
        List args = new ArrayList( 1 );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.StartRequest,
                                   args );

        session.write( cmd );
    }

    public void stop(long taskId,
                     String userId) {
        List args = new ArrayList( 1 );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.StopRequest,
                                   args );

        session.write( cmd );
    }

    public void release(long taskId,
                        String userId) {
        List args = new ArrayList( 1 );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.ReleaseRequest,
                                   args );

        session.write( cmd );
    }

    public void suspend(long taskId,
                        String userId) {
        List args = new ArrayList( 1 );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.SuspendRequest,
                                   args );

        session.write( cmd );
    }

    public void resume(long taskId,
                       String userId) {
        List args = new ArrayList( 1 );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.ResumeRequest,
                                   args );

        session.write( cmd );
    }

    public void skip(long taskId,
                     String userId) {
        List args = new ArrayList( 1 );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.SkipRequest,
                                   args );

        session.write( cmd );
    }

    public void complete(long taskId,
                         String userId) {
        List args = new ArrayList( 1 );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.CompleteRequest,
                                   args );

        session.write( cmd );
    }

    public void fail(long taskId,
                     String userId) {
        List args = new ArrayList( 1 );
        args.add( taskId );
        args.add( userId );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.FailRequest,
                                   args );

        session.write( cmd );
    }

    public void getTasksOwned(String userId,
                              String language,
                              TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksOwned,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        session.write( cmd );
    }

    public void getTasksAssignedAsBusinessAdministrator(String userId,
                                                        String language,
                                                        TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsBusinessAdministrator,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        session.write( cmd );
    }

    public void getTasksAssignedAsExcludedOwner(String userId,
                                                String language,
                                                TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsExcludedOwner,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        session.write( cmd );
    }

    public void getTasksAssignedAsPotentialOwner(String userId,
                                                 String language,
                                                 TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsPotentialOwner,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        session.write( cmd );
    }

    public void getTasksAssignedAsRecipient(String userId,
                                            String language,
                                            TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsRecipient,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        session.write( cmd );
    }

    public void getTasksAssignedAsTaskInitiator(String userId,
                                                String language,
                                                TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsTaskInitiator,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        session.write( cmd );
    }

    public void getTasksAssignedAsTaskStakeholder(String userId,
                                                  String language,
                                                  TaskSummaryResponseHandler responseHandler) {
        List args = new ArrayList( 2 );
        args.add( userId );
        args.add( language );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.QueryTasksAssignedAsTaskStakeholder,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        session.write( cmd );
    }

    public void registerForEvent(EventKey key,
                                 boolean remove,
                                 EventResponseHandler responseHandler) {
        List args = new ArrayList( 3 );
        args.add( key );
        args.add( remove );
        args.add( this.name );
        Command cmd = new Command( counter.getAndIncrement(),
                                   CommandName.RegisterForEventRequest,
                                   args );
        handler.addResponseHandler( cmd.getId(),
                                    responseHandler );
        session.write( cmd );
    }

}