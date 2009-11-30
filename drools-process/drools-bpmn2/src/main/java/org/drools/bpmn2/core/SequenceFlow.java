package org.drools.bpmn2.core;

import java.io.Serializable;

public class SequenceFlow implements Serializable {

	private static final long serialVersionUID = 4L;
	
	private String sourceRef;
	private String targetRef;
	private String bendpoints;
	private String expression;
	private String type;
	private String language;
	private String name;
	
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
