package org.drools.task;

public interface UserProfileRepository {
	UserProfile getUserProfile(String userName);
	void setUserProfile(UserProfile info);
}
