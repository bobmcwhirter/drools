package org.drools.osworkflow.dummy;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Condition;

/**
 * Dummy Condition for testing purposes only
 * 
 * @author mfossati
 *
 */
public class DummyCondition implements Condition {

	private Log logger = LogFactory.getLog(DummyCondition.class);
	
	public boolean passesCondition(Map arg0, Map arg1, PropertySet arg2) {
		String name = (String)arg0.get("name");
		String message = (String)arg0.get("message");
		logger.info("Condition" +name+ "called with message: " + message);
		return true;
	}

}
