package org.drools.integration.console;

import org.jboss.bpm.console.server.integration.ManagementFactory;
import org.jboss.bpm.console.server.integration.ProcessManagement;
import org.jboss.bpm.console.server.integration.TaskManagement;
import org.jboss.bpm.console.server.integration.UserManagement;

public class DroolsFlowManagementFactory extends ManagementFactory {

	public ProcessManagement createProcessManagement() {
		return new DroolsFlowProcessManagement();
	}

	public TaskManagement createTaskManagement() {
		return new DroolsFlowTaskManagement();
	}

	public UserManagement createUserManagement() {
		return new DroolsFlowUserManagement();
	}

}
