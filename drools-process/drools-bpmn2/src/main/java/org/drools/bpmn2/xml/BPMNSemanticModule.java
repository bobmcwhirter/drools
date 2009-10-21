package org.drools.bpmn2.xml;

import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.EventNode;
import org.drools.workflow.core.node.FaultNode;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.TimerNode;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.DefaultSemanticModule;

public class BPMNSemanticModule extends DefaultSemanticModule {
	
	public BPMNSemanticModule() {
		super("http://schema.omg.org/spec/BPMN/2.0");
		
        addHandler("process", new ProcessHandler());
        
        addHandler("property", new PropertyHandler());
        addHandler("lane", new LaneHandler());

        addHandler("startEvent", new StartEventHandler());
        addHandler("endEvent", new EndEventHandler());
        addHandler("exclusiveGateway", new ExclusiveGatewayHandler());
        addHandler("parallelGateway", new ParallelGatewayHandler());
        addHandler("complexGateway", new ComplexGatewayHandler());
        addHandler("scriptTask", new ScriptTaskHandler());
        addHandler("task", new TaskHandler());
        addHandler("userTask", new UserTaskHandler());
        addHandler("manualTask", new ManualTaskHandler());
        addHandler("serviceTask", new ServiceTaskHandler());
        addHandler("sendTask", new SendTaskHandler());
        addHandler("receiveTask", new ReceiveTaskHandler());
        addHandler("businessRuleTask", new BusinessRuleTaskHandler());
        addHandler("callActivity", new CallActivityHandler());
        addHandler("subProcess", new SubProcessHandler());
        addHandler("adHocSubProcess", new AdHocSubProcessHandler());
        addHandler("intermediateCatchEvent", new IntermediateCatchEventHandler());

        addHandler("sequenceFlow", new SequenceFlowHandler());

        addHandler("itemDefinition", new ItemDefinitionHandler());
        addHandler("message", new MessageHandler());
        addHandler("interface", new InterfaceHandler());
        addHandler("operation", new OperationHandler());
        addHandler("inMessageRef", new InMessageRefHandler());
        
        handlersByClass.put(Split.class, new SplitHandler());
        handlersByClass.put(Join.class, new JoinHandler());
        handlersByClass.put(EventNode.class, new EventNodeHandler());
        handlersByClass.put(TimerNode.class, new TimerNodeHandler());
        handlersByClass.put(EndNode.class, new EndNodeHandler());
        handlersByClass.put(FaultNode.class, new FaultNodeHandler());
        handlersByClass.put(WorkItemNode.class, new WorkItemNodeHandler());
	}

}
