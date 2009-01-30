package org.drools.osworkflow.dummy;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

public class AutomaticReleaseFunctionProvider implements FunctionProvider {
	private static final Log LOGGER = LogFactory
	.getLog(AutomaticReleaseFunctionProvider.class);
	public void execute(Map actionInputs, Map args, PropertySet persistentVars)
			throws WorkflowException {
		final String propertyName = (String) args.get("propertyName");
		final String variableName = (String) args.get("variableName");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("propertyName:" + propertyName);
			LOGGER.debug("variableName:" + variableName);
		}
		System.out.println("propertyName:" + propertyName);
		System.out.println("variableName:" + variableName);
		

	}

}
