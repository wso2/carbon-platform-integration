/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.automation.extensions.servers.carbonserver;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.CodeCoverageUtils;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.engine.frameworkutils.ReportGenerator;
import org.wso2.carbon.automation.engine.frameworkutils.TestFrameworkUtils;
import org.wso2.carbon.automation.extensions.ExtensionConstants;
import org.wso2.carbon.automation.extensions.servers.utils.ArchiveExtractor;
import org.wso2.carbon.automation.extensions.servers.utils.ClientConnectionUtil;
import org.wso2.carbon.automation.extensions.servers.utils.FileManipulator;
import org.wso2.carbon.automation.extensions.servers.utils.InputStreamHandler;
import org.wso2.carbon.automation.extensions.servers.utils.ServerLogReader;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * A set of utility methods such as starting & stopping a Carbon server.
 */
public class CarbonServerManager {
    private static final Log log = LogFactory.getLog(CarbonServerManager.class);
    private Process process;
    private String carbonHome;
    private AutomationContext automationContext;
    private ServerLogReader inputStreamHandler;
    private boolean isCoverageEnable = false;
    private static final String SERVER_SHUTDOWN_MESSAGE = "Halting JVM";
    private static final String SERVER_STARTUP_MESSAGE = "Mgt Console URL";
    private static final long DEFAULT_START_STOP_WAIT_MS = 1000 * 60 * 5;
    private static final String CMD_ARG = "cmdArg";
    private static int defaultHttpPort = Integer.parseInt(FrameworkConstants.SERVER_DEFAULT_HTTP_PORT);
    private static int defaultHttpsPort = Integer.parseInt(FrameworkConstants.SERVER_DEFAULT_HTTPS_PORT);


    public CarbonServerManager(AutomationContext context) {
        this.automationContext = context;
    }

    public synchronized void startServerUsingCarbonHome(String carbonHome,
                                                        Map<String, String> commandMap)
            throws AutomationFrameworkException {
        if (process != null) { // An instance of the server is running
            return;
        }
        final int portOffset = checkPortAvailability(commandMap);
        Process tempProcess = null;

        try {
            isCoverageEnable = Boolean.parseBoolean(automationContext.getConfigurationValue("//coverage"));
        } catch (XPathExpressionException e) {
            throw new AutomationFrameworkException("Coverage configuration not found in automation.xml", e);
        }
        try {
            if (!commandMap.isEmpty()) {
                if (getPortOffsetFromCommandMap(commandMap) == 0) {
                    System.setProperty(ExtensionConstants.CARBON_HOME, carbonHome);
                    System.setProperty("user.dir", carbonHome);
                }
            }
            File commandDir = new File(carbonHome);

            log.info("Starting carbon server............. ");
            String scriptName = TestFrameworkUtils.getStartupScriptFileName(carbonHome);
            String[] parameters = expandServerStartupCommandList(commandMap);

            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                commandDir = new File(carbonHome + File.separator + "bin");
                String[] cmdArray;

                if (isCoverageEnable) {
                    cmdArray = new String[]{"cmd.exe", "/c", scriptName + ".bat"};
                    cmdArray = mergePropertiesToCommandArray(parameters, cmdArray);
                } else {
                    cmdArray = new String[]{"cmd.exe", "/c", scriptName + ".bat"};
                    cmdArray = mergePropertiesToCommandArray(parameters, cmdArray);
                }
                tempProcess = Runtime.getRuntime().exec(cmdArray, null, commandDir);

            } else {
                String[] cmdArray;
                if (isCoverageEnable) {
                    cmdArray = new String[]{"sh", "bin/" + scriptName + ".sh"};
                    cmdArray = mergePropertiesToCommandArray(parameters, cmdArray);

                } else {
                    cmdArray = new String[]{"sh", "bin/" + scriptName + ".sh"};
                    cmdArray = mergePropertiesToCommandArray(parameters, cmdArray);
                }
                tempProcess = Runtime.getRuntime().exec(cmdArray, null, commandDir);
            }

            InputStreamHandler errorStreamHandler =
                    new InputStreamHandler("errorStream", tempProcess.getErrorStream());
            inputStreamHandler = new ServerLogReader("inputStream", tempProcess.getInputStream());
            // start the stream readers
            inputStreamHandler.start();
            errorStreamHandler.start();

            //register shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        serverShutdown(portOffset);
                    } catch (Exception e) {
                        log.error("Error while server shutdown ..", e);
                    }
                }
            });


            ClientConnectionUtil.waitForPort(defaultHttpPort + portOffset,
                                             DEFAULT_START_STOP_WAIT_MS, false,
                                             automationContext.getInstance().getHosts().get("default"));

            //wait until Mgt console url printed.
            long time = System.currentTimeMillis() + 60 * 1000;
            while (!inputStreamHandler.getOutput().contains(SERVER_STARTUP_MESSAGE) &&
                   System.currentTimeMillis() < time) {
                // wait until server startup is completed
            }

            ClientConnectionUtil.waitForLogin(automationContext);
            log.info("Server started successfully.");

        } catch (IOException e) {
            throw new IllegalStateException("Unable to start server", e);
        } catch (XPathExpressionException e) {
            throw new IllegalStateException("Unable to start server", e);
        }
        process = tempProcess;
    }

    private int checkPortAvailability(Map<String, String> commandMap)
            throws AutomationFrameworkException {
        final int portOffset = getPortOffsetFromCommandMap(commandMap);

        //check whether http port is already occupied
        if (ClientConnectionUtil.isPortOpen(defaultHttpPort + portOffset)) {
            throw new AutomationFrameworkException("Unable to start carbon server on port " +
                                                   (defaultHttpPort + portOffset) + " : Port already in use");
        }
        //check whether https port is already occupied
        if (ClientConnectionUtil.isPortOpen(defaultHttpsPort + portOffset)) {
            throw new AutomationFrameworkException("Unable to start carbon server on port " +
                                                   (defaultHttpsPort + portOffset) + " : Port already in use");
        }
        return portOffset;
    }



    private String[] mergePropertiesToCommandArray(String[] parameters, String[] cmdArray) {
        if (parameters != null) {
            cmdArray = mergerArrays(cmdArray, parameters);
        }
        return cmdArray;
    }

    public synchronized String setUpCarbonHome(String carbonServerZipFile)
            throws IOException {
        if (process != null) { // An instance of the server is running
            return carbonHome;
        }
        int indexOfZip = carbonServerZipFile.lastIndexOf(".zip");
        if (indexOfZip == -1) {
            throw new IllegalArgumentException(carbonServerZipFile + " is not a zip file");
        }
        String fileSeparator = (File.separator.equals("\\")) ? "\\" : "/";
        if (fileSeparator.equals("\\")) {
            carbonServerZipFile = carbonServerZipFile.replace("/", "\\");
        }
        String extractedCarbonDir =
                carbonServerZipFile.substring(carbonServerZipFile.lastIndexOf(fileSeparator) + 1,
                                              indexOfZip);
        FileManipulator.deleteDir(extractedCarbonDir);
        String extractDir = "carbontmp" + System.currentTimeMillis();
        String baseDir = (System.getProperty("basedir", ".")) + File.separator + "target";
        log.info("Extracting carbon zip file.. ");
        new ArchiveExtractor().extractFile(carbonServerZipFile, baseDir + File.separator + extractDir);
        return carbonHome =
                new File(baseDir).getAbsolutePath() + File.separator + extractDir + File.separator +
                extractedCarbonDir;
    }


    public synchronized void serverShutdown(int portOffset) throws AutomationFrameworkException {
        if (process != null) {
            log.info("Shutting down server..");
            if (ClientConnectionUtil.isPortOpen(Integer.parseInt(
                    ExtensionConstants.SERVER_DEFAULT_HTTPS_PORT) + portOffset)) {

                int httpsPort = defaultHttpsPort + portOffset;
                String url = null;
                try {
                    url = automationContext.getContextUrls().getBackEndUrl();
                } catch (XPathExpressionException e) {
                    throw new AutomationFrameworkException("Get context failed", e);
                }
                String backendURL = url.replaceAll("(:\\d+)", ":" + httpsPort);

                try {
                    ClientConnectionUtil.sendForcefulShutDownRequest(
                            backendURL,
                            automationContext.getSuperTenant().getContextUser().getUserName(),
                            automationContext.getSuperTenant().getContextUser().getPassword());
                } catch (AutomationFrameworkException e) {
                    throw new AutomationFrameworkException("Get context failed", e);
                } catch (XPathExpressionException e) {
                    throw new AutomationFrameworkException("Get context failed", e);
                }

                long time = System.currentTimeMillis() + DEFAULT_START_STOP_WAIT_MS;
                while (!inputStreamHandler.getOutput().contains(SERVER_SHUTDOWN_MESSAGE) &&
                       System.currentTimeMillis() < time) {
                    // wait until server shutdown is completed
                }
                log.info("Server stopped successfully...");
                inputStreamHandler.stop();
                process.destroy();
                process = null;
                //generate coverage report
                if (isCoverageEnable) {
                    try {
                        log.info("Generating Jacoco code coverage...");
                        generateCoverageReport(
                                new File(carbonHome + File.separator + "repository" +
                                         File.separator + "components" + File.separator + "plugins" + File.separator));
                    } catch (IOException e) {
                        log.error("Failed to generate code coverage ", e);
                        throw new AutomationFrameworkException("Failed to generate code coverage ", e);
                    }
                }
                if (portOffset == 0) {
                    System.clearProperty(ExtensionConstants.CARBON_HOME);
                }
            }
        }
    }

    private void generateCoverageReport(File classesDir)
            throws IOException, AutomationFrameworkException {

        CodeCoverageUtils.executeMerge(FrameworkPathUtil.getJacocoCoverageHome());
        ReportGenerator reportGenerator =
                new ReportGenerator(new File(FrameworkPathUtil.getCoverageMergeFilePath()),
                                    classesDir,
                                    new File(FrameworkPathUtil.getCoverageDirPath()),
                                    null);
        reportGenerator.create();

        log.info("Jacoco coverage dump file path : " + FrameworkPathUtil.getCoverageDumpFilePath());
        log.info("Jacoco class file path : " + classesDir);
        log.info("Jacoco coverage HTML report path : " + FrameworkPathUtil.getCoverageDirPath() + File.separator + "index.html");
    }

    public synchronized void restartGracefully() throws AutomationFrameworkException {

        try {
            ClientConnectionUtil.sendGraceFullRestartRequest(
                    automationContext.getContextUrls().getSecureServiceUrl(),
                    automationContext.getSuperTenant().getContextUser().getUserName(),
                    automationContext.getSuperTenant().getContextUser().getPassword());
        } catch (XPathExpressionException e) {
            throw new AutomationFrameworkException("restart failed", e);
        }

        long time = System.currentTimeMillis() + DEFAULT_START_STOP_WAIT_MS;
        while (!inputStreamHandler.getOutput().contains(SERVER_SHUTDOWN_MESSAGE) &&
               System.currentTimeMillis() < time) {
            // wait until server shutdown is completed
        }

        time = System.currentTimeMillis();

        while (System.currentTimeMillis() < time + 5000) {
            //wait for port to close
        }

        try {
            ClientConnectionUtil.waitForPort(
                    Integer.parseInt(automationContext.getInstance().getPorts().get("https")),
                    automationContext.getInstance().getHosts().get("default"));

            ClientConnectionUtil.waitForLogin(automationContext);

        } catch (XPathExpressionException e) {
            throw new AutomationFrameworkException("Connection attempt to carbon server failed", e);
        }
    }


    private String[] expandServerStartupCommandList(Map<String, String> commandMap) {
        if (commandMap == null || commandMap.size() == 0) {
            return null;
        }
        String[] cmdParaArray = null;
        String cmdArg = null;
        if (commandMap.containsKey(CMD_ARG)) {
            cmdArg = commandMap.get(CMD_ARG);
            cmdParaArray = cmdArg.trim().split("\\s+");
            commandMap.remove(CMD_ARG);
        }
        String[] parameterArray = new String[commandMap.size()];
        int arrayIndex = 0;
        Set<Map.Entry<String, String>> entries = commandMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String parameter;
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null || value.isEmpty()) {
                parameter = key;
            } else {
                parameter = key + "=" + value;
            }
            parameterArray[arrayIndex++] = parameter;
        }
        //setting cmdArg again
        if (cmdArg != null) {
            commandMap.put(CMD_ARG, cmdArg);
        }
        if (cmdParaArray == null || cmdParaArray.length == 0) {
            return parameterArray;
        } else {
            return ArrayUtils.addAll(parameterArray, cmdParaArray);
        }
    }

    private int getPortOffsetFromCommandMap(Map<String, String> commandMap) {
        if (commandMap.containsKey(ExtensionConstants.PORT_OFFSET_COMMAND)) {
            return Integer.parseInt(commandMap.get(
                    ExtensionConstants.PORT_OFFSET_COMMAND));
        } else {
            return 0;
        }
    }

    private String[] mergerArrays(String[] array1, String[] array2) {
        return ArrayUtils.addAll(array1, array2);
    }
}
