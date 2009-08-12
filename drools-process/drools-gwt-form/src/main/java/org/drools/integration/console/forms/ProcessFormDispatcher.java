package org.drools.integration.console.forms;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import javax.activation.DataHandler;

import org.jboss.bpm.console.server.plugin.FormAuthorityRef;

public class ProcessFormDispatcher extends AbstractFormDispatcher {
	
	public URL getDispatchUrl(FormAuthorityRef ref) {
	    System.out.println("getDispatchUrl " + ref.getReferenceId());
		InputStream template = getTemplate(ref.getReferenceId());
		if (template == null) {
			return null;
		}
		return super.getDispatchUrl(ref);
	}

	public DataHandler provideForm(FormAuthorityRef ref) {
        System.out.println("provideForm " + ref.getReferenceId());
		InputStream template = getTemplate(ref.getReferenceId());
		if (template == null) {
			return null;
		}
		return processTemplate(ref.getReferenceId(), template, new HashMap<String, Object>());
	}

}
