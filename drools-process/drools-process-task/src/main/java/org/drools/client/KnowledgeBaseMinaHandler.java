package org.drools.client;

import org.apache.mina.core.session.IoSession;
import org.drools.task.service.TaskClient;
import org.drools.task.service.mina.BaseMinaHandler;

public class KnowledgeBaseMinaHandler extends BaseMinaHandler {

	private TaskClient client;

    public TaskClient getClient() {
        return client;
    }

    public void setClient(TaskClient client) {
        this.client = client;
    }

    @Override
    public void exceptionCaught(IoSession session,
                                Throwable cause) throws Exception {
        //cause.printStackTrace();
        if ( !session.isConnected() ) {
            client.connect();
        }
    }

    @Override
    public void messageReceived(IoSession session,
                                Object message) throws Exception {
        KnowledgeBaseCommand cmd = (KnowledgeBaseCommand) message;
        switch ( cmd.getName() ) {
//            case  : {
//                Task task = (Task) cmd.getArguments().get( 0 );
//                AddPackageResponseHandler responseHandler = (AddPackageResponseHandler) responseHandlers.remove( cmd.getId() );
//                if ( responseHandler != null ) {
//                    responseHandler.execute( task );
//                }
//                break;
//            }
            default : {
                
            }            
        }
    }

}