package org.drools.task;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

/**
 * userProfileManager retrieves and update user profile using a plugable UserProfileRepository. 
 * UserProfileRepository is normally implemented by users based on the persistent mechanism 
 * (for example property file, RMDB, LDAP etc) as well as the data scheam that is used by 
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
	public UserProfile getUserProfile() {
		String userName = "";
		if (Contexts.isApplicationContextActive()) {
			userName = Identity.instance().getCredentials().getUsername();
		}
		return userProfileRepository.getUserProfile(userName);
	}
	
	public UserProfile getUserProfile(String userName) {
		if (userProfileRepository == null) {
			//TODO: throws exception?
			return null;
		}

		return userProfileRepository.getUserProfile(userName);
	}
	
	public void updateUserProfile(UserProfile info) {
				
	}
	
	public List<UserProfile> getUsers() {
		//Not sure how to implement this yet. As the information of all registered users
		//might be stored in a place different from where user profile info is stored,
		//thus the UserProfileRepository may not be capable to do this job.
		return null;
	}

	public List<Group> getGroups() {
		//Not sure how to implement this yet. As the information of all registered groups
		//might be stored in a place different from where user profile info is stored,
		//thus the UserProfileRepository may not be capable to do this job.		
		return null;
	}
	
	public UserProfileRepository getUserProfileRepository() {
		return userProfileRepository;
	}

	public void setUserProfileRepository(UserProfileRepository userProfileRepository) {
		this.userProfileRepository = userProfileRepository;
	}

}
