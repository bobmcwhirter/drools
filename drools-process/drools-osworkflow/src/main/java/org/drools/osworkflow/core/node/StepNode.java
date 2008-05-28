package org.drools.osworkflow.core.node;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workflow.core.impl.NodeImpl;

import com.opensymphony.workflow.loader.ActionDescriptor;

public class StepNode extends NodeImpl {

    public Map<Integer, ActionDescriptor> actions = 
        new HashMap<Integer, ActionDescriptor>();
    
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

}
