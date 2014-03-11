/*
* Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.carbon.automation.test.utils.registry;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.app.RemoteRegistry;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.ws.client.registry.WSRegistryServiceClient;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.MalformedURLException;

/**
 * Provide remote registries - ws-api, remote registry and governance registry
 */
public class RegistryProviderUtil {
    private static final Log log = LogFactory.getLog(RegistryProviderUtil.class);

    public WSRegistryServiceClient getWSRegistry(String productGroup, String instance, String tenantDomain, String userKey)
            throws RegistryException, AxisFault, XPathExpressionException {
        System.setProperty("carbon.repo.write.mode", "true");
        WSRegistryServiceClient registry = null;
//        String userName;
//        String password;
        ConfigurationContext configContext;
        String serverURL;
        AutomationContext automationContext = new AutomationContext(productGroup, instance, tenantDomain, userKey);
        String axis2Repo = FrameworkPathUtil.getSystemResourceLocation() + File.separator + "client";
        String axis2Conf = FrameworkPathUtil.getSystemResourceLocation() + "axis2config" +
                File.separator + "axis2_client.xml";
        setKeyStoreProperties();
        try {
            configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(axis2Repo, axis2Conf);
            int timeOutInMilliSeconds = 1000 * 60;
            configContext.setProperty(HTTPConstants.CONNECTION_TIMEOUT, timeOutInMilliSeconds);
            log.info("Group ConfigurationContext Timeout " + configContext.getServiceGroupContextTimeoutInterval());
            registry = new WSRegistryServiceClient(automationContext.getContextUrls().getBackEndUrl()
                    , automationContext.getUser().getUserName(), automationContext.getUser().getPassword(), configContext);
            log.info("WS Registry -Login Success");
        } catch (AxisFault axisFault) {
            log.error("Unable to initialize WSRegistryServiceClient :" + axisFault.getMessage());
            throw new AxisFault("Unable to initialize WSRegistryServiceClient :" + axisFault.getMessage());
        } catch (RegistryException e) {
            log.error("Unable to initialize WSRegistryServiceClient:" + e);
            throw new RegistryException("Unable to initialize WSRegistryServiceClient:" + e);
        }
        return registry;
    }

    /*public WSRegistryServiceClient getWSRegistry(String userName, String password,
                                                 String productName)
            throws RegistryException, AxisFault {
        System.setProperty("carbon.repo.write.mode", "true");
        WSRegistryServiceClient registry = null;
        ConfigurationContext configContext;
        String serverURL;
        EnvironmentBuilder env = new EnvironmentBuilder();
        if (env.getFrameworkSettings().getEnvironmentSettings().is_runningOnStratos()) {   //if Stratos tests are enabled.
            serverURL = getServiceURL(productName);
        } else {
            serverURL = getServiceURL(productName);
        }

        String axis2Repo = ProductConstant.getModuleClientPath();
        String axis2Conf = ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "axis2config" +
                           File.separator + "axis2_client.xml";
        PlatformUtil.setKeyStoreProperties();
        try {
            configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(axis2Repo, axis2Conf);
            int timeOutInMilliSeconds = 1000 * 60;
            configContext.setProperty(HTTPConstants.CONNECTION_TIMEOUT, timeOutInMilliSeconds);
            log.info("Group ConfigurationContext Timeout " + configContext.getServiceGroupContextTimeoutInterval());
            registry = new WSRegistryServiceClient(serverURL, userName, password, configContext);
            log.info("WS Registry -Login Success");
        } catch (AxisFault axisFault) {
            log.error("Unable to initialize WSRegistryServiceClient :" + axisFault.getMessage());
            throw new AxisFault("Unable to initialize WSRegistryServiceClient :" + axisFault.getMessage());
        } catch (RegistryException e) {
            log.error("Unable to initialize WSRegistryServiceClient:" + e);
            throw new RegistryException("Unable to initialize WSRegistryServiceClient:" + e);
        }
        return registry;
    }
*/
    public Registry getGovernanceRegistry(Registry registry, String userName)
            throws RegistryException, XPathExpressionException {
        Registry governance;
        setKeyStoreProperties();
        System.setProperty("carbon.repo.write.mode", "true");
        try {
            governance = GovernanceUtils.getGovernanceUserRegistry(registry, userName);
        } catch (RegistryException e) {
            log.error("getGovernance Registry Exception thrown:" + e);
            throw new RegistryException("getGovernance Registry Exception thrown:" + e);
        }
        return governance;
    }

    public RemoteRegistry getRemoteRegistry(String productGroup, String instance, String tenantDomain, String userKey)
            throws MalformedURLException, RegistryException {
        String registryURL;
        RemoteRegistry registry = null;
//        UserInfo userDetails = UserListCsvReader.getUserInfo(userId);
//        String username = userDetails.getUserName();
//        String password = userDetails.getPassword();
//        EnvironmentBuilder env = new EnvironmentBuilder();
//        FrameworkProperties properties = FrameworkFactory.getFrameworkProperties(productName);
//        System.setProperty("carbon.repo.write.mode", "true");
//
//        if (env.getFrameworkSettings().getEnvironmentSettings().is_runningOnStratos()) {
//            registryURL =
//                    UrlGenerationUtil.getRemoteRegistryURLOfStratos(properties.getProductVariables().getHttpsPort(),
//                                                                    properties.getProductVariables().getHostName(),
//                                                                    properties, userDetails);
//        } else {
//            registryURL = ProductUrlGeneratorUtil.getRemoteRegistryURLOfProducts(properties.getProductVariables().
//                    getHttpsPort(), properties.getProductVariables().getHostName(), properties.getProductVariables().getWebContextRoot());
//        }
//
//        log.info("Remote Registry URL" + registryURL);
//
//        try {
//            registry = new RemoteRegistry(new URL(registryURL), username, password);
//        } catch (RegistryException e) {
//            log.error("Error on initializing Remote Registry :" + e);
//            throw new RegistryException("Error on initializing Remote Registry error  :" + e);
//        } catch (MalformedURLException e) {
//            log.error("Invalid registry URL :" + e);
//            throw new MalformedURLException("Invalid registry URL" + e);
//        }
        return registry;
    }

    private static void setKeyStoreProperties() throws XPathExpressionException {
        AutomationContext context = new AutomationContext();
        System.setProperty("javax.net.ssl.trustStore", FrameworkPathUtil.getSystemResourceLocation()
                + context.getConfigurationValue("//keystore/fileName/text()"));
        System.setProperty("javax.net.ssl.trustStorePassword",
                context.getConfigurationValue("//keystore/keyPassword/text()"));
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        if (log.isDebugEnabled()) {
            log.debug("javax.net.ssl.trustStore :" + System.getProperty("javax.net.ssl.trustStore"));
            log.debug("javax.net.ssl.trustStorePassword :" + System.getProperty("javax.net.ssl.trustStorePassword"));
            log.debug("javax.net.ssl.trustStoreType :" + System.getProperty("javax.net.ssl.trustStoreType"));
        }
    }
}
