package org.drools.userprofile;

import java.util.List;

public class MockFileBasedUserProfileRepository implements UserProfileRepository {
	public UserProfile getUserProfile(String userName) {
		//load the property file, get user info.
		DroolsTaskUserProfile ui = new DroolsTaskUserProfile();

		ui.setID(userName);
		return ui;
	}

	public void setUserProfile(UserProfile info) {
		//NOT IMPLEMENTED;
	}
	
	
    public List<User> getUsers() {
		return null;
	}
	
	public List<Group> getGroups() {
		return null;
    }
}
