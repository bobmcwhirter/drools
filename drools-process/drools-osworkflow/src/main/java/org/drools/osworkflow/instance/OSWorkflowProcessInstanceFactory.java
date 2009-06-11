package org.drools.osworkflow.instance;

import org.drools.process.instance.AbstractProcessInstanceFactory;
import org.drools.process.instance.ProcessInstance;

public class OSWorkflowProcessInstanceFactory extends AbstractProcessInstanceFactory {

    public ProcessInstance createProcessInstance() {
        return new OSWorkflowProcessInstance();
    }

}
