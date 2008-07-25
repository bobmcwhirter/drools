package org.drools.bpel.core;

import org.drools.process.core.context.variable.VariableScope;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.CompositeContextNode;
import org.drools.workflow.core.node.CompositeNode;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELScope extends CompositeContextNode implements BPELActivity, BPELFaultHandlerContainer {

    private static final long serialVersionUID = 400L;

    public BPELScope() {
	    VariableScope variableScope = new VariableScope();
	    addContext(variableScope);
	    setDefaultContext(variableScope);
	}
	
	public VariableScope getVariableScope() {
	    return (VariableScope) getDefaultContext(VariableScope.VARIABLE_SCOPE);
	}

    public void setActivity(BPELActivity activity) {
        addNode(activity);
        linkIncomingConnections(
            Node.CONNECTION_DEFAULT_TYPE,
            new CompositeNode.NodeAndType(
                activity, Node.CONNECTION_DEFAULT_TYPE));
        linkOutgoingConnections(
            new CompositeNode.NodeAndType(
                activity, Node.CONNECTION_DEFAULT_TYPE),
            Node.CONNECTION_DEFAULT_TYPE);
    }

    public SourceLink[] getSourceLinks() {
        throw new IllegalArgumentException("A scope does not support links!");
    }

    public TargetLink[] getTargetLinks() {
        throw new IllegalArgumentException("A scope does not support links!");
    }

    public void setSourceLinks(SourceLink[] sourceLinks) {
        throw new IllegalArgumentException("A scope does not support links!");
    }

    public void setTargetLinks(TargetLink[] targetLinks) {
        throw new IllegalArgumentException("A scope does not support links!");
    }
    
}
