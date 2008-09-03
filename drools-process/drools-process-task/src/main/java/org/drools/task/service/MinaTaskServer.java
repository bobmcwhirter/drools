package org.drools.task.service;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MinaTaskServer
    implements
    Runnable {
    private static final int PORT = 9123;

    TaskServerHandler        handler;

    IoAcceptor               acceptor;

    volatile boolean         running;

    public MinaTaskServer(TaskService service) {
        handler = new TaskServerHandler( service );
    }

    public void run() {
        try {
            start();
            while ( running ) {
                Thread.sleep( 100 );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( "Task Service Exception: ",
                                        e );
        }
    }

    public void start() throws IOException {
        running = true;

        acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast( "logger",
                                           new LoggingFilter() );
        acceptor.getFilterChain().addLast( "codec",
                                           new ProtocolCodecFilter( new ObjectSerializationCodecFactory() ) );

        acceptor.setHandler( handler );
        acceptor.getSessionConfig().setReadBufferSize( 2048 );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE,
                                                 10 );
        acceptor.bind( new InetSocketAddress( PORT ) );
    }

    public void stop() {
        acceptor.dispose();
        running = false;
    }

    public static void main(String[] args) throws IOException {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.drools.task");
        TaskService taskService = new TaskService(emf);
        MinaTaskServer server = new MinaTaskServer(taskService);
        Thread thread = new Thread(server);
        thread.start();
    }
}