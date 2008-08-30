package org.drools.task.service;

import javax.persistence.EntityManager;

import org.drools.task.Deadline;
import org.drools.task.Escalation;
import org.drools.task.Notification;
import org.drools.task.Reassignment;
import org.drools.task.Task;

public class DefaultEscalatedDeadlineHandler implements EscalatedDeadlineHandler {

    public void executeEscalatedDeadline(Task task,
                                         Deadline deadline,
                                         EntityManager em) {
        
        for ( Escalation escalation : deadline.getEscalations() ) {
            // we won't impl constraints for now
            //escalation.getConstraints()

            for ( Notification notification : escalation.getNotifications() ) {
                //notification.g
            }

            for ( Reassignment reassignment : escalation.getReassignments() ) {
                //reassignment.g
            }
        }
        em.getTransaction().begin();
        deadline.setEscalated( true );
        em.getTransaction().commit();
    }

}
