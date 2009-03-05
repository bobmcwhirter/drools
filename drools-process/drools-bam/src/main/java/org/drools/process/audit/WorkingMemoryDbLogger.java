package org.drools.process.audit;

import java.util.Date;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryLogger;
import org.drools.audit.event.LogEvent;
import org.drools.audit.event.RuleFlowLogEvent;
import org.drools.audit.event.RuleFlowNodeLogEvent;
import org.drools.event.KnowledgeRuntimeEventManager;
import org.hibernate.Session;

public class WorkingMemoryDbLogger extends WorkingMemoryLogger {
    
    public WorkingMemoryDbLogger(WorkingMemory workingMemory) {
        super(workingMemory);
    }
    
    public WorkingMemoryDbLogger(KnowledgeRuntimeEventManager session) {
    	super(session);
    }

    public void logEventCreated(LogEvent logEvent) {
        switch (logEvent.getType()) {
            case LogEvent.BEFORE_RULEFLOW_CREATED:
                RuleFlowLogEvent processEvent = (RuleFlowLogEvent) logEvent;
                addProcessLog(processEvent.getProcessInstanceId(), processEvent.getProcessId());
                break;
            case LogEvent.AFTER_RULEFLOW_COMPLETED:
            	processEvent = (RuleFlowLogEvent) logEvent;
                updateProcessLog(processEvent.getProcessInstanceId());
                break;
            case LogEvent.BEFORE_RULEFLOW_NODE_TRIGGERED:
            	RuleFlowNodeLogEvent nodeEvent = (RuleFlowNodeLogEvent) logEvent;
            	addNodeEnterLog(nodeEvent.getProcessInstanceId(), nodeEvent.getProcessId(), nodeEvent.getNodeInstanceId(), nodeEvent.getNodeId());
                break;
            case LogEvent.BEFORE_RULEFLOW_NODE_EXITED:
            	nodeEvent = (RuleFlowNodeLogEvent) logEvent;
            	addNodeExitLog(nodeEvent.getProcessInstanceId(), nodeEvent.getProcessId(), nodeEvent.getNodeInstanceId(), nodeEvent.getNodeId());
                break;
            default:
                // ignore all other events
        }
    }

    private void addProcessLog(long processInstanceId, String processId) {
        ProcessInstanceLog log = new ProcessInstanceLog(processInstanceId, processId);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(log);
        session.getTransaction().commit();
    }
    
    private void updateProcessLog(long processInstanceId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<ProcessInstanceLog> result = session.createQuery(
        "from ProcessInstanceLog as log where log.processInstanceId = ? and log.end is null")
            .setLong(0, processInstanceId).list();
        if (result != null && result.size() != 0) {
        	ProcessInstanceLog log = result.get(result.size() - 1);
	        log.setEnd(new Date());
	        session.update(log);
        }
        session.getTransaction().commit();
    }
    
    private void addNodeEnterLog(long processInstanceId, String processId, String nodeInstanceId, String nodeId) {
        NodeInstanceLog log = new NodeInstanceLog(NodeInstanceLog.TYPE_ENTER, processInstanceId, processId, nodeInstanceId, nodeId);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(log);
        session.getTransaction().commit();
    }
    
    private void addNodeExitLog(long processInstanceId, String processId, String nodeInstanceId, String nodeId) {
        NodeInstanceLog log = new NodeInstanceLog(NodeInstanceLog.TYPE_EXIT, processInstanceId, processId, nodeInstanceId, nodeId);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(log);
        session.getTransaction().commit();
    }
    
    public void dispose() {
    	HibernateUtil.getSessionFactory().getCurrentSession().close();
    	HibernateUtil.close();
    }

}
