/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.bpmn2.legacy.beta1;

import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.CompositeContextNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.EventNode;
import org.drools.workflow.core.node.FaultNode;
import org.drools.workflow.core.node.ForEachNode;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.StateNode;
import org.drools.workflow.core.node.TimerNode;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.DefaultSemanticModule;

public class BPMNSemanticModule extends DefaultSemanticModule {
	
	public static final String BPMN2_URI = "http://schema.omg.org/spec/BPMN/2.0";
	
	public BPMNSemanticModule() {
		super(BPMN2_URI);
		
        addHandler("definitions", new DefinitionsHandler());
        
        addHandler("process", new ProcessHandler());
        
        addHandler("property", new PropertyHandler());
        addHandler("lane", new LaneHandler());

        addHandler("startEvent", new StartEventHandler());
        addHandler("endEvent", new EndEventHandler());
        addHandler("exclusiveGateway", new ExclusiveGatewayHandler());
        addHandler("inclusiveGateway", new InclusiveGatewayHandler());
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
        addHandler("intermediateThrowEvent", new IntermediateThrowEventHandler());
        addHandler("boundaryEvent", new BoundaryEventHandler());
        addHandler("dataObject", new DataObjectHandler());
        addHandler("transaction", new TransactionHandler());

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
        handlersByClass.put(ActionNode.class, new ActionNodeHandler());
        handlersByClass.put(StateNode.class, new StateNodeHandler());
        handlersByClass.put(CompositeContextNode.class, new CompositeContextNodeHandler());
        handlersByClass.put(ForEachNode.class, new ForEachNodeHandler());
	}

}
