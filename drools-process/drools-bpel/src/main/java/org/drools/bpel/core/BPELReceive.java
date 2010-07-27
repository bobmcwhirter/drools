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

package org.drools.bpel.core;

import java.util.List;

import org.drools.definition.process.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.core.node.EventNodeInterface;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELReceive extends NodeImpl implements BPELActivity, EventNodeInterface {

    private static final long serialVersionUID = 400L;

    private String partnerLink;
    private String portType;
    private String operation;
    private String variable;
    private boolean createInstance;
    private SourceLink[] sourceLinks;
    private TargetLink[] targetLinks;
    private BPELCorrelation[] correlations;
    
    public void setOperation(String partnerLink, String portType, String operation) {
    	this.partnerLink = partnerLink;
    	this.portType = portType;
    	this.operation = operation;
    }
    
    public void setVariable(String variable) {
    	this.variable = variable;
    }
    
    public String getVariable() {
    	return variable;
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

	public boolean acceptsEvent(String type, Object event) {
		if ("message".equals(type)) {
			String[] message = (String[]) event;
			return partnerLink.equals(message[0]) && portType.equals(message[1]) && operation.equals(message[2]);
		}
		return false;
	}
    	
}
