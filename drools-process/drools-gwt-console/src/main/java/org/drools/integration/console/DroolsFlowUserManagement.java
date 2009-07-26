package org.drools.integration.console;

import java.util.ArrayList;
import java.util.List;

import org.jboss.bpm.console.server.integration.UserManagement;

public class DroolsFlowUserManagement implements UserManagement {

	public List<String> getActorsForGroup(String groupName) {
		// TODO: fixme
		List<String> result = new ArrayList<String>();
		result.add("admin");
		return result;
	}

	public List<String> getGroupsForActor(String actorId) {
		// TODO: fixme
		List<String> result = new ArrayList<String>();
		result.add("admins");
		return result;
	}

}
