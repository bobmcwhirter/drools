package org.drools.bpmn2.handler;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class ReceiveTaskHandler implements WorkItemHandler {
    
    // TODO: use correlation instead of message type
    private Map<String, Long> waiting = new HashMap<String, Long>();
    private KnowledgeRuntime ksession;
    
    public ReceiveTaskHandler(KnowledgeRuntime ksession) {
        this.ksession = ksession;
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        String messageType = (String) workItem.getParameter("MessageType");
        waiting.put(messageType, workItem.getId());
    }
    
    public void messageReceived(String messageType, Object message) {
        Long workItemId = waiting.get(messageType);
        if (workItemId == null) {
            return;
        }
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Message", message);
        ksession.getWorkItemManager().completeWorkItem(workItemId, results);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // Do nothing, cannot be aborted
    }

}
