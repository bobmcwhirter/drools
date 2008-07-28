package org.drools.bpel.core;

import org.drools.process.core.event.EventFilter;
import org.drools.process.core.timer.Timer;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.node.CompositeNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.EventNode;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.TimerNode;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELPick extends CompositeNode implements BPELActivity {

    private static final long serialVersionUID = 400L;

    private boolean createInstance;
    private Join join;
    private SourceLink[] sourceLinks;
    private TargetLink[] targetLinks;

    public BPELPick() {
    	EndNode end = new EndNode();
    	end.setTerminate(false);
    	end.setMetaData("hidden", true);
    	addNode(end);
        join = new Join();
        join.setType(Join.TYPE_XOR);
        join.setMetaData("hidden", true);
        addNode(join);
        linkIncomingConnections(
            Node.CONNECTION_DEFAULT_TYPE,
            new CompositeNode.NodeAndType(
                end, Node.CONNECTION_DEFAULT_TYPE));
        linkOutgoingConnections(
            new CompositeNode.NodeAndType(
                join, Node.CONNECTION_DEFAULT_TYPE),
            Node.CONNECTION_DEFAULT_TYPE);
    }
    
    public boolean isCreateInstance() {
		return createInstance;
	}

	public void setCreateInstance(boolean createInstance) {
		this.createInstance = createInstance;
	}

	public void addOnMessage(OnMessage onMessage) {
    	EventNode eventNode = new EventNode();
    	eventNode.addEventFilter(new OnMessageEventFilter(
			onMessage.getPartnerLink(), onMessage.getPortType(), onMessage.getOperation()));
    	eventNode.setMetaData("hidden", true);
    	addNode(eventNode);
    	addNode(onMessage.getActivity());
        new ConnectionImpl(
            eventNode, Node.CONNECTION_DEFAULT_TYPE,
            onMessage.getActivity(), Node.CONNECTION_DEFAULT_TYPE);
        new ConnectionImpl(
    		onMessage.getActivity(), Node.CONNECTION_DEFAULT_TYPE,
            join, Node.CONNECTION_DEFAULT_TYPE);
    }
    
	public void addOnAlarm(OnAlarm onAlarm) {
		TimerNode timerNode = new TimerNode();
		Timer timer = new Timer();
		if (onAlarm.getForExpression() != null) {
			timer.setDelay(getDelayFor(onAlarm.getForExpression()));
		} else {
			timer.setDelay(getDelayUntil(onAlarm.getUntilExpression()));
		}
		timerNode.setTimer(timer);
		addNode(timerNode);
    	addNode(onAlarm.getActivity());
        new ConnectionImpl(
            timerNode, Node.CONNECTION_DEFAULT_TYPE,
            onAlarm.getActivity(), Node.CONNECTION_DEFAULT_TYPE);
        new ConnectionImpl(
    		onAlarm.getActivity(), Node.CONNECTION_DEFAULT_TYPE,
            join, Node.CONNECTION_DEFAULT_TYPE);
    }
	
	private int getDelayFor(String forExpression) {
		// TODO: BPELPick timer delay
		return 1000;
	}
    
	private int getDelayUntil(String untilExpression) {
		// TODO: BPELPick timer until
		return 1000;
	}
    
    public SourceLink[] getSourceLinks() {
        return sourceLinks;
    }

    public void setSourceLinks(SourceLink[] sourceLinks) {
        this.sourceLinks = sourceLinks;
    }

    public TargetLink[] getTargetLinks() {
        return targetLinks;
    }

    public void setTargetLinks(TargetLink[] targetLinks) {
        this.targetLinks = targetLinks;
    }
    
    public class OnMessage {
    	
    	private String partnerLink;
    	private String portType;
    	private String operation;
    	private String variable;
    	private BPELCorrelation[] correlations;
    	private BPELActivity activity;
    	
		public String getPartnerLink() {
			return partnerLink;
		}
		
		public void setPartnerLink(String partnerLink) {
			this.partnerLink = partnerLink;
		}
		
		public String getPortType() {
			return portType;
		}
		
		public void setPortType(String portType) {
			this.portType = portType;
		}
		
		public String getOperation() {
			return operation;
		}
		
		public void setOperation(String operation) {
			this.operation = operation;
		}
		
		public String getVariable() {
			return variable;
		}
		
		public void setVariable(String variable) {
			this.variable = variable;
		}
		
		public BPELCorrelation[] getCorrelations() {
			return correlations;
		}
		
		public void setCorrelations(BPELCorrelation[] correlations) {
			this.correlations = correlations;
		}
    	
		public BPELActivity getActivity() {
			return activity;
		}
		
		public void setActivity(BPELActivity activity) {
			this.activity = activity;
		}
    	
    }
    
    private class OnMessageEventFilter implements EventFilter {
    	
    	private String partnerLink;
    	private String portType;
    	private String operation;
    	
    	public OnMessageEventFilter(String partnerLink, String portType, String operation) {
    		this.partnerLink = partnerLink;
    		this.portType = portType;
    		this.operation = operation;
    	}

		public boolean acceptsEvent(String type, Object event) {
			if ("message".equals(type)) {
				String[] message = (String[]) event;
				return partnerLink.equals(message[0]) && portType.equals(message[1]) && operation.equals(message[2]);
			}
			return false;
		}
    	
    }
    
    public class OnAlarm {
    	
    	private String forExpression;
    	private String untilExpression;
    	private BPELActivity activity;
    	
		public String getForExpression() {
			return forExpression;
		}
		
		public void setForExpression(String forExpression) {
			this.forExpression = forExpression;
		}
		
		public String getUntilExpression() {
			return untilExpression;
		}
		
		public void setUntilExpression(String untilExpression) {
			this.untilExpression = untilExpression;
		}
		
		public BPELActivity getActivity() {
			return activity;
		}
		
		public void setActivity(BPELActivity activity) {
			this.activity = activity;
		}
    	
    }

}
