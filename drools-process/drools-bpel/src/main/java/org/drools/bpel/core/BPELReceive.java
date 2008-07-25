package org.drools.bpel.core;

import java.util.List;

import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.NodeImpl;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELReceive extends NodeImpl implements BPELActivity {

    private static final long serialVersionUID = 400L;

    private String partnerLink;
    private String portType;
    private String operation;
    private String variable;
    private boolean createInstance;
    private SourceLink[] sourceLinks;
    private TargetLink[] targetLinks;
    private BPELCorrelation[] correlations;
    
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

    public boolean isCreateInstance() {
        return createInstance;
    }
    
    public void setCreateInstance(boolean createInstance) {
        this.createInstance = createInstance;
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

    public Connection getTo() {
        final List<Connection> list =
            getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE);
        if (list.size() > 0) {
            return (Connection) list.get(0);
        }
        return null;
    }

	public BPELCorrelation[] getCorrelations() {
		return correlations;
	}

	public void setCorrelations(BPELCorrelation[] correlations) {
		this.correlations = correlations;
	}

}
