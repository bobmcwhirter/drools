package org.drools.osworkflow.dummy;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;

/**
 * FunctionProvider that does nothing but logging a custom message, just for testing purposes.
 * 
 * @param name: name of the function
 * @param message: message to log
 * 
 * @author mfossati
 *
 */
public class DummyFunctionProvider implements FunctionProvider {
	
	private Log logger = LogFactory.getLog(DummyFunctionProvider.class);

	public void execute(Map transientVars, Map args, PropertySet ps){
		String name = (String)args.get("name");
		String message = (String)args.get("message");
		logger.info("Function " +name+ " called with message: " + message);
		transientVars.put(message, "dummy" + message);
	}

}
