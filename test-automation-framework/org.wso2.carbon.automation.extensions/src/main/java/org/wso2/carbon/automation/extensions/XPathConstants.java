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
package org.wso2.carbon.automation.extensions;

/**
 * This class contain xpath expression to retrieve configurations in automation.xml
 */
public class XPathConstants {
    public static final String ADMIN_USER_PASSWORD = "//ns:%s/ns:tenant[@domain='%s']/ns:admin/ns:user/ns:password";
    public static final String USERS_NODE = "//ns:%s/ns:tenant[@domain='%s']/ns:users";
    public static final String USER_NODE = "//ns:%s/ns:tenant[@domain='%s']/ns:users/ns:user";
    public static final String ADMIN_USER_USERNAME = "//ns:%s/ns:tenant[@domain='%s']/ns:admin/ns:user/ns:userName";
    public static final String TENANT_USER_USERNAME =
            "//ns:%s/ns:tenant[@domain='%s']/ns:users/ns:user[@key='%s']/ns:userName";
    public static final String SUPER_TENANT_DOMAIN = "//ns:superTenant/ns:tenant[@key='superTenant']/@domain";
    public static final String TENANT_USER_PASSWORD =
            "//ns:%s/ns:tenant[@domain='%s']/ns:users/ns:user[@key='%s']/ns:password";
    public static final String PRODUCT_GROUP = "//ns:productGroup";
    public static final String SELENIUM_BROWSER_TYPE = "//ns:tools/ns:selenium/ns:browser/ns:browserType";
    public static final String SELENIUM_REMOTE_WEB_DRIVER_URL = "//ns:tools/ns:selenium/ns:remoteDriverUrl";
    public static final String CHROME_WEB_DRIVER_URL = "//ns:tools/ns:selenium/ns:browser/ns:webdriverPath";


    public static final String CLUSTERING_ENABLED = "clusteringEnabled";
    public static final String TENANTS_NODE = "//tenants";
    public static final String TENANTS = "tenants";
    public static final String SUPER_TENANT = "superTenant";
    public static final String DOMAIN = "domain";
    public static final String KEY = "key";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String WEB_CONTEXT_ENABLED = "webContextEnabled";
    public static final String WEB_CONTEXT_ROOT = "//ns:test/ns:root";
    public static final String DATA_SOURCE_NAME = "//ns:datasources/ns:datasource/ns:name";
    public static final String DATA_SOURCE_URL = "//ns:datasources/ns:datasource/ns:url";
    public static final String DATA_SOURCE_DRIVER_CLASS_NAME = "//ns:datasources/ns:datasource/ns:driverClassName";
    public static final String DATA_SOURCE_DB_USER_NAME = "//ns:datasources/ns:datasource/ns:username";
    public static final String DATA_SOURCE_DB_PASSWORD = "//ns:datasources/ns:datasource/ns:password";
}