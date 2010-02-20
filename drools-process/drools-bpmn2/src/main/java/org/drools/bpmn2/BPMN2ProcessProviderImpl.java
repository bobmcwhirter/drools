package org.drools.bpmn2;

import org.drools.bpmn2.xml.BPMNSemanticModule;
import org.drools.compiler.BPMN2ProcessProvider;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;

public class BPMN2ProcessProviderImpl implements BPMN2ProcessProvider {

    public void configurePackageBuilder(PackageBuilder packageBuilder) {
        PackageBuilderConfiguration conf = packageBuilder.getPackageBuilderConfiguration();
        if (conf.getSemanticModules().getSemanticModule(BPMNSemanticModule.BPMN2_URI) == null) {
        	conf.addSemanticModule(new BPMNSemanticModule());
        }
    }

}
