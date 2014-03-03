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

package org.wso2.carbon.automation.test.utils.server;

import org.wso2.carbon.automation.test.api.clients.server.admin.ServerAdminClient;
import org.wso2.carbon.automation.engine.frameworkutils.ClientConnectionUtil;
import org.wso2.carbon.automation.engine.frameworkutils.CodeCoverageUtils;
import org.wso2.carbon.utils.ServerConstants;

public class ServerManagementUtils {

    public synchronized void restartGracefully(ServerAdminClient serverAdminClient, int port,
                                         String hostName, String backendURL)
            throws Exception {

        serverAdminClient.restartGracefully();
        Thread.sleep(5000);//wait for port to close

        ClientConnectionUtil.waitForPort(port, hostName);
        //todo - wait for login
//        ClientConnectionUtil.waitForLogin(port, hostName, backendURL);
        CodeCoverageUtils.renameCoverageDataFile(System.getProperty(ServerConstants.CARBON_HOME));
        Thread.sleep(2000);
    }
}
