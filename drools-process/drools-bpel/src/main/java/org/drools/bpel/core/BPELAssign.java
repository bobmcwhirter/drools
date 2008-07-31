package org.drools.bpel.core;

import java.util.ArrayList;
import java.util.List;

import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.NodeImpl;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELAssign extends NodeImpl implements BPELActivity {

    private static final long serialVersionUID = 400L;
    
    private List<Copy> copies = new ArrayList<Copy>();
    private SourceLink[] sourceLinks;
    private TargetLink[] targetLinks;
    
    public void addCopy(Copy copy) {
    	copies.add(copy);
    }
    
    public List<Copy> getCopies() {
    	return copies;
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
    
    public void validateAddIncomingConnection(final String type,
            final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default incoming connection type!");
        }
        if (getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE) != null
                && !getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE).isEmpty()) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default outgoing connection type!");
        }
        if (getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE) != null
                && !getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE).isEmpty()) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one outgoing connection!");
        }
    }
    
    public class Copy {

    	private From from;
    	private To to;
    	
		public From getFrom() {
			return from;
		}
		
		public void setFrom(From from) {
			this.from = from;
		}
		
		public To getTo() {
			return to;
		}
		
		public void setTo(To to) {
			this.to = to;
		}
			
    }
    
    public interface From {
    }
    
    public interface To {
    }
    
    public class VariablePart implements From, To {

    	private String variable;
    	private String part;
    	
    	public VariablePart(String variable, String part) {
    		this.variable = variable;
    		this.part = part;
    	}
    	
    	public String getVariable() {
    		return variable;
    	}
    	
    	public String getPart() {
    		return part;
    	}
    	
    }
    
    public class LiteralValue implements From {
    	
    	private String value;
    	
    	public LiteralValue(String value) {
    		this.value = value;
    	}
    	
    	public String getValue() {
    		return value;
    	}
    	
    }

    public class Expression implements From {
    	
    	private String expression;
    	
    	public Expression(String expression) {
    		this.expression = expression;
    	}
    	
    	public String getExpression() {
    		return expression;
    	}
    	
    }

}
