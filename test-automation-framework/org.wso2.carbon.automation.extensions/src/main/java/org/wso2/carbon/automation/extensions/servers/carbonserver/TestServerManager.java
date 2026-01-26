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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.extensions.ExtensionConstants;
import org.wso2.carbon.automation.extensions.XPathConstants;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestServerManager {
    public static final String HEADER_OUTPUT_ADAPTER_EMAIL = "[output_adapter.email]";
    public static final String HEADER_SYSTEM_PARAMETER = "[system.parameter]";
    public static final String HEADER_SERVER = "[server]";
    public static final String CONFIG_ENABLE_V2_AUDIT_LOGS = "enableV2AuditLogs";
    public static final String CONFIG_DIAGNOSTIC_LOG_MODE = "diagnostic_log_mode";
    protected CarbonServerManager carbonServer;
    protected AutomationContext context;
    protected String carbonZip;
    protected int portOffset;
    protected Map<String, String> commandMap = new HashMap<String, String>();
    private static final Log log = LogFactory.getLog(TestServerManager.class);
    protected String carbonHome;

    public TestServerManager(AutomationContext context) {
        carbonServer = new CarbonServerManager(context);
        this.context = context;
    }

    public TestServerManager(AutomationContext context, String carbonZip) {
        carbonServer = new CarbonServerManager(context);
        this.context = context;
        this.carbonZip = carbonZip;
    }

    public TestServerManager(AutomationContext context, int portOffset) {
        carbonServer = new CarbonServerManager(context);
        this.portOffset = portOffset;
        this.context = context;
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
        this.context = context;
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

        Path deploymentTomlPath = Paths.get(carbonHome, "repository", "conf", "deployment.toml");
        try {
            if (context == null) {
                context = new AutomationContext();
            }
            Node emailSenderConfigs = context.getConfigurationNode(XPathConstants.EMAIL_SENDER_CONFIGS);
            if (emailSenderConfigs != null) {

                StringBuilder configString = new StringBuilder();
                configString.append("\n").append(HEADER_OUTPUT_ADAPTER_EMAIL).append("\n");
                NodeList childNodes = emailSenderConfigs.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        String nodeName = node.getNodeName();
                        String nodeValue = node.getTextContent();
                        configString.append(nodeName).append("= \"").append(nodeValue).append("\"\n");
                    }
                }

                Files.write(deploymentTomlPath, configString.toString().getBytes(), StandardOpenOption.APPEND);
            }

            // Enable admin/soap services.
            enableAdminServices(deploymentTomlPath);

            Node loggingConfigs = context.getConfigurationNode(XPathConstants.LOGGING_CONFIGS);
            if (loggingConfigs != null) {
                StringBuilder v2auditLogConfigString = new StringBuilder();
                StringBuilder diagnosticLogConfigString = new StringBuilder();
                NodeList childNodes = loggingConfigs.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        if (node.getNodeName().equals(CONFIG_ENABLE_V2_AUDIT_LOGS)) {
                            v2auditLogConfigString.append(node.getNodeName()).append(" = \"").append(node.getTextContent()).append("\"");
                        }
                        if (node.getNodeName().equals(CONFIG_DIAGNOSTIC_LOG_MODE)) {
                            diagnosticLogConfigString.append(node.getNodeName()).append(" = \"").append(node.getTextContent()).append("\"");
                        }
                    }
                }

                // Append the v2 audit log config to the deployment.toml
                appendConfigs(deploymentTomlPath, HEADER_SYSTEM_PARAMETER, v2auditLogConfigString);
                // Append the diagnostic log config to the deployment.toml
                appendConfigs(deploymentTomlPath, HEADER_SERVER, diagnosticLogConfigString);
            }
        } catch (XPathExpressionException | IOException e) {
            throw new AutomationFrameworkException(e);
        }
    }

    private static void appendConfigs(Path deploymentTomlPath, String tomlHeader,
                                      StringBuilder configsToAppend) throws IOException {

        List<String> lines = Files.readAllLines(deploymentTomlPath);
        int insertAfterLine = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(tomlHeader)) {
                // Find the line has the header
                insertAfterLine = i + 1;
                break;
            }
        }
        if (insertAfterLine != -1) {
            // If the header is found, append the config after the header
            lines.add(insertAfterLine, configsToAppend.toString());
            Files.write(deploymentTomlPath, lines, StandardOpenOption.WRITE);
        } else {
            // If the header is not found, append the header and the config to the end of the file
            Files.write(deploymentTomlPath, ("\n" + tomlHeader + "\n").getBytes(),
                    StandardOpenOption.APPEND);
            Files.write(deploymentTomlPath, configsToAppend.toString().getBytes(), StandardOpenOption.APPEND);
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
            configureServer();
        }
        log.info("Carbon Home - " + carbonHome);
        if (commandMap.get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND) != null) {
            this.portOffset = Integer.parseInt(commandMap.get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND));
        } else {
            this.portOffset = 0;
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

    /**
     * Adds or updates a single configuration entry in the deployment.toml file under a given parent section.
     *
     * @param tomlPath Path to the deployment.toml file.
     * @param sectionHeader Header of the parent section (e.g., "[server]").
     * @param key  Configuration key to add or update.
     * @param value Value for the configuration key.
     * @throws IOException If an error occurs while reading or writing the file.
     */
    public void addOrUpdateConfigInToml(Path tomlPath, String sectionHeader, String key, String value)
            throws IOException {

        List<String> fileLines = Files.readAllLines(tomlPath, StandardCharsets.UTF_8);
        List<String> updatedLines = new ArrayList<>();

        // Indicates whether the current position is inside the target section.
        boolean insideTargetSection = false;
        // Indicates if the key already exists in the section.
        boolean keyAlreadyExists = false;

        // Iterate over all lines in the file
        for (String line : fileLines) {
            String trimmedLine = line.trim();

            if (insideTargetSection) {
                // Replace the value if the key already exists.
                if (trimmedLine.startsWith(key)) {
                    updatedLines.set(updatedLines.size() - 1, key + " = " + value);
                    keyAlreadyExists = true;
                    // Insert the key if it does not exist when reaching an empty line or new section header.
                } else if (trimmedLine.isEmpty() || (trimmedLine.startsWith("[")
                        && !trimmedLine.equals(sectionHeader))) {
                    if (!keyAlreadyExists) {
                        updatedLines.add(key + " = " + value);
                        keyAlreadyExists = true;
                    }
                    // Exiting the target section.
                    insideTargetSection = false;
                }
            }

            updatedLines.add(line);
            // Identify the start of the target section.
            if (trimmedLine.equals(sectionHeader)) {
                insideTargetSection = true;
                keyAlreadyExists = false;
            }
        }
        // Append the key if the section ends at EOF and the key was not added.
        if (insideTargetSection && !keyAlreadyExists) {
            updatedLines.add(key + " = " + value);
        }
        // Write the updated content back to the toml file.
        Files.write(tomlPath, updatedLines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Read <enableAdminServices> node from the automation.xml.
     *
     * @return  Parent Key, key and expected value.
     * @throws XPathExpressionException
     */
    private Map<String, String> readEnableAdminServicesConfig() throws XPathExpressionException {

        Map<String, String> configMap = new HashMap<>();
        Node enableAdminServicesNode = context.getConfigurationNode(XPathConstants.ENABLE_ADMIN_SERVICES);
        if (enableAdminServicesNode != null) {
            NodeList childNodes = enableAdminServicesNode.getChildNodes();
            if (childNodes != null) {
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if (node != null) {
                        switch (node.getNodeName()) {
                            case XPathConstants.ENABLE_ADMIN_SERVICES_PARENT_KEY:
                                configMap.put(XPathConstants.ENABLE_ADMIN_SERVICES_PARENT_KEY,
                                        node.getTextContent().trim());
                                break;
                            case XPathConstants.ENABLE_ADMIN_SERVICES_KEY:
                                configMap.put(XPathConstants.ENABLE_ADMIN_SERVICES_KEY, node.getTextContent().trim());
                                break;
                            case XPathConstants.ENABLE_ADMIN_SERVICES_VALUE:
                                configMap.put(XPathConstants.ENABLE_ADMIN_SERVICES_VALUE, node.getTextContent().trim());
                                break;
                        }
                    }
                }
            }
        }
        return configMap;
    }

    /**
     * Enable Admin services.
     *
     * @param tomlPath Toml paths
     * @throws XPathExpressionException
     * @throws IOException
     */
    private void enableAdminServices(Path tomlPath) throws XPathExpressionException, IOException {

        Map<String, String> config = readEnableAdminServicesConfig();
        if (!config.isEmpty()) {
            String sectionHeader = "[" + config.get(XPathConstants.ENABLE_ADMIN_SERVICES_PARENT_KEY) + "]";
            String key = config.get(XPathConstants.ENABLE_ADMIN_SERVICES_KEY);
            String value = config.get(XPathConstants.ENABLE_ADMIN_SERVICES_VALUE);
            addOrUpdateConfigInToml(tomlPath, sectionHeader, key, value);
        }
    }

}
