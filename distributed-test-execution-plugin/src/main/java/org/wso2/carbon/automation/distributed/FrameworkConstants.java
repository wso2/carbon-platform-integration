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
package org.wso2.carbon.automation.distributed;

/**
 * FrameworkConstants.
 */
public class FrameworkConstants {
    public static final String SYSTEM_PROPERTY_SETTINGS_LOCATION = "automation.settings.location";
    public static final String SYSTEM_PROPERTY_BASEDIR_LOCATION = "basedir";
    public static final String SYSTEM_PROPERTY_OS_NAME = "os.name";
    public static final String SYSTEM_PROPERTY_CARBON_ZIP_LOCATION = "carbon.zip";
    public static final String SYSTEM_PROPERTY_SEC_VERIFIER_DIRECTORY = "sec.verifier.dir";
    public static final int DEFAULT_CARBON_PORT_OFFSET = 0;
    public static final String SERVICE_FILE_SEC_VERIFIER = "SecVerifier.aar";
    public static final String LIST_SUPPORTED_DATABASES = "mysql,oracle,derby,h2";
    public static final String SEVER_STARTUP_SCRIPT_NAME = "wso2server";
    public static final String SERVER_STARTUP_PORT_OFFSET_COMMAND = "-DportOffset";
    public static final String SERVER_DEFAULT_HTTPS_PORT = "9443";
    public static final String SERVER_DEFAULT_HTTP_PORT = "9763";
    public static final String SUPER_TENANT_DOMAIN_NAME = "carbon.super";
    public static final String ADMIN_ROLE = "admin";
    public static final String DEFAULT_KEY_STORE = "wso2";
    public static final String TENANT_USAGE_PLAN_DEMO = "demo";
    public static final String AUTOMATION_SCHEMA_NAME = "automationXMLSchema.xsd";
    public static final String LISTENER_INIT_METHOD = "initiate";
    public static final String LISTENER_EXECUTE_METHOD = "execution";
    public static final String DEFAULT_BACKEND_URL = "https://localhost:9443/services/";
    public static final String AUTHENTICATE_ADMIN_SERVICE_NAME = "AuthenticationAdmin";
    public static final String CONFIGURATION_FILE_NAME = "automation.xml";
    public static final String MAPPING_FILE_NAME = "automation_mapping.xsd";
    public static final String DEFAULT_PRODUCT_GROUP = "default.product.group";
    public static final String EXECUTION_MODE = "framework.execution.mode";
    public static final String ENVIRONMENT_STANDALONE = "standalone";
    public static final String ENVIRONMENT_PLATFORM = "platform";
    public static final String SYSTEM_ARTIFACT_RESOURCE_LOCATION = "framework.resource.location";
    public static final String SUPER_TENANT_KEY = "superTenant";
    public static final String SUPER_TENANT_ADMIN = "superAdmin";
    public static final String CARBON_HOME = "carbon.home";
    public static final String JACOCO_AGENT_JAR_NAME = "jacocoagent.jar";
    public static final String CLASS_FILE_PATTERN = "**/*.class";
    public static final String JSON_FILE_PATH = "jsonFilePath";
    public static final String TESTLINK_CLASSMAP_SPLITTER = "#";
    public static final String DEFAULT_TESTNG_FILE = "testng.xml";
    public static final String TESTNG_RESULT_OUT_DIRECTORY = "apim-intergration-tests";

}
