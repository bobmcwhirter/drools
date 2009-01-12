package org.drools.process.audit;

import java.util.List;

import org.hibernate.Session;

public class ProcessInstanceDbLog {
    
    @SuppressWarnings("unchecked")
	public static List<ProcessInstanceLog> findProcessInstances() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<ProcessInstanceLog> result = session.createQuery("from ProcessInstanceLog").list();
        session.getTransaction().commit();
        return result;
    }

    @SuppressWarnings("unchecked")
	public static List<ProcessInstanceLog> findProcessInstances(String processId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<ProcessInstanceLog> result = session.createQuery(
            "from ProcessInstanceLog as log where log.processId = ?")
                .setString(0, processId).list();
        session.getTransaction().commit();
        return result;
    }

    @SuppressWarnings("unchecked")
	public static ProcessInstanceLog findProcessInstance(long processInstanceId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<ProcessInstanceLog> result = session.createQuery(
            "from ProcessInstanceLog as log where log.processInstanceId = ?")
                .setLong(0, processInstanceId).list();
        session.getTransaction().commit();
        return result == null || result.size() == 0 ? null : result.get(0);
    }

    @SuppressWarnings("unchecked")
	public static List<NodeInstanceLog> findNodeInstances(long processInstanceId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<NodeInstanceLog> result = session.createQuery(
            "from NodeInstanceLog as log where log.processInstanceId = ?")
                .setLong(0, processInstanceId).list();
        session.getTransaction().commit();
        return result;
    }

    @SuppressWarnings("unchecked")
	public static List<NodeInstanceLog> findNodeInstances(long processInstanceId, String nodeId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<NodeInstanceLog> result = session.createQuery(
            "from NodeInstanceLog as log where log.processInstanceId = ? and log.nodeId = ?")
                .setLong(0, processInstanceId)
                .setString(1, nodeId).list();
        session.getTransaction().commit();
        return result;
    }

	@SuppressWarnings("unchecked")
	public static void clear() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<ProcessInstanceLog> processInstances =
        	session.createQuery("from ProcessInstanceLog").list();
        for (ProcessInstanceLog processInstance: processInstances) {
        	session.delete(processInstance);
        }
        List<NodeInstanceLog> nodeInstances =
        	session.createQuery("from NodeInstanceLog").list();
        for (NodeInstanceLog nodeInstance: nodeInstances) {
        	session.delete(nodeInstance);
        }
        session.getTransaction().commit();
    }

}
