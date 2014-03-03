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
package org.wso2.carbon.automation.extensions.usermgt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.configurations.UrlGenerationUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.ExtensionConstants;
import org.wso2.carbon.automation.extensions.XPathConstants;
import org.wso2.carbon.automation.extensions.adminclients.AuthenticationAdminClient;
import org.wso2.carbon.automation.extensions.adminclients.TenantManagementServiceClient;
import org.wso2.carbon.automation.extensions.adminclients.UserManagementAdminServiceClient;

import javax.xml.xpath.XPathExpressionException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for adding tenants and users
 * defined under userManagement entry in automation.xml to servers.
 */
public class UserPopulator {
    private static final Log log = LogFactory.getLog(UserPopulator.class);
    String sessionCookie;
    String backendURL;
    List<String> tenantsList;
    TenantManagementServiceClient tenantStub;
    String productGroupName;
    String instanceName;


    public UserPopulator(String productGroupName, String instanceName) throws XPathExpressionException {
        this.productGroupName = productGroupName;
        this.instanceName = instanceName;
        tenantsList = getTenantsDomainList();

    }

    public void populateUsers() throws Exception {
        String tenantAdminSession;
        UserManagementAdminServiceClient userManagementClient;
        AutomationContext automationContext = new AutomationContext(productGroupName, instanceName,
                TestUserMode.SUPER_TENANT_ADMIN);
        backendURL = automationContext.getContextUrls().getBackEndUrl();
        sessionCookie = automationContext.login();
        tenantStub = new TenantManagementServiceClient(backendURL, sessionCookie);
        //tenants is the domain of the tenants elements
        for (String tenants : tenantsList) {
            if (!tenants.equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
                tenantStub.addTenant(tenants, automationContext.getConfigurationValue(String.format
                        (XPathConstants.ADMIN_USER_PASSWORD, XPathConstants.TENANTS, tenants)),
                        automationContext.getConfigurationValue(String.format
                                (XPathConstants.ADMIN_USER_USERNAME, XPathConstants.TENANTS, tenants)),
                        FrameworkConstants.TENANT_USAGE_PLAN_DEMO);
            }
            log.info("Start populating users for " + tenants);
            String superTenantReplacement = XPathConstants.TENANTS;
            if (tenants.equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
                superTenantReplacement = XPathConstants.SUPER_TENANT;
            }
            tenantAdminSession = login(automationContext.getConfigurationValue(String.
                    format(XPathConstants.ADMIN_USER_USERNAME, superTenantReplacement, tenants)), tenants,
                    automationContext.
                            getConfigurationValue(String.format(XPathConstants.ADMIN_USER_PASSWORD, superTenantReplacement,
                                    tenants)), backendURL,
                    UrlGenerationUtil.getManagerHost(automationContext.getInstance()));
            //here we populate the user list of the current tenant
            List<String> userList = getUserList(tenants);
            userManagementClient = new UserManagementAdminServiceClient
                    (backendURL, tenantAdminSession);
            for (String tenantUsername : userList) {
                System.out.println(userManagementClient.getUserList().size());
                boolean isUserAddedAlready = userManagementClient.getUserList().contains(automationContext.
                        getConfigurationValue(String.format(XPathConstants.TENANT_USER_USERNAME, superTenantReplacement,
                                tenants,
                                tenantUsername)));
                if (!isUserAddedAlready) {
                    userManagementClient.addUser(automationContext.getConfigurationValue(String.format(XPathConstants.
                            TENANT_USER_USERNAME, superTenantReplacement, tenants, tenantUsername)),
                            automationContext.getConfigurationValue(String.format(XPathConstants.TENANT_USER_PASSWORD,
                                    superTenantReplacement,
                                    tenants, tenantUsername)),
                            new String[]{FrameworkConstants.ADMIN_ROLE}, null);
                    log.info("Populated " + tenantUsername);
                } else {
                    if (!tenantUsername.equals(ExtensionConstants.ADMIN_USER)) {
                        log.info(tenantUsername + " is already in " + tenants);
                    }
                }
            }
        }

    }

    public void deleteUsers() throws Exception {
        String tenantAdminSession;
        AutomationContext automationContext = new AutomationContext(productGroupName, instanceName,
                TestUserMode.SUPER_TENANT_ADMIN);
        UserManagementAdminServiceClient userManagementClient;
        for (String tenants : tenantsList) {
            String superTenantReplacement = XPathConstants.TENANTS;
            if (tenants.equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
                superTenantReplacement = XPathConstants.SUPER_TENANT;
            }
            backendURL = automationContext.getContextUrls().getBackEndUrl();
            tenantAdminSession = login(automationContext.getConfigurationValue(String.
                    format(XPathConstants.ADMIN_USER_USERNAME, superTenantReplacement, tenants)), tenants,
                    automationContext.
                            getConfigurationValue(String.format(XPathConstants.ADMIN_USER_PASSWORD,
                                    superTenantReplacement,
                                    tenants)), backendURL,
                    UrlGenerationUtil.getManagerHost(automationContext.getInstance()));
            userManagementClient = new UserManagementAdminServiceClient
                    (backendURL, tenantAdminSession);
            List<String> userList = getUserList(tenants);
            for (String user : userList) {
                boolean isUserAddedAlready = userManagementClient.getUserList().contains(automationContext.
                        getConfigurationValue(String.format(XPathConstants.TENANT_USER_USERNAME,
                                superTenantReplacement,
                                tenants,
                                user)));
                if (isUserAddedAlready) {
                    if (!user.equals(FrameworkConstants.ADMIN_ROLE)) {
                        userManagementClient.deleteUser(automationContext.getConfigurationValue
                                (String.format(XPathConstants.
                                        TENANT_USER_USERNAME, superTenantReplacement, tenants, user)));
                        log.info(user + " user deleted successfully");
                    }
                }
            }
        }
    }

    protected String login(String userName, String domain, String password, String backendUrl,
                           String hostName)
            throws RemoteException, LoginAuthenticationExceptionException, XPathExpressionException {
        AuthenticationAdminClient loginClient = new AuthenticationAdminClient(backendUrl);
        return loginClient.login(domain, userName, password, hostName);
    }

    public List<String> getTenantsDomainList() throws XPathExpressionException {
        List<String> tenantDomain = new ArrayList<String>();
        tenantDomain.add(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME);
        AutomationContext automationContext = new AutomationContext();
        int numberOfTenants = automationContext.getConfigurationNodeList(XPathConstants.TENANTS_NODE).item(0).
                getChildNodes().getLength();
        for (int i = 0; i < numberOfTenants; i++) {
            tenantDomain.add(automationContext.getConfigurationNodeList(XPathConstants.TENANTS_NODE).item(0).
                    getChildNodes().
                    item(i).getAttributes().getNamedItem(XPathConstants.DOMAIN).getNodeValue());
        }
        return tenantDomain;
    }

    public List<String> getUserList(String tenantDomain) throws XPathExpressionException {

        //according to the automation xml the super tenant no has to be accessed explicitly
        List<String> userList = new ArrayList<String>();
        AutomationContext automationContext = new AutomationContext();
        int numberOfUsers;
        String superTenantReplacement = XPathConstants.TENANTS;
        if (tenantDomain.equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
            superTenantReplacement = XPathConstants.SUPER_TENANT;
        }
        numberOfUsers = automationContext.getConfigurationNodeList(String.format(XPathConstants.USER_NODE,
                superTenantReplacement, tenantDomain)).getLength();
        for (int i = 0; i < numberOfUsers; i++) {
            String userKey = automationContext.getConfigurationNodeList(String.
                    format(XPathConstants.USERS_NODE, superTenantReplacement, tenantDomain)).item(0).getChildNodes().
                    item(i).getAttributes().getNamedItem(XPathConstants.KEY).getNodeValue();
            userList.add(userKey);
        }
        return userList;
    }
}


