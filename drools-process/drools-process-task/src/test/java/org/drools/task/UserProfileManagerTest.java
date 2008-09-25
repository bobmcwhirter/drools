package org.drools.task;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;

import junit.framework.TestCase;

public class UserProfileManagerTest extends TestCase {
    
    protected void setUp() throws Exception {
        super.setUp();                    
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testFileBasedUserProfileRepository() throws Exception {
    	//Mock up SEAM contexts
    	Map application = new HashMap<String, Object>();
    	Lifecycle.beginApplication(application);
    	Lifecycle.beginCall();
    	MockIdentity midentity = new MockIdentity();
    	Contexts.getSessionContext().set("org.jboss.seam.security.identity", midentity);
    	
    	
    	UserProfileManager upm = new UserProfileManager();
    	upm.setUserProfileRepository(new MockFileBasedUserProfileRepository());
    	
    	DroolsTaskUserProfile userProfile = (DroolsTaskUserProfile)upm.getUserProfile();    	
    	assertEquals(userProfile.getID(), "mockedUser");    	
    }    

}
