package org.drools.osworkflow.instance.node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.drools.osworkflow.core.node.StepNode;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.ConditionsDescriptor;
import com.opensymphony.workflow.loader.RestrictionDescriptor;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.util.StatusCondition;

public class StepNodeInstance extends NodeInstanceImpl implements Step {

    private static final long serialVersionUID = 1L;
    
    private String status;
    
    public StepNode getStepNode() {
        return (StepNode) getNode();
    }
    
    public int getStepId() {
        return (int) getNodeId();
    }

    public void internalTrigger(NodeInstance from, String type) {
        setStatus(type);
    }
    
    public void doAction(int actionId, Map inputs) {
        ActionDescriptor action = getStepNode().getAction(actionId);
        if (action == null) {
            throw new IllegalArgumentException(
                "Unknown action id " + actionId);
        }
        // TODO check stuff
        triggerCompleted(actionId + "", true);
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public boolean isAvailableAction(int actionId) {
        return isAvailableAction(getStepNode().getAction(actionId));
    }
    
    private boolean isAvailableAction(ActionDescriptor action) {
        if (action == null) {
            return false;
        }
        RestrictionDescriptor restriction = action.getRestriction();
        if (restriction != null) {
            ConditionsDescriptor conditions = restriction.getConditionsDescriptor();
            if (conditions != null) {
                List<ConditionDescriptor> conditionList = conditions.getConditions();
                for (ConditionDescriptor condition: conditionList) {
                    String className = (String) condition.getArgs().get("class.name");
                    if (StatusCondition.class.getName().equals(className.trim())) {
                        String status = (String) condition.getArgs().get("status");
                        return status.equals(this.status);
                    }
                }
            }
        }
        return true;
    }
    
    public List<Integer> getAvailableActions() {
        List<Integer> ids = new ArrayList<Integer>();
        for (ActionDescriptor action: getStepNode().getActions()) {
           if (isAvailableAction(action)) {
               ids.add(action.getId());
           }
        }
        return ids;
    }

    public int getActionId() {
        // TODO
        return 0;
    }

    public String getCaller() {
        // TODO
        return null;
    }

    public Date getDueDate() {
        // TODO
        return null;
    }

    public long getEntryId() {
        // TODO
        return 0;
    }

    public Date getFinishDate() {
        // TODO
        return null;
    }

    public String getOwner() {
        // TODO
        return null;
    }

    public long[] getPreviousStepIds() {
        // TODO
        return null;
    }

    public Date getStartDate() {
        // TODO
        return null;
    }

}
