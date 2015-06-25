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

package org.wso2.carbon.automation.extensions.servers.carbonserver;

import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A container for multiple Carbon server instances.
 * <p/>
 * How to use this class:
 * 1. Implement a TestServerManager instance for different types of servers. e.g. AS, ESB etc
 * 2. Create a Class with @BeforeSuite & @AfterSuite or  @BeforeClass & @AfterClass annotations.
 * <p/>
 * In @BeforeSuite/@BeforeClass, you can call {@link MultipleServersManager#startServers(TestServerManager...)}
 * In @AfterSuite/@AfterClass,, you can call {@link MultipleServersManager#stopAllServers()}
 */
public class MultipleServersManager {

    private Map<String, TestServerManager> servers = new HashMap<String, TestServerManager>();

    /**
     * Start a set of Carbon servers
     *
     * @param serverManagers vararg which specifies a TestServerManager instance per Carbon server
     * @throws AutomationFrameworkException If an error occurs while copying deployment artifacts into Carbon servers
     */
    public void startServers(TestServerManager... serverManagers)
            throws AutomationFrameworkException {
        for (TestServerManager zip : serverManagers) {
            String carbonHome = null;
            try {
                carbonHome = zip.startServer();
            } catch (IOException e) {
                throw new AutomationFrameworkException("Server start failed", e);
            } catch (AutomationFrameworkException e) {
                throw new AutomationFrameworkException("Server start failed", e);
            } catch (XPathExpressionException e) {
                throw new AutomationFrameworkException("Server start failed", e);
            }
            servers.put(carbonHome, zip);
        }
    }

    /**
     * Stop all servers started by this org.wso2.carbon.automation.extensions.servers.carbonserver.
     * MultipleServersManager
     *
     * @throws AutomationFrameworkException If an error occurs while stopping servers
     */
    public void stopAllServers() throws AutomationFrameworkException {
        for (TestServerManager serverUtils : servers.values()) {
            serverUtils.stopServer();
        }
    }
}
