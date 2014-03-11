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
package org.wso2.carbon.automation.test.utils.usermgt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.test.api.clients.registry.ResourceAdminServiceClient;
import org.wso2.carbon.automation.test.api.clients.user.mgt.UserManagementClient;

public class UserManagementUtil {
    private static final Log log = LogFactory.getLog(UserManagementUtil.class);

    /**
     * Add new user to given role
     *
     * @param backendUrl      - backend url of products
     * @param newUserName     - user name of the new user to be added
     * @param newUserPassword - password of the new user to be added.
     * @param roleName        - role of the user
     * @param userInfo        - UserBean of user
     * @throws Exception - throws if user addition fails.
     */
    public static void createUser(String backendUrl, String newUserName,
                                  String newUserPassword, String roleName,
                                  User userInfo)
            throws Exception {
        UserManagementClient userManagementClient = null;
        userManagementClient = new UserManagementClient(backendUrl, userInfo.getUserName(),
                userInfo.getPassword());
        if (userManagementClient.roleNameExists(roleName)) {
            userManagementClient.addUser(newUserName, newUserPassword, new String[]{roleName}, null);
            log.info("User " + newUserName + " was created successfully");
        }
    }

    /**
     * The role will be created with all permissions and read, write, delete and authorize permission fro registry browser
     *
     * @param roleName   - name of the role to be added
     * @param backendUrl - backendURL of the product
     * @param userInfo   - UserBean of admin user
     * @throws Exception - throws if role addition fails
     */
    public static void createRoleWithAllPermissions(String roleName, String backendUrl,
                                                    User userInfo)
            throws Exception {
        //todo - getting default role name
        final String DEFAULT_PRODUCT_ROLE = "testRole";
        ResourceAdminServiceClient resourceAdmin = null;
        UserManagementClient userManagementClient = null;
        String[] permissions = {"/permission/"};
        resourceAdmin = new ResourceAdminServiceClient(backendUrl, userInfo.getUserName(),
                userInfo.getPassword());
        userManagementClient = new UserManagementClient(backendUrl, userInfo.getUserName(),
                userInfo.getPassword());
        String[] userList = null;
        if (!userManagementClient.roleNameExists(roleName)) {
            userManagementClient.addRole(roleName, userList, permissions);
            resourceAdmin.addResourcePermission("/", DEFAULT_PRODUCT_ROLE, "3", "1");
            resourceAdmin.addResourcePermission("/", DEFAULT_PRODUCT_ROLE, "2", "1");
            resourceAdmin.addResourcePermission("/", DEFAULT_PRODUCT_ROLE, "4", "1");
            resourceAdmin.addResourcePermission("/", DEFAULT_PRODUCT_ROLE, "5", "1");
            log.info("Role " + roleName + " was created successfully");
        }
    }
}



