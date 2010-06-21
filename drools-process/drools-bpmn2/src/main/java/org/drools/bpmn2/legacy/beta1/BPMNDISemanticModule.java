package org.drools.bpmn2.legacy.beta1;

import org.drools.bpmn2.legacy.beta1.di.ActivityShapeHandler;
import org.drools.bpmn2.legacy.beta1.di.EventShapeHandler;
import org.drools.bpmn2.legacy.beta1.di.GatewayShapeHandler;
import org.drools.bpmn2.legacy.beta1.di.ProcessDiagramHandler;
import org.drools.bpmn2.legacy.beta1.di.SequenceFlowConnectorHandler;
import org.drools.xml.DefaultSemanticModule;

public class BPMNDISemanticModule extends DefaultSemanticModule {
	
	public BPMNDISemanticModule() {
		super("http://bpmndi.org");
		
		addHandler("processDiagram", new ProcessDiagramHandler());
		addHandler("eventShape", new EventShapeHandler());
        addHandler("gatewayShape", new GatewayShapeHandler());
        addHandler("activityShape", new ActivityShapeHandler());
        addHandler("sequenceFlowConnector", new SequenceFlowConnectorHandler());
	}

}
