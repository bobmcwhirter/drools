package org.drools.jpdl.instance;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.process.instance.AbstractProcessInstanceFactory;
import org.drools.process.instance.ProcessInstance;

public class JpdlProcessInstanceFactory extends AbstractProcessInstanceFactory implements Externalizable {

    private static final long serialVersionUID = 4L;

    public ProcessInstance createProcessInstance() {
        return new JpdlProcessInstance();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

}
