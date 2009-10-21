package org.drools.bpmn2.xml;

import org.drools.bpmn2.xml.di.ActivityShapeHandler;
import org.drools.bpmn2.xml.di.EventShapeHandler;
import org.drools.bpmn2.xml.di.GatewayShapeHandler;
import org.drools.bpmn2.xml.di.ProcessDiagramHandler;
import org.drools.bpmn2.xml.di.SequenceFlowConnectorHandler;
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
