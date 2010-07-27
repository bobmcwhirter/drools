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

package org.drools.osworkflow.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.process.core.context.variable.VariableScope;
import org.drools.workflow.core.impl.WorkflowProcessImpl;

import com.opensymphony.workflow.loader.ActionDescriptor;

public class OSWorkflowProcess extends WorkflowProcessImpl {

    public static final String OSWORKFLOW_TYPE = "OSWorkflow";

    private static final long serialVersionUID = 1L;
    
    public Map<Integer, ActionDescriptor> initialActions = 
        new HashMap<Integer, ActionDescriptor>();
    
    public OSWorkflowProcess() {
        setType(OSWORKFLOW_TYPE);
        VariableScope variableScope = new VariableScope();
        addContext(variableScope);
        setDefaultContext(variableScope);
    }

    public void addInitialAction(ActionDescriptor action) {
        this.initialActions.put(action.getId(), action);
    }
    
    public void setInitialActions(List<ActionDescriptor> actions) {
        for (ActionDescriptor action: actions) {
            addInitialAction(action);
        }
    }
    
    public Collection<ActionDescriptor> getInitialActions() {
        return initialActions.values();
    }
    
    public ActionDescriptor getInitialAction(int id) {
        return initialActions.get(id);
    }

}
