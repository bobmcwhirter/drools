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

package org.drools.osworkflow.core.node;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workflow.core.impl.NodeImpl;

import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

public class StepNode extends NodeImpl {

    private Map<Integer, ActionDescriptor> actions = 
        new HashMap<Integer, ActionDescriptor>();
    private List<FunctionDescriptor> preFunctions;
    private List<FunctionDescriptor> postFunctions;
    
    private static final long serialVersionUID = 1L;
    
    public void addAction(ActionDescriptor action) {
        this.actions.put(action.getId(), action);
    }
    
    public void setActions(List<ActionDescriptor> actions) {
        for (ActionDescriptor action: actions) {
            addAction(action);
        }
    }
    
    public Collection<ActionDescriptor> getActions() {
        return actions.values();
    }
    
    public ActionDescriptor getAction(int id) {
        return actions.get(id);
    }

    public List<FunctionDescriptor> getPreFunctions() {
        return preFunctions;
    }

    public void setPreFunctions(List<FunctionDescriptor> preFunctions) {
        this.preFunctions = preFunctions;
    }

    public List<FunctionDescriptor> getPostFunctions() {
        return postFunctions;
    }

    public void setPostFunctions(List<FunctionDescriptor> postFunctions) {
        this.postFunctions = postFunctions;
    }

}
