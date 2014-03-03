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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;

public class ClientConnectionUtil {
    private static final Log log = LogFactory.getLog(ClientConnectionUtil.class);
    private static final long TIMEOUT = 180000;

    /**
     * Wait for sometime until it is possible to login to the Carbon server
     */
    public static void waitForLogin(String backendURL, String domain, String username, String password)
            throws MalformedURLException, LoginAuthenticationExceptionException {
        long startTime = System.currentTimeMillis();
        boolean loginFailed = true;
        while (((System.currentTimeMillis() - startTime) < TIMEOUT) && loginFailed) {
            log.info("Waiting to login  user...");
            try {
                new LoginLogoutUtil(backendURL).login(domain, username, password);
                loginFailed = false;
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Login failed after server startup", e);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                    // Nothing to do
                }
            }
        }
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
    public static boolean isPortOpen(int port, String hostName) {
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
}
