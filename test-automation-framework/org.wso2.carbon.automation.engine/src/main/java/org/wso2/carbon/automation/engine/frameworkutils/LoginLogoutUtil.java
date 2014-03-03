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
package org.wso2.carbon.automation.engine.frameworkutils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.adminclients.AuthenticationAdminClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * A utility for logging into & logging out of Carbon servers
 */
public final class LoginLogoutUtil {
    private static final Log log = LogFactory.getLog(LoginLogoutUtil.class);
    private String sessionCookie;
    private int port;
    private String hostName;
    private String backendURL;

    public LoginLogoutUtil(String backendURL) throws MalformedURLException {
        URL backend = new URL(backendURL);
        this.backendURL = backendURL;
        this.port = backend.getPort();
        this.hostName = backend.getHost();
    }

    /**
     * Log in to a Carbon server
     *
     * @param userName - login user name
     * @param password - login password
     * @return The session cookie on successful login
     */
/*    public String login(String domain, String userName, String password)
            throws LoginAuthenticationExceptionException, IOException, XMLStreamException, URISyntaxException, SAXException, NonExistenceException, ConfigurationMismatchException {
        FrameworkUtil.setKeyStoreProperties();
        ClientConnectionUtil.waitForPort(port, hostName);
        AuthenticationAdminClient loginClient = new AuthenticationAdminClient(backendURL);
        return loginClient.login(domain, userName, password, hostName);
    }*/

    /**
     * Log out from carbon server
     */
    public void logout() throws LogoutAuthenticationExceptionException, RemoteException {
        AuthenticationAdminClient logoutClient = new AuthenticationAdminClient(hostName);
        logoutClient.logOut();
    }
}

