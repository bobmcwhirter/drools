package org.drools.integration.console.forms;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import javax.activation.DataHandler;

import org.jboss.bpm.console.server.plugin.FormAuthorityRef;

public class ProcessFormDispatcher extends AbstractFormDispatcher {
	
	public URL getDispatchUrl(FormAuthorityRef ref) {
	    InputStream template = getTemplate(ref.getReferenceId());
		if (template == null) {
			return null;
		}
		return super.getDispatchUrl(ref);
	}

	public DataHandler provideForm(FormAuthorityRef ref) {
        InputStream template = getTemplate(ref.getReferenceId());
		if (template == null) {
			return null;
		}
		return processTemplate(ref.getReferenceId(), template, new HashMap<String, Object>());
	}

}
