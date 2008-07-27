package org.drools.bpel.compiler;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.ode.bpel.compiler.bom.Activity;
import org.apache.ode.bpel.compiler.bom.AssignActivity;
import org.apache.ode.bpel.compiler.bom.BpelObjectFactory;
import org.apache.ode.bpel.compiler.bom.Catch;
import org.apache.ode.bpel.compiler.bom.EmptyActivity;
import org.apache.ode.bpel.compiler.bom.FlowActivity;
import org.apache.ode.bpel.compiler.bom.InvokeActivity;
import org.apache.ode.bpel.compiler.bom.Link;
import org.apache.ode.bpel.compiler.bom.LinkSource;
import org.apache.ode.bpel.compiler.bom.LinkTarget;
import org.apache.ode.bpel.compiler.bom.OnAlarm;
import org.apache.ode.bpel.compiler.bom.OnMessage;
import org.apache.ode.bpel.compiler.bom.PickActivity;
import org.apache.ode.bpel.compiler.bom.Process;
import org.apache.ode.bpel.compiler.bom.ReceiveActivity;
import org.apache.ode.bpel.compiler.bom.ReplyActivity;
import org.apache.ode.bpel.compiler.bom.ScopeActivity;
import org.apache.ode.bpel.compiler.bom.SequenceActivity;
import org.apache.ode.bpel.compiler.bom.SwitchActivity;
import org.apache.ode.bpel.compiler.bom.ThrowActivity;
import org.apache.ode.bpel.compiler.bom.WaitActivity;
import org.apache.ode.bpel.compiler.bom.WhileActivity;
import org.apache.ode.utils.StreamUtils;
import org.drools.RuleBase;
import org.drools.bpel.core.BPELActivity;
import org.drools.bpel.core.BPELAssign;
import org.drools.bpel.core.BPELEmpty;
import org.drools.bpel.core.BPELFaultHandler;
import org.drools.bpel.core.BPELFlow;
import org.drools.bpel.core.BPELInvoke;
import org.drools.bpel.core.BPELPick;
import org.drools.bpel.core.BPELProcess;
import org.drools.bpel.core.BPELReceive;
import org.drools.bpel.core.BPELReply;
import org.drools.bpel.core.BPELScope;
import org.drools.bpel.core.BPELSequence;
import org.drools.bpel.core.BPELSwitch;
import org.drools.bpel.core.BPELThrow;
import org.drools.bpel.core.BPELWait;
import org.drools.bpel.core.BPELWhile;
import org.drools.bpel.core.BPELActivity.SourceLink;
import org.drools.bpel.core.BPELActivity.TargetLink;
import org.drools.common.AbstractRuleBase;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.ProcessBuilder;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.xml.sax.InputSource;

public class BPELCompiler {
	
	public void loadProcess(RuleBase ruleBase, String fileName) {
		try {
			BPELCompiler compiler = new BPELCompiler();
			BPELProcess process = compiler.compileProcess(
				BPELCompiler.class.getResource(fileName));
			PackageBuilder packageBuilder = new PackageBuilder();
			ProcessBuilder processBuilder = new ProcessBuilder(packageBuilder);
			processBuilder.buildProcess(process);
	        if (!processBuilder.getErrors().isEmpty()) {
	        	for (DroolsError error: processBuilder.getErrors()) {
	        		System.err.println(error);
	        	}
	        	throw new IllegalArgumentException("Could not build process " + fileName);
	        }
	        ((AbstractRuleBase) ruleBase).addProcess(process);
		} catch (Throwable t) {
			throw new IllegalArgumentException("Could not load process " + fileName, t);
		}
	}

	public BPELProcess compileProcess(URL bpelFile) throws Exception {
		InputSource isrc = new InputSource(new ByteArrayInputStream(StreamUtils.read(bpelFile)));
		Process process = BpelObjectFactory.getInstance().parse(isrc, bpelFile.toURI());
		return compileProcess(process);
	}
	
	private BPELProcess compileProcess(Process process) {
		BPELProcess result = new BPELProcess();
		result.setName(process.getName());
		result.setId(process.getTargetNamespace());
		result.setPackageName("org.drools.bpel");
		result.setVersion("1.0");
		BPELActivity activity = compileActivity(process.getRootActivity());
		result.setActivity(activity);
		// variables
		VariableScope variableScope = result.getVariableScope();
        List<Variable> variables = new ArrayList<Variable>();
		for (org.apache.ode.bpel.compiler.bom.Variable variable: process.getVariables()) {
	        Variable bpelVariable =	new Variable();
	        bpelVariable.setName(variable.getName());
	        bpelVariable.setType(new StringDataType());
	        variables.add(bpelVariable);
		}
		variableScope.setVariables(variables);
		// fault handlers
		if (process.getFaultHandler() != null) {
	        List<BPELFaultHandler> faultHandlers = new ArrayList<BPELFaultHandler>();
	        for (Catch catcher: process.getFaultHandler().getCatches()) {
	            BPELFaultHandler faultHandler = new BPELFaultHandler();
	            faultHandler.setFaultName(catcher.getFaultName().toString());
	            faultHandler.setFaultVariable(catcher.getFaultVariable());
	            faultHandler.setActivity(compileActivity(catcher.getActivity()));
	            faultHandlers.add(faultHandler);
	        }
	        result.setFaultHandlers(faultHandlers);
		}
		return result;
	}
	
	private BPELActivity compileActivity(Activity activity) {
		BPELActivity result = null;
		if (activity instanceof ReceiveActivity) {
			ReceiveActivity receiveActivity = (ReceiveActivity) activity;
			BPELReceive receive = new BPELReceive();
			receive.setCreateInstance(receiveActivity.isCreateInstance());
			receive.setPartnerLink(receiveActivity.getPartnerLink());
			receive.setPortType(receiveActivity.getPortType().toString());
			receive.setOperation(receiveActivity.getOperation());
			receive.setVariable(receiveActivity.getVariable());
			result = receive;
		} else if (activity instanceof ReplyActivity) {
			ReplyActivity replyActivity = (ReplyActivity) activity;
			BPELReply reply = new BPELReply();
			reply.setPartnerLink(replyActivity.getPartnerLink());
			reply.setPortType(replyActivity.getPortType().toString());
			reply.setOperation(replyActivity.getOperation());
			reply.setVariable(replyActivity.getVariable());
			if (replyActivity.getFaultName() != null) {
				reply.setFaultName(replyActivity.getFaultName().toString());
			}
			result = reply;
		} else if (activity instanceof InvokeActivity) {
			InvokeActivity invokeActivity = (InvokeActivity) activity;
			BPELInvoke invoke = new BPELInvoke();
			invoke.setPartnerLink(invokeActivity.getPartnerLink());
			invoke.setPortType(invokeActivity.getPortType().toString());
			invoke.setOperation(invokeActivity.getOperation());
			invoke.setInputVariable(invokeActivity.getInputVar());
			if (invokeActivity.getOutputVar() != null) { 
				invoke.setOutputVariable(invokeActivity.getOutputVar());
			}
			result = invoke;
		} else if (activity instanceof SequenceActivity) {
			SequenceActivity sequenceActivity = (SequenceActivity) activity;
			BPELSequence sequence = new BPELSequence();
			List<BPELActivity> subActivities = new ArrayList<BPELActivity>();
			for (Activity subActivity: sequenceActivity.getActivities()) {
				subActivities.add(compileActivity(subActivity));
			}
			sequence.setActivities(subActivities);
			result = sequence;
		} else if (activity instanceof FlowActivity) {
			FlowActivity flowActivity = (FlowActivity) activity;
			BPELFlow flow = new BPELFlow();
			List<BPELActivity> subActivities = new ArrayList<BPELActivity>();
			for (Activity subActivity: flowActivity.getActivities()) {
				subActivities.add(compileActivity(subActivity));
			}
			flow.setActivities(subActivities);
			String[] links = new String[flowActivity.getLinks().size()];
			int i = 0;
			for (Link link: flowActivity.getLinks()) {
				links[i++] = link.getLinkName();
			}
			flow.setLinks(links);
			result = flow;
		} else if (activity instanceof AssignActivity) {
			AssignActivity assignActivity = (AssignActivity) activity;
			BPELAssign assign = new BPELAssign();
			// TODO assign copy
			assign.setAction("");
			result = assign;
		} else if (activity instanceof EmptyActivity) {
			result = new BPELEmpty();
		} else if (activity instanceof ThrowActivity) {
			ThrowActivity throwActivity = (ThrowActivity) activity;
			BPELThrow bpelThrow = new BPELThrow();
			bpelThrow.setFaultName(throwActivity.getFaultName().toString());
			bpelThrow.setFaultVariable(throwActivity.getFaultVariable());
			result = bpelThrow;
		} else if (activity instanceof PickActivity) {
			PickActivity pickActivity = (PickActivity) activity;
			BPELPick pick = new BPELPick();
			pick.setCreateInstance(pickActivity.isCreateInstance());
			for (OnMessage onMessage: pickActivity.getOnMessages()) {
				BPELPick.OnMessage bpelOnMessage = pick.new OnMessage();
				bpelOnMessage.setPartnerLink(onMessage.getPartnerLink());
				bpelOnMessage.setPortType(onMessage.getPortType().toString());
				bpelOnMessage.setOperation(onMessage.getOperation());
				bpelOnMessage.setVariable(onMessage.getVariable());
				bpelOnMessage.setActivity(compileActivity(onMessage.getActivity()));
				pick.addOnMessage(bpelOnMessage);
			}
			for (OnAlarm onAlarm: pickActivity.getOnAlarms()) {
				BPELPick.OnAlarm bpelOnAlarm = pick.new OnAlarm();
				if (onAlarm.getFor() != null) {
					bpelOnAlarm.setForExpression(onAlarm.getFor().toString());
				}
				if (onAlarm.getUntil() != null) {
					bpelOnAlarm.setUntilExpression(onAlarm.getUntil().toString());
				}
				pick.addOnAlarm(bpelOnAlarm);
			}
			result = pick;
		} else if (activity instanceof ScopeActivity) {
			ScopeActivity scopeActivity = (ScopeActivity) activity;
			BPELScope scope = new BPELScope();
			VariableScope variableScope = scope.getVariableScope();
	        List<Variable> variables = new ArrayList<Variable>();
			// TODO
//			for (org.apache.ode.bpel.compiler.bom.Variable variable: scopeActivity.getVariables()) {
//		        Variable bpelVariable =	new Variable();
//		        bpelVariable.setName(variable.getName());
//		        bpelVariable.setType(new StringDataType());
//		        variables.add(bpelVariable);
//			}
			variableScope.setVariables(variables);
			result = scope;
		} else if (activity instanceof SwitchActivity) {
			SwitchActivity switchActivity = (SwitchActivity) activity;
			BPELSwitch bpelSwitch = new BPELSwitch();
			for (SwitchActivity.Case bpelCase: switchActivity.getCases()) {
				bpelSwitch.addCase(bpelCase.getCondition().toString(), compileActivity(bpelCase.getActivity()));
			}
			result = bpelSwitch;
		} else if (activity instanceof WaitActivity) {
			WaitActivity waitActivity = (WaitActivity) activity;
			BPELWait wait = new BPELWait();
			if (waitActivity.getFor() != null) {
				wait.setForExpression(waitActivity.getFor().toString());
			}
			if (waitActivity.getUntil() != null) {
				wait.setUntilExpression(waitActivity.getUntil().toString());
			}
			result = wait;
		} else if (activity instanceof WhileActivity) {
			WhileActivity whileActivity = (WhileActivity) activity;
			BPELWhile bpelWhile = new BPELWhile();
			bpelWhile.setActivity(
				whileActivity.getCondition().toString(),
				compileActivity(whileActivity.getActivity()));
			result = bpelWhile;
		} else {
			throw new IllegalArgumentException("Unknown activity type " + activity.getClass());
		}
		result.setName(activity.getName());
		SourceLink[] sourceLinks = new SourceLink[activity.getLinkSources().size()];
		int i = 0;
		for (LinkSource linkSource: activity.getLinkSources()) {
			sourceLinks[i] = new SourceLink(linkSource.getLinkName());
			if (linkSource.getTransitionCondition() != null) {
				sourceLinks[i].setTransitionCondition(linkSource.getTransitionCondition().getTextValue());
			}
			i++;
		}
		result.setSourceLinks(sourceLinks);
		TargetLink[] targetLinks = new TargetLink[activity.getLinkTargets().size()];
		i = 0;
		for (LinkTarget linkTarget: activity.getLinkTargets()) {
			targetLinks[i++] = new TargetLink(linkTarget.getLinkName());
		}
		result.setTargetLinks(targetLinks);
		return result;
	}

}
