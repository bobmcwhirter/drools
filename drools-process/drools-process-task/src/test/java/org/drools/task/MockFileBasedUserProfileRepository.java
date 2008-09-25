package org.drools.task;

public class MockFileBasedUserProfileRepository implements UserProfileRepository {
	String configFile = "userinfo.txt";
	
	public UserProfile getUserProfile(String userName) {
		//load the property file, get user info.		
		DroolsTaskUserProfile ui = new DroolsTaskUserProfile();		
	
		ui.setID(userName);
		return ui;
	}
	
	public void setUserProfile(UserProfile info) {
		//NOT IMPLEMENTED;
	}
}
