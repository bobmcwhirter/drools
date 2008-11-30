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

}
