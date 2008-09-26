package org.drools.userprofile;

import java.util.List;

public interface UserProfileRepository {
	UserProfile getUserProfile(String userName);
	void setUserProfile(UserProfile info);
	
	List<User> getUsers();
	List<Group> getGroups();
}
