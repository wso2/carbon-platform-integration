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
package org.wso2.carbon.automation.engine.configurations;

public class ConfigurationConstants {
    //Root Context
    public static final String CONTEXT_FILE_NAME = "automation.xml";
    public static final String CONFIGURATION_VALIDATOR_XSD = "automationXMLSchema.xsd";
    public static final String CONFIGURATION_NODE = "configurations";
    public static final String TOOLS_NODE = "configurations";
    public static final String DEPLOYMENT_DELAY = "deploymentDelay";
    public static final String EXECUTION_ENVIRONMENT = "executionEnvironment";
    public static final String CONFIGURATION_CONTEXT_EXECUTION_MODE = "executionMode";
    public static final String CONFIGURATION_CONTEXT_CLOUD_ENABLED = "cloudEnabled";
    public static final String CONFIGURATION_CONTEXT_CLUSTERING = "clustering";
    public static final String COVERAGE = "coverage";
    public static final String CONFIGURATION_CONTEXT_FRAMEWORK_DASHBOARD = "frameworkDashboard";
    public static final String MULTITENANT_MODE = "multiTenantMode";
    public static final String TOOLS_CONTEXT_NODE = "tools";
    public static final String SECURITY_NODE = "security";
    public static final String FEATURE_MANAGEMENT_CONTEXT_NODE = "featureManagement";
    public static final String USER_MANAGEMENT_NODE = "userManagement";
    public static final String PLATFORM_NODE = "platform";
    public static final String LISTENER_EXTENSION_NODE = "listenerExtensions";
    public static final String DATABASE_CONTEXT_NODE = "databases";
    public static final String ATTRIBUTE_NAME = "name";
    public static final String DATABASE_CONTEXT_URL = "url";
    public static final String DATABASE_CONTEXT_USERNAME = "username";
    public static final String DATABASE_CONTEXT_PASSWORD = "password";
    public static final String DATABASE_CONTEXT_DRIVERCLASSNAME = "driverClassName";
    public static final String SECURITY_KEYSTORE_NAME = "name";
    public static final String KEYSTORE_FILENAME = "fileName";
    public static final String KEYSTORE_TYPE = "type";
    public static final String KEYSTORE_PASSWORD = "password";
    public static final String SECURITY_KEYSTORE_KEYALIAS = "keyAlias";
    public static final String SECURITY_KEYSTORE_KEYPASSWORD = "keyPassword";
    public static final String SECURITY_TRUSTSTORE_NAME = "name";
    public static final String SECURITY_TRUSTSTORE_FILENAME = "fileName";
    public static final String SECURITY_TRUSTSTORE_TYPE = "password";
    public static final String SECURITY_TRUSTSTORE_PASSWORD = "password";
    public static final String SECURITY_STORES_KETSTORE = "keystore";
    public static final String SECURITY_STORES_TRUSTSTORE = "truststore";
    public static final String FEATURE_MANAGEMENT_CONTEXT_P2RESITORIES_NAME = "name";
    public static final String FEATURE_MANAGEMENT_CONTEXT_REPOSITORY_ID = "id";
    public static final String USER_MANAGEMENT_CONTEXT_TENANT_KEY = "key";
    public static final String USER_MANAGEMENT_TENANT_ADMIN_KEY = "super";
    ;
    public static final String USER_MANAGEMENT_CONTEXT_TENANT_DOMAIN = "domain";
    public static final String USER_MANAGEMENT_CONTEXT_TENANT_USERNAME = "userName";
    public static final String USER_MANAGEMENT_CONTEXT_TENANT_PASSWORD = "password";
    public static final String USER_MANAGEMENT_CONTEXT_USER_TYPE_TENANT_USERS = "tenantUsers";
    public static final String ADMIN_TENANT = "admin";
    public static final String TOOL_CONTEXT_TOOL_SELENIUM = "selenium";
    public static final String TOOL_CONTEXT_TOOL_SELENIUM_REMOTE_DRIVE_URL = "remoteDriverUrl";
    public static final String TOOL_CONTEXT_TOOL_SELENIUM_REMOTE_DRIVE_URL_ENABLE = "enable";
    public static final String TOOL_CONTEXT_TOOL_SELENIUM_BROWSER_TYPE = "browserType";
    public static final String TOOL_CONTEXT_TOOL_SELENIUM_BROWSER_WEB_DRIVE_PATH = "webdriverPath";
    public static final String TOOL_CONTEXT_TOOL_SELENIUM_BROWSER_WEB_DRIVER_PATH_ENABLE = "enable";
    public static final String TOOL_CONTEXT_TOOL_SELENIUM_BROWSER = "browser";
    public static final String PRODUCT_GROUP_NAME = "name";
    public static final String PLATFORM_PRODUCT_GROUP = "productGroup";
    public static final String PLATFORM_CONTEXT_INSTANCE_GROUP_CLUSTERING_ENABLED =
            "clusteringEnabled";
    public static final String PLATFORM_CONTEXT_INSTANCE_NAME = "name";
    public static final String PLATFORM_CONTEXT_INSTANCE_TYPE = "type";
    public static final String PLATFORM_CONTEXT_INSTANCE_PORT_TYPE = "servicePortType";
    public static final String PLATFORM_CONTEXT_INSTANCE_HOST = "host";
    public static final String PLATFORM_CONTEXT_INSTANCE_HTTP_PORT = "httpport";
    public static final String PLATFORM_CONTEXT_INSTANCE_NHTTP_PORT = "nhttpport";
    public static final String PLATFORM_CONTEXT_INSTANCE_NHTTPS_PORT = "nhttpsport";
    public static final String PLATFORM_CONTEXT_INSTANCE_HTTPS_PORT = "httpsport";
    public static final String PLATFORM_CONTEXT_INSTANCE_WEB_CONTEXT = "webContext";
    public static final String PLATFORM_CONTEXT_INSTANCE_MANAGER_HOST = "managerHost";
    public static final String WORKER_HOST = "worker";
    public static final String TENANT_ADMIN_KEY = "super";
    public static final String TESTNG_EXTENSION_PLATFORM_EXECUTION_MANAGER =
            "PlatformExecutionManager";
    public static final String TESTNG_EXTENSION_EXECUTION_MANAGER_EXECUTION_START =
            "onExecutionStart";
    public static final String TESTNG_EXTENSION_EXECUTION_MANAGER_EXECUTION_FINISH =
            "onExecutionFinish";
    public static final String TESTNG_EXTENSION_PLATFORM_SUITE_MANAGER = "PlatformSuiteManager";
    public static final String TESTNG_EXTENSION_PLATFORM_SUITE_MANAGER_START = "onStart";
    public static final String TESTNG_EXTENSION_PLATFORM_SUITE_MANAGER_FINISH = "onFinish";
    public static final String TESTNG_EXTENSION_PLATFORM_TEST_MANAGER = "PlatformTestManager";
    public static final String TESTNG_EXTENSION_PLATFORM_TEST_MANAGER_TEST_START = "onTestStart";
    public static final String TESTNG_EXTENSION_PLATFORM_TEST_MANAGER_TEST_SUCCESS =
            "onTestSuccess";
    public static final String TESTNG_EXTENSION_PLATFORM_TEST_MANAGER_TEST_FAILURE =
            "onTestFailure";
    public static final String TESTNG_EXTENSION_PLATFORM_TEST_MANAGER_TEST_SKIPPED =
            "onTestSkipped";
    public static final String TESTNG_EXTENSION_PLATFORM_TEST_MANAGER_TEST_FAILED_SUCCESS =
            "onTestFailedButWithinSuccessPercentage";
    public static final String TESTNG_EXTENSION_PLATFORM_TEST_MANAGER_START = "onStart";
    public static final String TESTNG_EXTENSION_PLATFORM_TEST_MANAGER_FINISH = "onFinish";
    public static final String TESTNG_EXTENSION_PLATFORM_ANNOTATION_MANAGER =
            "PlatformAnnotationTransferManager";
    public static final String TESTNG_EXTENSION_PLATFORM_ANNOTATION_MANAGER_TRANSFORM = "transform";
    public static final String TESTNG_EXTENSION_PLATFORM_ANNOTATION_MANAGER_COMPARE_ANNOTATION =
            "compareAnnotation";
    public static final String TESTNG_EXTENSION_PLATFORM_REPORT_MANAGER = "PlatformReportManager";
    public static final String TESTNG_EXTENSION_PLATFORM_REPORT_MANAGER_GENERATE_REPORT =
            "generateReport";
    public static final String TESTNG_EXTENSION_PLATFORM_REPORT_MANAGER_XML_REPORT = "xmlReport";
    public static final String PRODUCT_GROUP_WORKER_INSTANCE = "asw001";
    public static final String PRODUCT_GROUP_APP_SERVER = "AS";
}
