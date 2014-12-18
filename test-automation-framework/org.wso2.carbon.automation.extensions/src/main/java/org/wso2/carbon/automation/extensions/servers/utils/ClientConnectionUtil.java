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
package org.wso2.carbon.automation.extensions.servers.utils;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.context.AutomationContext;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;

public class ClientConnectionUtil {
    private static final Log log = LogFactory.getLog(ClientConnectionUtil.class);
    private static final long TIMEOUT = 120000;

    /**
     * Wait for sometime until it is possible to login to the Carbon server
     */
    public static void waitForLogin(AutomationContext context) throws Exception {

        waitForLogin(context.getContextUrls().getSecureServiceUrl(), context.getSuperTenant().getTenantAdmin());
//        long startTime = System.currentTimeMillis();
//        boolean loginSuccess = false;
//        String superAdminName;
//
//        try {
//
//            superAdminName = context.getSuperTenant().getTenantAdmin().getUserName();
//            String superAdminPassword = context.getSuperTenant().getTenantAdmin().getPassword();
//            String hostName = context.getDefaultInstance().getHosts().get("default");
//            String endpointURL = context.getContextUrls().getSecureServiceUrl();
//
//            while (((System.currentTimeMillis() - startTime) < TIMEOUT) && !loginSuccess) {
//                log.info("Waiting for user login...");
//                try {
//                    loginSuccess =
//                            checkAuthenticationAdminService(createPayLoad(superAdminName, superAdminPassword, hostName),
//                                                            endpointURL);
//                    if (!loginSuccess) {
//                        Thread.sleep(1000);
//                    }
//                } catch (Exception e) {
//                    if (log.isDebugEnabled()) {
//                        log.debug("Login failed after server startup ", e);
//                    }
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException ignored) {
//                        //ignored because of login attempts which could happen before proper carbon
//                        //server startup
//                    }
//                }
//            }
//        } catch (XPathExpressionException e) {
//            log.error("unable to get admin information from automation context ", e);
//            throw new Exception("unable to get admin information from automation context ", e);
//        }
//        if (!loginSuccess) {
//            throw new AutomationFrameworkException("Login failed for user " + superAdminName + " while verifying server startup" +
//                                                   ". Please make sure that server is up and running or user is a valid user");
//        }
    }

    /**
     * Wait for sometime until it is possible to login to the Carbon server
     */
    public static void waitForLogin(String backendUrl, User userInfo) throws Exception {

        long startTime = System.currentTimeMillis();
        boolean loginSuccess = false;
        String superAdminName = userInfo.getUserName();
        String superAdminPassword = userInfo.getPassword();
        String hostName;

        //try to get the current machine IP address to send with authentication request as parameter
        try {
            hostName = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Can not retrieve machine IP address. Setting it to 127.0.0.1");
            hostName = "127.0.0.1";
        }

        while (((System.currentTimeMillis() - startTime) < TIMEOUT) && !loginSuccess) {
            log.info("Waiting for user login...");
            try {
                loginSuccess =
                        checkAuthenticationAdminService(createPayLoad(superAdminName, superAdminPassword, hostName),
                                                        backendUrl);
                if (!loginSuccess) {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Login failed after server startup ", e);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                    //ignored because of login attempts which could happen before proper carbon
                    //server startup
                }
            }
        }

        if (!loginSuccess) {
            throw new AutomationFrameworkException("Login failed for user " + superAdminName + " while verifying server startup" +
                                                   ". Please make sure that server is up and running or user is a valid user");
        }
    }

    @Deprecated
    public static boolean sendAdminServiceRequest(OMElement payload, String endpointURL)
            throws Exception {

        try {
            ServiceClient serviceClient = new ServiceClient();
            Options opts = new Options();
            opts.setTo(new EndpointReference(endpointURL + "/AuthenticationAdmin"));
            log.info(endpointURL);
            opts.setAction("urn:login");
            serviceClient.setOptions(opts);
            OMElement result = serviceClient.sendReceive(payload);

            if (result.toString().contains("<ns:return>true</ns:return>")) {
                log.info("Login was successful..");
                String sessionCookie = (String) serviceClient.getServiceContext().getProperty(HTTPConstants.COOKIE_STRING);
                log.info(sessionCookie);
                return false;
            }

        } catch (AxisFault e) {
            log.error("Unable to login as user..");
        }
        return true;
    }

    /**
     * sending authentication request and return true if the user login is successful
     *
     * @param payload
     * @param endpointURL
     * @return
     * @throws Exception
     */
    public static boolean checkAuthenticationAdminService(OMElement payload, String endpointURL)
            throws Exception {

        try {
            ServiceClient serviceClient = new ServiceClient();
            Options opts = new Options();
            opts.setTo(new EndpointReference(endpointURL + "/AuthenticationAdmin"));
            log.info(endpointURL);
            opts.setAction("urn:login");
            serviceClient.setOptions(opts);
            OMElement result = serviceClient.sendReceive(payload);

            if (result.toString().contains("<ns:return>true</ns:return>")) {
                log.info("Login was successful..");
                String sessionCookie = (String) serviceClient.getServiceContext().getProperty(HTTPConstants.COOKIE_STRING);
                log.info(sessionCookie);
                return true;
            }

        } catch (AxisFault e) {
            log.error("Unable to login as user..");
        }
        return false;
    }

    /**
     * @param port    The port that needs to be checked
     * @param timeout The timeout waiting for the port to open
     * @param verbose if verbose is set to true,
     * @throws RuntimeException if the port is not opened within the {@link #TIMEOUT}
     */
    public static void waitForPort(int port, long timeout, boolean verbose, String hostName)
            throws RuntimeException {
        long startTime = System.currentTimeMillis();
        boolean isPortOpen = false;
        while (!isPortOpen && (System.currentTimeMillis() - startTime) < timeout) {
            Socket socket = null;
            try {
                InetAddress address = InetAddress.getByName(hostName);
                socket = new Socket(address, port);
                isPortOpen = socket.isConnected();
                if (isPortOpen) {
                    if (verbose) {
                        log.info("Successfully connected to the server on port " + port);
                    }
                    return;
                }
            } catch (IOException e) {
                if (verbose) {
                    log.info("Waiting until server starts on port " + port);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            } finally {
                try {
                    if ((socket != null) && (socket.isConnected())) {
                        socket.close();
                    }
                } catch (IOException e) {
                    log.error("Can not close the socket with is used to check the server status ",
                              e);
                }
            }
        }
        throw new RuntimeException("Port " + port + " is not open");
    }

    /**
     * Checks whether the given <code>port</code> is open, and waits for sometime until the port is
     * open. If the port is not open within {@link #TIMEOUT}, throws RuntimeException.
     *
     * @param port The port that needs to be checked
     * @throws RuntimeException if the port is not opened within the {@link #TIMEOUT}
     */
    public static void waitForPort(int port, String hostName) {
        waitForPort(port, TIMEOUT, true, hostName);
    }

    /**
     * Check whether the provided <code>port</code> is open
     *
     * @param port The port that needs to be checked
     * @return true if the <code>port</code> is open & false otherwise
     */
    public static boolean isPortOpen(int port) {
        Socket socket = null;
        boolean isPortOpen = false;
        try {
            InetAddress address = InetAddress.getLocalHost();
            socket = new Socket(address, port);
            isPortOpen = socket.isConnected();
            if (isPortOpen) {
                log.info("Successfully connected to the server on port " + port);
            }
        } catch (IOException e) {
            log.info("Port " + port + " is Closed");
            isPortOpen = false;
        } finally {
            try {
                if ((socket != null) && (socket.isConnected())) {
                    socket.close();
                }
            } catch (IOException e) {
                log.error("Can not close the socket with is used to check the server status ", e);
            }
        }
        return isPortOpen;
    }

    /**
     * Generate OM Payload to invoke AuthenticationAdmin service.
     *
     * @param userNameOfAdmin - Name of the super admin user
     * @param passwordOfAdmin - Password of the user
     * @param hostName        - HostName of the user
     * @return - payload generated
     */
    public static OMElement createPayLoad(String userNameOfAdmin, String passwordOfAdmin,
                                          String hostName) {

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs =
                fac.createOMNamespace("http://authentication.services.core.carbon.wso2.org", "aut");

        OMElement loginRoot = fac.createOMElement("login", omNs);
        OMElement username = fac.createOMElement("username", omNs);
        OMElement password = fac.createOMElement("password", omNs);
        OMElement remoteAddress = fac.createOMElement("remoteAddress", omNs);

        username.setText(userNameOfAdmin);
        password.setText(passwordOfAdmin);
        remoteAddress.setText(hostName);

        loginRoot.addChild(username);
        loginRoot.addChild(password);
        loginRoot.addChild(remoteAddress);

        return loginRoot;
    }

    public static void sendForcefulShutDownRequest(String backendURL, String userName,
                                                   String password) throws Exception {
        try {
            ServiceClient serviceClient = new ServiceClient();
            Options opts = new Options();
            opts.setManageSession(true);
            opts.setTo(new EndpointReference(backendURL + "ServerAdmin"));
            opts.setAction("urn:shutdown");
            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setUsername(userName);
            auth.setPassword(password);
            opts.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, auth);

            serviceClient.setOptions(opts);
            serviceClient.sendReceive(ClientConnectionUtil.createPayLoadShutDownServerForcefully());

        } catch (AxisFault e) {
            log.error("Unable to shutdown carbon server forcefully..", e);
            throw new Exception("Unable to shutdown carbon server forcefully..", e);
        }
    }

    public static void sendGraceFullRestartRequest(String backendURL, String userName,
                                                   String password) throws Exception {
        try {
            ServiceClient serviceClient = new ServiceClient();
            Options opts = new Options();
            opts.setManageSession(true);
            opts.setTo(new EndpointReference(backendURL + "/ServerAdmin"));
            opts.setAction("urn:restart");
            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setUsername(userName);
            auth.setPassword(password);
            opts.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, auth);

            serviceClient.setOptions(opts);
            serviceClient.sendReceive(ClientConnectionUtil.createPayLoadRestartServerGracefully());

        } catch (AxisFault e) {
            log.error("Unable to restart carbon server gracefully..", e);
            throw new Exception("Unable to restart carbon server gracefully..", e);
        }
    }

    /**
     * Generate OM Payload to invoke ServerAdmin service and shutdown server forcefully.
     *
     * @return - payload generated
     */
    public static OMElement createPayLoadShutDownServerForcefully() {

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://org.apache.axis2/xsd", "xsd");
        return fac.createOMElement("shutdown", omNs);
    }

    /**
     * Generate OM Payload to invoke ServerAdmin service and restart server gracefully.
     *
     * @return - payload generated
     */
    public static OMElement createPayLoadRestartServerGracefully() {

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://org.apache.axis2/xsd", "xsd");
        return fac.createOMElement("restart", omNs);
    }
}
