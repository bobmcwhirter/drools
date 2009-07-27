package org.drools.bpmn2.core;

public class SequenceFlow {

	private String sourceRef;
	private String targetRef;
	
	public SequenceFlow(String sourceRef, String targetRef) {
		this.sourceRef = sourceRef;
		this.targetRef = targetRef;
	}
	
	public String getSourceRef() {
		return sourceRef;
	}
	
	public String getTargetRef() {
		return targetRef;
	}
	
}
