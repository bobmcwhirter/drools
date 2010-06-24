package org.drools.process.audit;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryLogger;
import org.drools.audit.event.LogEvent;
import org.drools.audit.event.RuleFlowLogEvent;
import org.drools.audit.event.RuleFlowNodeLogEvent;
import org.drools.event.KnowledgeRuntimeEventManager;
import org.drools.impl.StatelessKnowledgeSessionImpl;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeRuntime;

/**
 * Enables history log via JPA.
 * 
 * @author jserdaru
 * @author krisv
 * 
 */
public class JPAWorkingMemoryDbLogger extends WorkingMemoryLogger {

    protected Environment env;

    public JPAWorkingMemoryDbLogger(WorkingMemory workingMemory) {
        super(workingMemory);
        env = workingMemory.getEnvironment();
    }
    
    public JPAWorkingMemoryDbLogger(KnowledgeRuntimeEventManager session) {
    	super(session);
        if (session instanceof KnowledgeRuntime) {
            env = ((KnowledgeRuntime) session).getEnvironment();
        } else if (session instanceof StatelessKnowledgeSessionImpl) {
        	env = ((StatelessKnowledgeSessionImpl) session).getEnvironment();
        } else {
            throw new IllegalArgumentException(
                "Not supported session in logger: " + session.getClass());
        }
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
        getEntityManager().persist(log);
    }

    @SuppressWarnings("unchecked")
    private void updateProcessLog(long processInstanceId) {
        List<ProcessInstanceLog> result = getEntityManager().createQuery(
            "from ProcessInstanceLog as log where log.processInstanceId = ? and log.end is null")
                .setParameter(1, processInstanceId).getResultList();
        if (result != null && result.size() != 0) {
            ProcessInstanceLog log = result.get(result.size() - 1);
            log.setEnd(new Date());
            getEntityManager().merge(log);
        }
    }

    private void addNodeEnterLog(long processInstanceId, String processId, String nodeInstanceId, String nodeId) {
        NodeInstanceLog log = new NodeInstanceLog(
    		NodeInstanceLog.TYPE_ENTER, processInstanceId, processId, nodeInstanceId, nodeId);
        getEntityManager().persist(log);
    }

    private void addNodeExitLog(long processInstanceId,
            String processId, String nodeInstanceId, String nodeId) {
        NodeInstanceLog log = new NodeInstanceLog(
            NodeInstanceLog.TYPE_EXIT, processInstanceId, processId, nodeInstanceId, nodeId);
        getEntityManager().persist(log);
    }

    public void dispose() {
    }

    protected EntityManager getEntityManager() {
        return (EntityManager) env.get(EnvironmentName.ENTITY_MANAGER);
    }

}
