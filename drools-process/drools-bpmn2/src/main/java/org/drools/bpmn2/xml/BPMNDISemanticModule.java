package org.drools.bpmn2.xml;

import org.drools.bpmn2.xml.di.BPMNEdgeHandler;
import org.drools.bpmn2.xml.di.BPMNPlaneHandler;
import org.drools.bpmn2.xml.di.BPMNShapeHandler;
import org.drools.xml.DefaultSemanticModule;

public class BPMNDISemanticModule extends DefaultSemanticModule {
	
	public BPMNDISemanticModule() {
		super("http://www.omg.org/spec/BPMN/20100524/BPMNDI");
		
		addHandler("BPMNPlane", new BPMNPlaneHandler());
		addHandler("BPMNShape", new BPMNShapeHandler());
		addHandler("BPMNEdge", new BPMNEdgeHandler());
	}

}
