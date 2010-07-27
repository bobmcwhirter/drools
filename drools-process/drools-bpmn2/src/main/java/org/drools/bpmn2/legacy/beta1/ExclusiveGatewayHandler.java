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

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.Split;
import org.xml.sax.Attributes;

public class ExclusiveGatewayHandler extends AbstractNodeHandler {
    
    protected Node createNode(Attributes attrs) {
        final String type = attrs.getValue("gatewayDirection");
        if ("converging".equals(type)) {
        	Join join = new Join();
        	join.setType(Join.TYPE_XOR);
        	return join;
        } else if ("diverging".equals(type)) {
        	Split split = new Split();
        	split.setType(Split.TYPE_XOR);
        	String isDefault = attrs.getValue("default");
        	split.setMetaData("Default", isDefault);
        	return split;
        } else {
        	throw new IllegalArgumentException(
    			"Unknown gateway direction: " + type);
        }
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return Node.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		throw new IllegalArgumentException("Writing out should be handled by split / join handler");
	}

}
