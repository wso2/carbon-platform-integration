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
package org.wso2.carbon.automation.engine.context.mappertype;

import java.util.*;

public class Tenant {


    private String domain;
    private User tenantAdmin;
    private Map<String, User> tenantUsers = new HashMap<String, User>();


    public void setDomain(String value) {

        this.domain = value;
    }

    public void setTenantAdmin(User admin) {

        this.tenantAdmin = admin;
    }

    public void setTenantUsers(Map<String, User> users) {

        this.tenantUsers = users;
    }

    public String getDomain() {

        return domain;
    }

    public User getTenantAdmin() {

        return tenantAdmin;
    }

    public Map<String, User> getTenantUserMap() {

        return tenantUsers;
    }

    public List<User> getTenantUserList() {
        List<User> userList = new LinkedList<User>();
        Iterator<User> repoIterator = tenantUsers.values().iterator();
        while (repoIterator.hasNext()) {
            userList.add(repoIterator.next());
        }
        return userList;
    }


    public User getTenantUser(String tenantKey) {

        return tenantUsers.get(tenantKey);
    }

    public void addTenantUsers(User user) {

        tenantUsers.put(user.getKey(), user);

    }
}
