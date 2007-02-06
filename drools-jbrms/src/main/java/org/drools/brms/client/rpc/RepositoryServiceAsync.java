package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

/**

 * This is what the remote service will implement, as a servlet.

 * (in hosted/debug mode, you could also use an implementation that was in-process).

 */

public interface RepositoryServiceAsync
    extends
    RemoteService {

    /** Generated by AsyncInterfaceGenerator hackery */
    public void loadChildCategories(java.lang.String p0, AsyncCallback cb);
    public void loadRuleListForCategories(java.lang.String p0, AsyncCallback cb);
    public void loadTableConfig(java.lang.String p0, AsyncCallback cb);
    public void createCategory(java.lang.String p0, java.lang.String p1, java.lang.String p2, AsyncCallback cb);
    public void createNewRule(java.lang.String p0, java.lang.String p1, java.lang.String p2, java.lang.String p3, java.lang.String p4, AsyncCallback cb);
    public void listRulePackages(AsyncCallback cb);
    public void loadRuleAsset(java.lang.String p0, AsyncCallback cb);
    public void loadAssetHistory(java.lang.String p0, AsyncCallback cb);
    public void checkinVersion(org.drools.brms.client.rpc.RuleAsset p0, AsyncCallback cb);
    public void restoreVersion(java.lang.String p0, java.lang.String p1, java.lang.String p2, AsyncCallback cb);
    public void createPackage(java.lang.String p0, java.lang.String p1, AsyncCallback cb);
    public void loadPackage(java.lang.String p0, AsyncCallback cb);
    public void savePackage(org.drools.brms.client.rpc.PackageConfigData p0, AsyncCallback cb);
    public void listAssetsByFormat(java.lang.String p0, java.lang.String p1, int p2, int p3, AsyncCallback cb);
    
}
