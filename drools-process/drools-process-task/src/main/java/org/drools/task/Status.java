package org.drools.task;

import javax.persistence.Entity;

public enum Status {
    Created, Ready, Reserved, InProgress, Suspended, Completed, Failed, Error, Exited, Obselete;
}
