package org.drools.bpmn2.core;

public class SequenceFlow {

	private String sourceRef;
	private String targetRef;
	private String bendpoints;
	private String expression;
	
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

	public String getBendpoints() {
		return bendpoints;
	}

	public void setBendpoints(String bendpoints) {
		this.bendpoints = bendpoints;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
}
