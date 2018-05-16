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
package org.wso2.carbon.automation.engine.context;

public class ContextXpathConstants {
    //constants for product group element
    public static final String PRODUCT_GROUP_NAME = "//ns:productGroup[@name='%s']";
    public static final String PRODUCT_GROUP_PARENT="//ns:platform/ns:productGroup";
    public static final String PRODUCT_GROUP_ALL_STANDALONE_INSTANCE="//ns:platform/ns:productGroup/ns:instance[@type='standalone']";
    
    public static final String PRODUCT_GROUP_STANDALONE_INSTANCE =
            "//ns:productGroup[@default='true']/ns:instance[@type='standalone']";
    public static final String PRODUCT_GROUP_CLUSTERING_ENABLED = "//ns:productGroup[@name='%s']/@clusteringEnabled";
    public static final String PRODUCT_GROUP_DEFAULT_NAME = "//ns:productGroup[@default='true']/@name";
    public static final String PRODUCT_GROUP_INSTANCE_NAME = "//ns:productGroup[@name='%s']/ns:instance[@name='%s']";
    public static final String PRODUCT_GROUP_INSTANCE_TYPE = "//ns:productGroup[@name='%s']/ns:instance[@type='%s']";
    public static final String PRODUCT_GROUP_INSTANCE_PORT =
            "//ns:productGroup[@name='%s']/ns:instance[@name='%s']/ns:ports/ns:port";
    public static final String PRODUCT_GROUP_INSTANCE_HOST =
            "//ns:productGroup[@name='%s']/ns:instance[@name='%s']/ns:hosts/ns:host";
    public static final String PRODUCT_GROUP_INSTANCE_PROPERTY =
            "//ns:productGroup[@name='%s']/ns:instance[@name='%s']/ns:properties/ns:property";
    public static final String PRODUCT_GROUP_DEFAULT_STANDALONE_INSTANCE = "" +
            "//ns:productGroup[@default='true']/ns:instance[@type='standalone']";
    public static final String PRODUCT_GROUP_INSTANCE = "//ns:productGroup[@name='%s']/ns:instance[ @name='%s']";
    public static final String PRODUCT_GROUP_INSTANCE_PORTS =
            "//ns:productGroup[@name='%s']/ns:instance[@name='%s']/ns:ports/ns:port";
    public static final String PRODUCT_GROUP_INSTANCE_HOSTS =
            "//ns:productGroup[@name='%s']/ns:instance[@name='%s']/ns:hosts/ns:host";
    public static final String PRODUCT_GROUP_INSTANCE_PROPERTIES =
            "//ns:productGroup[@name='%s']/ns:instance[@name='%s']/ns:properties/ns:property";
    //constants for user management element
    public static final String USER_MANAGEMENT_SUPER_TENANT_KEY ="//ns:userManagement/ns:superTenant/ns:tenant/@key";
    public static final String SUPER_TENANT_ADMIN_USERNAME ="//ns:userManagement/ns:superTenant/ns:tenant[@key='%s']/ns:admin/ns:user[@key='%s']/ns:userName";
    public static final String SUPER_TENANT_ADMIN_PASSWORD ="//ns:userManagement/ns:superTenant/ns:tenant[@key='%s']/ns:admin/ns:user[@key='%s']/ns:password";
    public static final String USER_MANAGEMENT_SUPER_TENANT_ADMIN = "//ns:superTenant/ns:tenant/ns:admin";
    public static final String USER_MANAGEMENT_SUPER_TENANT_USER_KEY = "//ns:superTenant/ns:tenant/ns:%s/ns:user/@key";
    public static final String USER_MANAGEMENT_SUPER_TENANT_DOMAIN = "//ns:superTenant/ns:tenant/@domain";
    public static final String USER_MANAGEMENT_TENANT_ADMIN_USERNAME =
            "//ns:%s/ns:tenant[@domain='%s']/ns:admin/ns:user[@key='%s']/ns:userName";
    public static final String USER_MANAGEMENT_TENANT_ADMIN_PASSWORD =
            "//ns:%s/ns:tenant[@domain='%s']/ns:admin/ns:user[@key='%s']/ns:password";
    public static final String USER_MANAGEMENT_SUPER_TENANT_ADMIN_USER = "//ns:superTenant/ns:tenant/ns:admin/ns:user";
    public static final String USER_MANAGEMENT_SUPER_TENANT_USERS = "//ns:superTenant/ns:tenant/ns:users";
    public static final String USER_MANAGEMENT_TENANT_DOMAIN = "//ns:%s/ns:tenant[@key='%s']/@domain";
    public static final String TENANT_DOMAIN = "//ns:tenants/ns:tenant/@domain";
    public static final String USER_MANAGEMENT_TENANT_ADMIN = "//ns:tenants/ns:tenant[@domain='%s']/ns:admin";
    public static final String USER_MANAGEMENT_TENANT_USERS = "//ns:tenants/ns:tenant[@domain='%s']/ns:users";
    public static final String USER_MANAGEMENT_TENANT_USER = "//ns:%s/ns:tenant[@domain='%s']/ns:users/ns:user[@key='%s']";
    public static final String USER_MANAGEMENT_TENANT_USER_KEY = "//ns:tenants/ns:tenant[@domain='%s']/ns:%s/ns:user/@key";
    public static final String SUPER_TENANT_DOMAIN = "//ns:superTenant/ns:tenant[@key='superTenant']/@domain";
    public static final String USER_MANAGEMENT_TENANT_USER_NAME =
            "//ns:%s/ns:tenant[@domain='%s']/ns:users/ns:user[@key='%s']/ns:userName";
    public static final String USER_MANAGEMENT_TENANT_USER_PASSWORD =
            "//ns:%s/ns:tenant[@domain='%s']/ns:users/ns:user[@key='%s']/ns:password";
    public static final String PRODUCT_GROUP_WEBCONTEXT = "webContext";
    public static final String PRODUCT_GROUP_PORT_HTTPS = "https";
    public static final String PRODUCT_GROUP_PORT_HTTP = "http";
    public static final String PRODUCT_GROUP_PORT_NHTTPS = "nhttps";
    public static final String PRODUCT_GROUP_PORT_NHTTP = "nhttp";
    public static final String TENANTS = "tenants";
    public static final String SUPER_TENANT = "superTenant";
    public static final String SUPER_ADMIN = "superAdmin";
    public static final String ADMIN = "admin";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String INSTANCE = "instance";
    public static final String KEY = "key";
    public static final String NON_BLOCKING_ENABLED = "nonBlockingTransportEnabled";
    public static final String WORKER = "worker";
    public static final String DEFAULT = "default";
    public static final String MANAGER = "manager";
    public static final String USERNAME = "userName";
    public static final String PASSWORD = "password";
    public static final String USER = "user";
    public static final String USERS = "users";
    public static final String CLUSTERING_ENABLED = "clusteringEnabled";
    public static final String SUPER_TENANT_ADMIN = "SUPER_TENANT_ADMIN";
    public static final String SUPER_TENANT_USER = "SUPER_TENANT_USER";
    public static final String TENANT_ADMIN = "TENANT_ADMIN";
    public static final String TENANT_USER = "TENANT_USER";
    public static final String EXECUTION_ENVIRONMENT = "//ns:executionEnvironment/text()";


	public static final String ROLES = "roles";
	public static final String USER_MANAGEMENT_TENANT_USERS_ROLES =
			"//ns:%s/ns:tenant[@domain='%s']/ns:users/ns:user[@key='%s']/ns:roles/ns:role";

    public static final String USER_NODE = "//ns:%s/ns:tenant[@domain='%s']/ns:users/ns:user";
    public static final String TENANTS_NODE = "//ns:tenants";
    public static final String DOMAIN = "domain";
}