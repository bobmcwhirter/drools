package org.drools.integration.console.forms;

import java.net.URL;

import javax.activation.DataHandler;

import org.jboss.bpm.console.server.plugin.FormAuthorityRef;
import org.jboss.bpm.console.server.plugin.FormDispatcherPlugin;

public class FormDispatcherComposite implements FormDispatcherPlugin {

	private FormDispatcherPlugin taskDispatcher;
	private FormDispatcherPlugin processDispatcher;

	public FormDispatcherComposite() {
		this.taskDispatcher = new TaskFormDispatcher();
		this.processDispatcher = new ProcessFormDispatcher();
	}

	public URL getDispatchUrl(FormAuthorityRef ref) {
		switch (ref.getType()) {
			case TASK:
				return taskDispatcher.getDispatchUrl(ref);
			case PROCESS:
				return processDispatcher.getDispatchUrl(ref);
			default:
				throw new IllegalArgumentException("Unknown authority type:" + ref.getType());
		}
	}

	public DataHandler provideForm(FormAuthorityRef ref) {
		switch (ref.getType()) {
			case TASK:
				return taskDispatcher.provideForm(ref);
			case PROCESS:
				return processDispatcher.provideForm(ref);
			default:
				throw new IllegalArgumentException("Unknown authority type:" + ref.getType());
		}
	}

}
