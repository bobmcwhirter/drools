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

package org.drools.jpdl.instance.node;

import java.util.Collection;
import java.util.Map;

import org.drools.jpdl.core.node.Fork;
import org.drools.runtime.process.NodeInstance;
import org.jbpm.JbpmException;
import org.jbpm.graph.action.Script;

public class ForkInstance extends JpdlNodeInstance {

    private static final long serialVersionUID = 1L;
    
    public Fork getFork() {
        return (Fork) getNode();
    }

    public void execute(NodeInstance from, String type) {
        Collection<String> transitionNames = null;
        Script script = getFork().getScript();
        if (script == null) {
            transitionNames = getNode().getOutgoingConnections().keySet();
        } else {
            Map<String, Object> outputMap = null;
            try {
                outputMap = script.eval(new JpdlExecutionContext());
            } catch (Exception e) {
                this.raiseException(e);
            }
            if (outputMap.size() == 1) {
                Object result = outputMap.values().iterator().next();
                if (result instanceof Collection) {
                    transitionNames = (Collection<String>) result;
                }
            }
            if (transitionNames == null) {
                throw new JbpmException(
                    "script for fork '" + getNode().getName() +
                    "' should produce one collection (in one writable variable): " +
                    transitionNames);
            }
        }
        for (String transitionName: transitionNames) {
            leave(transitionName);
        }
    }
}
