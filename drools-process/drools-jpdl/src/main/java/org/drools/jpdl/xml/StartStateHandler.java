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

package org.drools.jpdl.xml;

import org.drools.compiler.xml.processes.AbstractNodeHandler;
import org.drools.jpdl.core.node.StartState;
import org.drools.workflow.core.Node;

public class StartStateHandler extends AbstractNodeHandler {
    
    protected Node createNode() {
        return new StartState();
    }
    
    public Class generateNodeFor() {
        return StartState.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		StartState startNode = (StartState) node;
		writeNode("start", startNode, xmlDump, includeMeta);
        endNode(xmlDump);
	}

}
