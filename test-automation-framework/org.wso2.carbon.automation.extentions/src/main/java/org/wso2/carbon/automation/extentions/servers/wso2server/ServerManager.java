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
package org.wso2.carbon.automation.extentions.servers.wso2server;

import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.configurations.AutomationConfiguration;
import org.wso2.carbon.automation.engine.configurations.configurationenum.Platforms;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.frameworkutils.CodeCoverageUtils;
import org.wso2.carbon.automation.extentions.ExtentionConstants;
import org.wso2.carbon.automation.extentions.adminclients.ServerAdminServiceClient;
import org.wso2.carbon.automation.extentions.servers.utils.ServerLogReader;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ServerManager {
    private static final Log log = LogFactory.getLog(ServerManager.class);
    private int portOffset;
    private Process process;
    private ServerLogReader inputStreamHandler;
    private Thread carbonThread = null;
    private Process tempProcess;
    private String hostName;
    boolean isRunning = false;
    private Map<String, String> commandMap;
    private String carbonHome;
    private AutomationContext context;
    private boolean isCoverageEnable;
    private static final String SERVER_SHUTDOWN_MESSAGE = "Halting JVM";
    private static final String SERVER_STARTUP_MESSAGE = "Mgt Console URL";
    private static final long DEFAULT_START_STOP_WAIT_MS = 1000 * 60 * 5;

    public ServerManager(Map<String, String> commandMap) throws ZipException {
        this.commandMap = commandMap;
        portOffset = Integer.parseInt(commandMap.get(ExtentionConstants
                .SERVER_STARTUP_PORT_OFFSET_COMMAND));
        CarbonPackageManager carbonPackageManager = new CarbonPackageManager();
        carbonHome = carbonPackageManager.unzipCarbonPackage();
    }

    public ServerManager(int portOffset) throws ZipException {
        this.commandMap = new HashMap<String, String>();
        this.portOffset = portOffset;

        CarbonPackageManager carbonPackageManager = new CarbonPackageManager();
        carbonHome = carbonPackageManager.unzipCarbonPackage();
    }

    public ServerManager(AutomationContext automationContext) throws ZipException {
        this.context = automationContext;
        this.commandMap = new HashMap<String, String>();
        this.portOffset = ExtentionConstants.DEFAULT_CARBON_PORT_OFFSET;
        CarbonPackageManager carbonPackageManager = new CarbonPackageManager();
        carbonHome = carbonPackageManager.unzipCarbonPackage();
    }

    public synchronized void startServer() throws Exception {
        startCarbonServer();
    }

    public synchronized void startCarbonServer() throws Exception {
        if (process != null) { // An instance of the server is running
            return;
        }
        //   isCoverageEnable = AutomationConfiguration.isCoverageEnabled();
        try {
            File commandDir = new File(carbonHome);
            String scriptName = ExtentionConstants.SEVER_STARTUP_SCRIPT_NAME;
            System.setProperty(ServerConstants.CARBON_HOME, carbonHome);
            if (System.getProperty(ExtentionConstants.SYSTEM_PROPERTY_OS_NAME).
                    toLowerCase().contains("windows")) {
                commandDir = new File(carbonHome + File.separator + "bin");
                String[] cmdArray;
                if (isCoverageEnable) {
                    CodeCoverageUtils.init();
                    CodeCoverageUtils.instrument(carbonHome);
                    cmdArray = new String[]{"cmd.exe", "/c", scriptName + ".bat",
                            "-Demma.properties=" + System.getProperty("emma.properties"),
                            "-Demma.rt.control.port=" + (47653 + portOffset),
                            expandServerStartupCommandList(commandMap)};
                } else {
                    cmdArray = new String[]{"cmd.exe", "/c", scriptName + ".bat",
                            expandServerStartupCommandList(commandMap)};
                }
                tempProcess = Runtime.getRuntime().exec(cmdArray, null, commandDir);
            } else {
                String[] cmdArray;
                if (isCoverageEnable) {
                    CodeCoverageUtils.init();
                    CodeCoverageUtils.instrument(carbonHome);
                    cmdArray = new String[]{"sh", "bin/" + scriptName + ".sh",
                            "-Demma.properties=" + System.getProperty("emma.properties"),
                            expandServerStartupCommandList(commandMap)};
                } else {
                    cmdArray = new String[]{"sh", "bin/" + scriptName + ".sh"};
                }
                tempProcess = Runtime.getRuntime().exec(cmdArray, null, commandDir);
            }
            ServerLogReader errorStreamHandler =
                    new ServerLogReader("errorStream", tempProcess.getErrorStream());
            inputStreamHandler = new ServerLogReader("inputStream", tempProcess.getInputStream());

            /* start the stream readers*/
            inputStreamHandler.start();
            errorStreamHandler.start();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        shutdown();
                    } catch (Exception e) {
                        log.error("Error while shutting down server ..", e);
                    }
                }
            });
            System.setProperty("user.dir", carbonHome);
            //String firstProductName = AutomationConfiguration.getFirstProductGroup().getValue("name");
            //String firstStandalone = AutomationConfiguration.getAllStandaloneInstances().get(0);
            //get the host name of first standalone node
            String hostName = "localhost";
            ClientConnectionUtil.waitForPort(Integer.parseInt(ExtentionConstants.
                    SERVER_DEFAULT_HTTPS_PORT) + portOffset,
                    DEFAULT_START_STOP_WAIT_MS, false, hostName);
            //wait until Mgt console url printed.
            long time = System.currentTimeMillis() + 60 * 1000;
            while (!inputStreamHandler.getOutput().contains(SERVER_STARTUP_MESSAGE) &&
                    System.currentTimeMillis() < time) {
                // wait until server startup is completed
            }
            //ClientConnectionUtil.waitForLogin();
        } catch (IOException e) {
            throw new RuntimeException("Unable to start server :", e);
        }
        process = tempProcess;
    }

    public synchronized void shutdown() throws Exception {
        if (process != null) {
          if (ClientConnectionUtil.isPortOpen(Integer.parseInt(ExtentionConstants.
                    SERVER_DEFAULT_HTTPS_PORT) + portOffset, "loacalhost")) {
                String executionEnvironment = AutomationConfiguration.getExecutionEnvironment();
                if (executionEnvironment.equals(Platforms.product.name())) {
                    //fix this
                    String backendURL = context.getContextUrls().getBackEndUrl();
                    String sessionCookie = context.login();
                /*    String sessionCookie = ContextUtills.
                            getAdminUserSessionCookie(productGroupName, instance);
                    String backendURL = UrlGenerationUtil.getBackendURL(productGroupName, instance);*/
                    ServerAdminServiceClient serverAdminServiceClient = new ServerAdminServiceClient(backendURL, sessionCookie);
                    serverAdminServiceClient.shutdown();
                    long time = System.currentTimeMillis() + DEFAULT_START_STOP_WAIT_MS;
                    while (!inputStreamHandler.getOutput().contains(SERVER_SHUTDOWN_MESSAGE) &&
                            System.currentTimeMillis() < time) {
                        // wait until server shutdown is completed
                    }
                    log.info("Server stopped successfully...");
                }
                tempProcess.destroy();
                process.destroy();
                log.info("Server stopped successfully...");
                if (isCoverageEnable) {
                    List<File> list = new ArrayList<File>();
                    list.add(new File(carbonHome));
                    //  CodeCoverageUtils.generateReports(list);
                }
                if (portOffset == 0) {
                    System.clearProperty(ServerConstants.CARBON_HOME);
                }
            }
            inputStreamHandler.stop();
        }
    }

    private String expandServerStartupCommandList(Map<String, String> commandMap) {
        StringBuilder keyValueBuffer = new StringBuilder();
        String keyValueArray;
        if (commandMap.isEmpty()) {
            commandMap.put(ExtentionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND, "0");
        }
        Set<Map.Entry<String, String>> entries = commandMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null || value.isEmpty()) {
                keyValueBuffer.append(key).append(",");
            } else {
                keyValueBuffer.append(key).append("=").append(value).append(",");
            }
        }
        keyValueArray = keyValueBuffer.toString();
        keyValueArray = keyValueArray.substring(0, keyValueArray.length() - 1);
        return keyValueArray;
    }
}
