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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.configurations.AutomationConfiguration;
import org.wso2.carbon.automation.engine.configurations.ConfigurationConstants;
import org.wso2.carbon.automation.engine.configurations.configurationenum.Platforms;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkUtil;

import java.util.List;

public class UserPopulateHandler {
    private static final Log log = LogFactory.getLog(UserPopulateHandler.class);
    private static String executionEnvironment;
    private static UserPopulator userPopulator;
    private static String sessionCookie;
    private static String instanceName;
    private static String backendURL;
    private static boolean multiTenantEnabled = AutomationConfiguration.isMultiTenantEnabled();

    /**
     * This class handles the user population for  different execution modes
     *
     * @throws Exception
     */
    public static void populateUsers() throws Exception {
        FrameworkUtil.setKeyStoreProperties();
        executionEnvironment = AutomationConfiguration.getExecutionEnvironment();
        if (executionEnvironment.equals(Platforms.product.name())) {
            String productGroupName = AutomationConfiguration.getFirstProductGroup().
                    getValue(ConfigurationConstants.PRODUCT_GROUP_NAME);
            List<String> instanceList = AutomationConfiguration.getAllStandaloneInstances();
            for (String instance : instanceList) {
              //  sessionCookie = ContextUtills.
               //         getAdminUserSessionCookie(productGroupName, instance);
                System.out.println("Session Cookie is " + sessionCookie);
               // backendURL = UrlGenerationUtil.getBackendURL(productGroupName, instance);
                userPopulator = new UserPopulator(sessionCookie, backendURL, multiTenantEnabled);
                log.info("Populating users for " + productGroupName + " product group: " +
                        instance + " instance");
                userPopulator.populateUsers(productGroupName, instance);
            }
        }
        //here we go through every product group and populate users for those
        else if (executionEnvironment.equals(Platforms.platform.name())) {
            List<String> productGroupList = AutomationConfiguration.getAllProductGroups();
            for (String productGroupName : productGroupList) {
                if (! AutomationConfiguration.isClusteringEnabled(productGroupName)) {
                    instanceName = AutomationConfiguration.getAllStandaloneInstances().get(0);
                } else {
                    if (AutomationConfiguration.getAllLBWorkerManagerInstances().size() > 0) {
                         instanceName = AutomationConfiguration.getAllLBWorkerManagerInstances().get(0);
                    } else if (AutomationConfiguration.getAllLBManagerInstances().size() > 0) {
                        instanceName = AutomationConfiguration.getAllLBManagerInstances().get(0);
                    } else if (AutomationConfiguration.getAllManagerInstances().size() > 0) {
                        instanceName = AutomationConfiguration.getAllManagerInstances().get(0);
                    }
                }
                //sessionCookie = ContextUtills.getAdminUserSessionCookie
                  //      (productGroupName, instanceName);
                //backendURL = UrlGenerationUtil.getBackendURL(productGroupName, instanceName);
                userPopulator = new UserPopulator(sessionCookie, backendURL, multiTenantEnabled);
                log.info("Populating users for " + productGroupName + " product group: " +
                        instanceName + " instance");
                userPopulator.populateUsers(productGroupName, instanceName);
            }
        }
    }

    public static void deleteUsers() throws Exception {
        if (executionEnvironment.equals(Platforms.product.name())) {
            String productGroupName = AutomationConfiguration.getFirstProductGroup().
                    getValue(ConfigurationConstants.PRODUCT_GROUP_NAME);
            List<String> instanceList = AutomationConfiguration.getAllStandaloneInstances();
            for (String instance : instanceList) {
             //   sessionCookie = ContextUtills.
              //          getAdminUserSessionCookie(productGroupName, instance);
              //  backendURL = UrlGenerationUtil.getBackendURL(productGroupName, instance);
                userPopulator = new UserPopulator(sessionCookie, backendURL, multiTenantEnabled);
                log.info("Populating users for " + productGroupName + " product group: " +
                        instance + " instance");
                userPopulator.deleteUsers(productGroupName, instance);
            }
        }
        //here we go through every product group and populate users for those
        else if (executionEnvironment.equals(Platforms.platform.name())) {
            List<String> productGroupList = AutomationConfiguration.getAllProductGroups();
            for (String productGroupName : productGroupList) {
                instanceName = AutomationConfiguration.getAllLBManagerInstances().get(0);
              //  sessionCookie = ContextUtills.getAdminUserSessionCookie
               //         (productGroupName, instanceName);
               // backendURL = UrlGenerationUtil.getBackendURL(productGroupName, instanceName);
                userPopulator = new UserPopulator(sessionCookie, backendURL, multiTenantEnabled);
                log.info("Populating users for " + productGroupName + " product group: " +
                        instanceName + " instance");
                userPopulator.deleteUsers(productGroupName, instanceName);
            }
        }
    }
}
