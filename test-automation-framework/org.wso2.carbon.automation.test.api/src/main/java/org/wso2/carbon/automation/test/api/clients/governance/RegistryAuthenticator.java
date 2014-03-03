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

package org.wso2.carbon.automation.test.api.clients.governance;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.ws.client.registry.WSRegistryServiceClient;

public class RegistryAuthenticator {
    public static Registry authenticateRegistry(String username, String password)
            throws AxisFault, RegistryException {
        String GREG_HOME = "/home/charitha/products/greg/cluster/wso2greg-4.0.0-SNAPSHOT";
        System.setProperty("javax.net.ssl.trustStore", GREG_HOME + "/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        String axis2Repo = GREG_HOME + "/repository/deployment/client";
        String axis2Conf = GREG_HOME + "/repository/conf/axis2/axis2_client.xml";
        ConfigurationContext configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(axis2Repo, axis2Conf);
        String serverURL = "https://localhost:9443/services/";
        WSRegistryServiceClient registry = new WSRegistryServiceClient(serverURL, username, password, configContext);
        return GovernanceUtils.getGovernanceUserRegistry(registry, username);
    }
}