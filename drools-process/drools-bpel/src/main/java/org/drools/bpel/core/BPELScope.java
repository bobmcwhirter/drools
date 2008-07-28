package org.drools.bpel.core;

import java.util.List;

import org.drools.process.core.context.exception.ExceptionScope;
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

	private SourceLink[] sourceLinks;
    private TargetLink[] targetLinks;

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

    public void setFaultHandlers(List<BPELFaultHandler> faultHandlers) {
        ExceptionScope exceptionScope = new ExceptionScope();
        addContext(exceptionScope);
        setDefaultContext(exceptionScope);
        for (BPELFaultHandler faultHandler: faultHandlers) {
            addNode(faultHandler.getActivity());
            exceptionScope.setExceptionHandler(faultHandler.getFaultName(), faultHandler);
        }
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

}
