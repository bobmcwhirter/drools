package org.drools.osgi.bpmn2;

import java.util.Hashtable;

import org.drools.Service;
import org.drools.bpmn2.BPMN2ProcessProviderImpl;
import org.drools.compiler.BPMN2ProcessProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator
    implements
    BundleActivator {
    private ServiceRegistration bpmn2ProcessReg;

    public void start(BundleContext bc) throws Exception {
        System.out.println( "registering BPMN2 process drools services" );
        this.bpmn2ProcessReg = bc.registerService( new String[]{ BPMN2ProcessProvider.class.getName(), Service.class.getName()},
                                                   new BPMN2ProcessProviderImpl(),
                                                   new Hashtable() );

        System.out.println( "drools BPMN2 process services registered" );
    }

    public void stop(BundleContext bc) throws Exception {
        this.bpmn2ProcessReg.unregister();
    }

}
