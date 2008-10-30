package org.drools.osworkflow.instance;

import org.drools.process.instance.InternalProcessInstance;
import org.drools.process.instance.ProcessInstanceFactory;

public class OSWorkflowProcessInstanceFactory implements ProcessInstanceFactory {

    public InternalProcessInstance createProcessInstance() {
        return new OSWorkflowProcessInstance();
    }

}
