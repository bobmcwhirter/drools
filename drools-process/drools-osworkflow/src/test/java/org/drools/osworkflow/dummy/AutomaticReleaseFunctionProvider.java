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
