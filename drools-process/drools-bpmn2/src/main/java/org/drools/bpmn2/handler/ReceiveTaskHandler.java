package org.drools.bpmn2.handler;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class ReceiveTaskHandler implements WorkItemHandler {
    
    // TODO: use correlation instead of message id
    private Map<String, Long> waiting = new HashMap<String, Long>();
    private KnowledgeRuntime ksession;
    
    public ReceiveTaskHandler(KnowledgeRuntime ksession) {
        this.ksession = ksession;
    }
    
    public void setKnowledgeRuntime(KnowledgeRuntime ksession) {
    	this.ksession = ksession;
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        String messageId = (String) workItem.getParameter("MessageId");
        waiting.put(messageId, workItem.getId());
    }
    
    public void messageReceived(String messageId, Object message) {
        Long workItemId = waiting.get(messageId);
        if (workItemId == null) {
            return;
        }
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Message", message);
        ksession.getWorkItemManager().completeWorkItem(workItemId, results);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    	String messageId = (String) workItem.getParameter("MessageId");
        waiting.remove(messageId);
    }

}
