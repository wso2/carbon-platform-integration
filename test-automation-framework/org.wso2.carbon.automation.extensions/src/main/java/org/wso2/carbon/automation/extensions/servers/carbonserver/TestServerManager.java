/*
*  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.automation.extensions.servers.carbonserver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.extensions.ExtensionConstants;
import org.wso2.carbon.automation.extensions.FrameworkExtensionUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used mange a carbon server for integration tests.
 */
public class TestServerManager {
    protected CarbonServerManager carbonServer;
    protected String carbonZip;
    protected int portOffset;
    protected Map<String, String> commandMap = new HashMap<String, String>();
    private static final Log log = LogFactory.getLog(TestServerManager.class);
    protected String carbonHome;

    public TestServerManager(AutomationContext context) {
        carbonServer = new CarbonServerManager(context);
    }

    public TestServerManager(AutomationContext context, String carbonZip) {
        carbonServer = new CarbonServerManager(context);
        this.carbonZip = carbonZip;
    }

    public TestServerManager(AutomationContext context, int portOffset) {
        carbonServer = new CarbonServerManager(context);
        this.portOffset = portOffset;
        commandMap.put(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND, String.valueOf(portOffset));
    }

    public TestServerManager(AutomationContext context, String carbonZip,
                             Map<String, String> commandMap) {
        carbonServer = new CarbonServerManager(context);
        this.carbonZip = carbonZip;
        if (commandMap.get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND) != null) {
            this.portOffset = Integer.parseInt(commandMap.get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND));
        } else {
            throw new IllegalArgumentException("portOffset value must be set in command list");
        }
        this.commandMap = commandMap;
    }

    public String getCarbonZip() {
        return carbonZip;
    }

    public String getCarbonHome() {
        return carbonHome;
    }

    public int getPortOffset() {
        return portOffset;
    }

    public void configureServer() throws AutomationFrameworkException {

    }

    /**
     * Apply the configuration changes from the given confChangesLocation.
     * If the confChangesLocation is empty will not do anything.
     *
     * @param confChangesLocation location where the desired config changes are.
     * @throws AutomationFrameworkException throw if carbonHome is not defined or copy config fails.
     */
    public void configureServer(String confChangesLocation) throws AutomationFrameworkException {
        if (StringUtils.isNotEmpty(confChangesLocation)) {
            if (StringUtils.isEmpty(carbonHome)) {
                throw new AutomationFrameworkException("Carbon Home is not set. Therefore cannot copy configuration " +
                        "changes from " + confChangesLocation);
            }
            File sourceDir = new File(FrameworkExtensionUtils.getOSSensitivePath(confChangesLocation));
            File destinationDir = new File(carbonHome + File.separator + "repository");
            try {
                FileUtils.copyDirectory(sourceDir, destinationDir);
            } catch (IOException e) {
                throw new AutomationFrameworkException("Error while copying config files form" + sourceDir.getPath()
                        + " to " + destinationDir.getPath());
            }
        }
    }


    public Map<String, String> getCommands() {
        return commandMap;
    }

    /**
     * This method is called for starting a Carbon server in preparation for execution of a
     * TestSuite
     * <p/>
     * Add the @BeforeSuite TestNG annotation in the method overriding this method
     *
     * @return The CARBON_HOME
     * @throws java.io.IOException If an error occurs while copying the deployment artifacts into the
     *                             Carbon server
     */
    public String startServer() throws AutomationFrameworkException {
        return startServer(StringUtils.EMPTY);
    }

    /**
     * This method starts a carbon server in preparation for execution of a test suite.
     * Before starting the server copies the configuration files from the given conf location
     * to the carbon server conf directory.
     *
     * @param confChangesLocation  Directory location of the desired configuration files.
     * @return The carbon home.
     * @throws AutomationFrameworkException Possible when carbonZip cannot be found and when setting up carbon home.
     */
    public String startServer(String confChangesLocation) throws AutomationFrameworkException {
        if (carbonHome == null) {
            if (carbonZip == null) {
                carbonZip = System.getProperty(FrameworkConstants.SYSTEM_PROPERTY_CARBON_ZIP_LOCATION);
            }
            if (carbonZip == null) {
                throw new AutomationFrameworkException("Carbon zip file cannot be found in the given " +
                        FrameworkConstants.SYSTEM_PROPERTY_CARBON_ZIP_LOCATION + " location.");
            }

            try {
                carbonHome = carbonServer.setUpCarbonHome(carbonZip);
            } catch (IOException e) {
                throw new AutomationFrameworkException("Error while setting up carbon home.", e);
            }
            configureServer(confChangesLocation);
        }
        log.info("Carbon Home - " + carbonHome);
        if (commandMap.get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND) != null) {
            this.portOffset = Integer.parseInt(commandMap.get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND));
        } else {
            this.portOffset = ExtensionConstants.DEFAULT_CARBON_PORT_OFFSET;
        }
        carbonServer.startServerUsingCarbonHome(carbonHome, commandMap);
        return carbonHome;
    }

    /**
     * Restarting server already started by the method startServer
     * @throws AutomationFrameworkException
     */
    public void restartGracefully() throws AutomationFrameworkException {
        if(carbonHome == null) {
            throw new AutomationFrameworkException("No Running Server found to restart. " +
                                                   "Please make sure whether server is started");
        }
        carbonServer.restartGracefully();
    }

    /**
     * This method is called for stopping a Carbon server
     * <p/>
     * Add the @AfterSuite annotation in the method overriding this method
     *
     * @throws AutomationFrameworkException If an error occurs while shutting down the server
     */
    public void stopServer() throws AutomationFrameworkException {
        carbonServer.serverShutdown(portOffset);
    }
}
