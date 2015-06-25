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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.CodeCoverageUtils;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.engine.frameworkutils.TestFrameworkUtils;
import org.wso2.carbon.automation.extensions.ExtensionConstants;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestServerManager {
    protected CarbonServerManager carbonServer;
    protected String carbonZip;
    protected int portOffset;
    protected Map<String, String> commandMap = new HashMap<String, String>();
    private static final Log log = LogFactory.getLog(TestServerManager.class);
    protected String carbonHome;
    protected String coverageDumpFilePath;
    protected AutomationContext context;

    public TestServerManager(AutomationContext context) {
        this.context = context;
        carbonServer = new CarbonServerManager(context);
    }

    public TestServerManager(AutomationContext context, String carbonZip) {
        this.context = context;
        carbonServer = new CarbonServerManager(context);
        this.carbonZip = carbonZip;
    }

    public TestServerManager(AutomationContext context, int portOffset) {
        this.context = context;
        carbonServer = new CarbonServerManager(context);
        this.portOffset = portOffset;
        commandMap.put(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND, String.valueOf(portOffset));
    }

    public TestServerManager(AutomationContext context, String carbonZip,
                             Map<String, String> commandMap) {
        this.context = context;
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
    public String startServer()
            throws AutomationFrameworkException, IOException, XPathExpressionException {
        if(carbonHome == null) {
            if (carbonZip == null) {
                carbonZip = System.getProperty(FrameworkConstants.SYSTEM_PROPERTY_CARBON_ZIP_LOCATION);
            }
            if (carbonZip == null) {
                throw new IllegalArgumentException("carbon zip file cannot find in the given location");
            }
            carbonHome = carbonServer.setUpCarbonHome(carbonZip);
            //insert Jacoco agent configuration to carbon server startup script. This configuration
            //cannot be directly pass as server startup command due to script limitation.
            instrumentForCoverage(context);
            System.out.println("GENERATING COVERAGE");
            configureServer();
        }
        log.info("Carbon Home - " + carbonHome);
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



    /**
     * This methods will insert jacoco agent settings into startup script under JAVA_OPTS
     *
     * @param carbonHome - carbonHome
     * @param scriptName - Name of the startup script
     * @throws IOException - throws if shell script edit fails
     */
    private void insertJacocoAgentToShellScript(String carbonHome, String scriptName)
            throws IOException {

        String jacocoAgentFile = CodeCoverageUtils.getJacocoAgentJarLocation();
        coverageDumpFilePath = FrameworkPathUtil.getCoverageDumpFilePath();

        CodeCoverageUtils.insertStringToFile(
                new File(carbonHome + File.separator + "bin" + File.separator + scriptName + ".sh"),
                new File(carbonHome + File.separator + "tmp" + File.separator + scriptName + ".sh"),
                "-Dwso2.server.standalone=true",
                "-javaagent:" + jacocoAgentFile + "=destfile=" + coverageDumpFilePath + "" +
                ",append=true,includes=" + CodeCoverageUtils.getInclusionJarsPattern(":") + " \\");
    }


    /**
     * This methods will insert jacoco agent settings into windows bat script
     *
     * @param carbonHome - carbonHome
     * @param scriptName - Name of the startup script
     * @throws IOException - throws if shell script edit fails
     */
    private void insertJacocoAgentToBatScript(String carbonHome, String scriptName)
            throws IOException {

        String jacocoAgentFile = CodeCoverageUtils.getJacocoAgentJarLocation();
        coverageDumpFilePath = FrameworkPathUtil.getCoverageDumpFilePath();

        CodeCoverageUtils.insertJacocoAgentToStartupBat(
                new File(carbonHome + File.separator + "bin" + File.separator + scriptName + ".bat"),
                new File(carbonHome + File.separator + "tmp" + File.separator + scriptName + ".bat"),
                "-Dcatalina.base",
                "-javaagent:" + jacocoAgentFile + "=destfile=" + coverageDumpFilePath + "" +
                ",append=true,includes=" + CodeCoverageUtils.getInclusionJarsPattern(":"));
    }


    /**
     * This method will check the OS and edit server startup script to inject jacoco agent
     * @param autoCtx - Automation context of the provided
     * @throws XPathExpressionException - If automation context cannot be retrieved
     * @throws IOException - If agent insertion fails.
     *
     */
    private void instrumentForCoverage(AutomationContext autoCtx)
            throws XPathExpressionException, IOException {
        Boolean isCoverageEnable = Boolean.parseBoolean(autoCtx.getConfigurationValue("//coverage"));
        String scriptName = TestFrameworkUtils.getStartupScriptFileName(carbonHome);
        if (isCoverageEnable) {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {

                insertJacocoAgentToBatScript(carbonHome, scriptName);
                if (log.isDebugEnabled()) {
                    log.debug("Included files " + CodeCoverageUtils.getInclusionJarsPattern(":"));
                    log.debug("Excluded files " + CodeCoverageUtils.getExclusionJarsPattern(":"));
                }
            } else {
                insertJacocoAgentToShellScript(carbonHome, scriptName);
            }
        }
    }
}
