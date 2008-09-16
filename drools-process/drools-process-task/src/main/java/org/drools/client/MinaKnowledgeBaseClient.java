package org.drools.client;

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
import org.drools.task.service.BaseMinaClient;
import org.drools.task.service.Command;
import org.drools.task.service.CommandName;
import org.drools.task.service.TaskClientHandler;
import org.drools.task.service.TaskClientHandler.AddAttachmentResponseHandler;
import org.drools.task.service.TaskClientHandler.AddCommentResponseHandler;
import org.drools.task.service.TaskClientHandler.AddTaskResponseHandler;
import org.drools.task.service.TaskClientHandler.GetContentResponseHandler;
import org.drools.task.service.TaskClientHandler.GetTaskResponseHandler;
import org.drools.task.service.TaskClientHandler.SetDocumentResponseHandler;
import org.drools.task.service.TaskClientHandler.TaskSummaryResponseHandler;

public class MinaKnowledgeBaseClient extends BaseMinaClient {
    
    public MinaKnowledgeBaseClient(String name,
                          TaskClientHandler handler) {
        super( name,
               handler );
    }            

    public void addPackage(Task task,
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


}