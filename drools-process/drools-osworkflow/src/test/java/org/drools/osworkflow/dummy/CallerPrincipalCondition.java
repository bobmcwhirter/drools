package org.drools.osworkflow.dummy;

import java.util.Map;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.WorkflowException;

public class CallerPrincipalCondition implements Condition {

	public boolean passesCondition(Map arg0, Map arg1, PropertySet arg2)
			throws WorkflowException {
		
		return true;
	}

}
