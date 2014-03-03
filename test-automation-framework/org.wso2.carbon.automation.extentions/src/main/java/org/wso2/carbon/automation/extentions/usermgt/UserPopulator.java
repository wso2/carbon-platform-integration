/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.automation.extentions.usermgt;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.adminclients.AuthenticationAdminClient;
import org.wso2.carbon.automation.engine.adminclients.TenantManagementServiceClient;
import org.wso2.carbon.automation.engine.adminclients.UserManagementAdminServiceClient;
import org.wso2.carbon.automation.engine.configurations.AutomationConfiguration;
import org.wso2.carbon.automation.engine.configurations.ConfigurationConstants;
import org.wso2.carbon.automation.engine.configurations.exceptions.NonExistenceException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class UserPopulator {
    private static final Log log = LogFactory.getLog(UserPopulator.class);
    Boolean isMultiTenantMode;
    String sessionCookie;
    String backendURL;
    List<String> tenantsList;
    TenantManagementServiceClient tenantStub;

    /**
     * @param sessionCookie   session cookie of the tenant domain session
     * @param multiTenantMode true/false values for whether multi tenant mode enabled or disabled
     */
    public UserPopulator(String sessionCookie, String backendURL, Boolean multiTenantMode)
            throws AxisFault, NonExistenceException {
        this.sessionCookie = sessionCookie;
        this.isMultiTenantMode = multiTenantMode;
        this.backendURL = backendURL;
        tenantStub = new TenantManagementServiceClient(backendURL, sessionCookie);
        //get the tenant list
        if (!isMultiTenantMode) {
            tenantsList = new ArrayList<String>();
            tenantsList.add(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME);
        } else {
            tenantsList = new ArrayList<String>(AutomationConfiguration.getTenantsDomainList());
        }
    }

    public void populateUsers(String productName, String instanceName) throws Exception {
        String tenantAdminSession;
        UserManagementAdminServiceClient userManagementClient;
        //tenants is the domain of the tenants elements
        for (String tenants : tenantsList) {
            if (!tenants.equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
                tenantStub.addTenant(tenants, AutomationConfiguration.
                        getTenantPassword(tenants, ConfigurationConstants.ADMIN_TENANT),
                        AutomationConfiguration.getTenantUsername(tenants,
                                ConfigurationConstants.ADMIN_TENANT),
                        FrameworkConstants.TENANT_USAGE_PLAN_DEMO);
            }
            Thread.sleep(2000);
            log.info("Start populating users for " + tenants);
            tenantAdminSession = login(AutomationConfiguration.
                    getTenantUsername(tenants, ConfigurationConstants.ADMIN_TENANT), tenants,
                    AutomationConfiguration.
                            getTenantPassword(tenants, ConfigurationConstants.ADMIN_TENANT), backendURL,
                    AutomationConfiguration.getManagementHost(productName, instanceName));
            List<String> userList = AutomationConfiguration.getTenantList(tenants);
            userManagementClient = new UserManagementAdminServiceClient
                    (backendURL, tenantAdminSession);
            for (String tenantUsername : userList) {
                System.out.println(userManagementClient.getUserList().size());
                if (!userManagementClient.getUserList().contains(AutomationConfiguration.
                        getTenantUsername(tenants, tenantUsername))) {
                    userManagementClient.addUser(AutomationConfiguration.
                            getTenantUsername(tenants, tenantUsername),
                            AutomationConfiguration.getTenantPassword(tenants, tenantUsername),
                            new String[]{FrameworkConstants.ADMIN_ROLE}, null);
                    log.info("Populated " + tenantUsername);
                } else {
                    if (!tenantUsername.equals(ConfigurationConstants.ADMIN_TENANT)) {
                        log.info(tenantUsername + " is already in " + tenants);
                    }
                }
            }
        }
        Thread.sleep(2000);
    }

    public void deleteUsers(String productName, String instanceName) throws Exception {
        String tenantAdminSession;
        UserManagementAdminServiceClient userManagementClient;
        for (String tenants : tenantsList) {
            tenantAdminSession = login(AutomationConfiguration.
                    getTenantUsername(tenants, ConfigurationConstants.ADMIN_TENANT), tenants,
                    AutomationConfiguration.
                            getTenantPassword(tenants, ConfigurationConstants.ADMIN_TENANT), backendURL,
                    AutomationConfiguration.getManagementHost(productName, instanceName));
            userManagementClient = new UserManagementAdminServiceClient(backendURL,
                    tenantAdminSession);
            List<String> userList = AutomationConfiguration.getTenantList(tenants);
            for (String user : userList) {
                if (userManagementClient.getUserList().contains(AutomationConfiguration.
                        getTenantUsername(tenants, user))) {
                    if (!user.equals(FrameworkConstants.ADMIN_ROLE)) {
                        userManagementClient.deleteUser(AutomationConfiguration.
                                getTenantUsername(tenants, user));
                        log.info(user + " user deleted successfully");
                    } else {
                        continue;
                    }
                }
            }
            if (!tenants.equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
                tenantStub.deleteTenant(tenants);
            }
        }
    }

    protected static String login(String userName, String domain, String password, String backendUrl,
                                  String hostName)
            throws RemoteException, LoginAuthenticationExceptionException {
        AuthenticationAdminClient loginClient = new AuthenticationAdminClient(backendUrl);
        return loginClient.login(domain, userName, password, hostName);
    }
}
