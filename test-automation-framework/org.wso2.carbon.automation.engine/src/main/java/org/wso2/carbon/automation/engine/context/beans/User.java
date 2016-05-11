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

package org.wso2.carbon.automation.engine.context.beans;

import org.wso2.carbon.automation.engine.FrameworkConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * User bean for Automation context.
 */
public class User {
    private String key;
    private String userName = null;
    private String password = null;
    private List<String> roles;

    public void setKey(String key) {
        this.key = key;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getKey() {
        return key;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserNameWithoutDomain() {
        String[] result = userName.split("@");
        return result[0];
    }

    public String getUserDomain() {
        if (userName.contains("@")) {
            return userName.substring(userName.lastIndexOf("@") + 1);
        } else {
            return FrameworkConstants.SUPER_TENANT_DOMAIN_NAME;
        }
    }

    public List<String> getRoles() {
        if (roles == null) {
            roles = new ArrayList<String>();
        }
        return roles;
    }

    public void addRole(String role) {
        getRoles().add(role);
    }
}
