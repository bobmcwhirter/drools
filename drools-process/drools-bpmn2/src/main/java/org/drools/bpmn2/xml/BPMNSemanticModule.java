package org.drools.bpmn2.xml;

import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.Split;
import org.drools.xml.DefaultSemanticModule;

public class BPMNSemanticModule extends DefaultSemanticModule {
	
	public BPMNSemanticModule() {
		super("http://schema.omg.org/spec/BPMN/2.0");
		
		addHandler("process", new ProcessHandler());
		
		addHandler("property", new PropertyHandler());

		addHandler("startEvent", new StartEventHandler());
		addHandler("endEvent", new EndEventHandler());
		addHandler("exclusiveGateway", new ExclusiveGatewayHandler());
		addHandler("parallelGateway", new ParallelGatewayHandler());
		addHandler("complexGateway", new ComplexGatewayHandler());
		addHandler("scriptTask", new ScriptTaskHandler());
		addHandler("task", new TaskHandler());
		addHandler("userTask", new UserTaskHandler());
		addHandler("businessRuleTask", new BusinessRuleTaskHandler());
		addHandler("callActivity", new CallActivityHandler());
		addHandler("subProcess", new SubProcessHandler());

		addHandler("sequenceFlow", new SequenceFlowHandler());
		
		handlersByClass.put(Split.class, new SplitHandler());
		handlersByClass.put(Join.class, new JoinHandler());
	}

}
