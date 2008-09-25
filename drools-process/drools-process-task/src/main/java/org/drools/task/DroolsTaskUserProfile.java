package org.drools.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DroolsTaskUserProfile extends UserProfile {
	//TODO: A group is a collection of users and other groups (when nested groups are allowed) ?
/*	List<OrganizationalEntity> organizationalEntities = new ArrayList<OrganizationalEntity>();
	
	public void addOrganizationalEntity(OrganizationalEntity entity) {
		organizationalEntities.add(entity);
	}
	
	public void removeOrganizationalEntity(OrganizationalEntity entity) {
		organizationalEntities.remove(entity);
	}*/
	
    public String getDisplayName(OrganizationalEntity entity){
    	return null;
    }
    
    public Iterator<OrganizationalEntity> getMembersForGroup(Group group){
    	return null;
    }
    
    public boolean hasEmail(Group group) {
    	return false;
    }
    
    public String getEmailForEntity(OrganizationalEntity entity) {
    	return null;
    }
    
    public String getLanguageForEntity(OrganizationalEntity entity) {
    	return null;
    }
}
