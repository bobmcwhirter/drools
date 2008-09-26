package org.drools.userprofile;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

/**
 * userProfileManager retrieves and update user profile using a pluggable UserProfileRepository. 
 * UserProfileRepository is normally implemented by users based on the persistent mechanism 
 * (for example property file, RMDB, LDAP etc) as well as the data schema that is used by 
 * users' application.
 * Following snippet shows how to configure UserProfileRepository in components.xml.
    <component name="userProfileManager">
	    <property name="userProfileRepository">org.drools.task.MockFileBasedUserProfileRepository</property>
    </component>
 *   
 * @author Jervis Lliu
 */

@Scope(ScopeType.APPLICATION)
@Startup
@Name("userProfileManager")
public class UserProfileManager {
	UserProfileRepository userProfileRepository = null;

	//get current user
	public User getUser() {
		String userName = "";
		if (Contexts.isApplicationContextActive()) {
			userName = Identity.instance().getCredentials().getUsername();
		}
		return getUser(userName);
	}
	
	public User getUser(String userName) {
		if (userProfileRepository == null) {
			//TODO: throws exception?
			return null;
		}
		User user = new User();
		user.setUserProfile(userProfileRepository.getUserProfile(userName));
		return user;
	}
	
	public void updateUserProfile(UserProfile info) {
				
	}
	
	public List<User> getUsers() {
		if (userProfileRepository == null) {
			//TODO: throws exception?
			return null;
		}
		return userProfileRepository.getUsers();
	}

	public List<Group> getGroups() {
		if (userProfileRepository == null) {
			//TODO: throws exception?
			return null;
		}
		return userProfileRepository.getGroups();
	}
	
	public UserProfileRepository getUserProfileRepository() {
		return userProfileRepository;
	}

	public void setUserProfileRepository(UserProfileRepository userProfileRepository) {
		this.userProfileRepository = userProfileRepository;
	}

}
