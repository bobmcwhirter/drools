package org.drools.process.audit;

import org.junit.Ignore;


@Ignore
public class ProcessInstanceDbLogTest {
    
    public static final void main(String[] args) {
        try {
            System.out.println("Checking process instances for process 'com.sample.ruleflow'");
            for (ProcessInstanceLog processInstance: ProcessInstanceDbLog.findProcessInstances("com.sample.ruleflow")) {
                System.out.println(processInstance);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
