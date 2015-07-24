/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.automation.remotecoverage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.UnknownHostException;

public class RemoteCoverageServer {
    private static Log log = LogFactory.getLog(RemoteCoverageServer.class);

    public static void main(String[] args) throws UnknownHostException {
        int port = 6330;
        String hostName = null;
        String coverageFilePath = System.getProperty("java.io.tmpdir") + "jacoco-server"
                                  + System.currentTimeMillis() + ".exec";

        if (args.length == 0) {
            log.info("Proper Usage is: [port, coverage dump file location, IP address/hostname]");
            System.exit(0);
        }

        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.error("Port " + args[0] + " must be an integer.");
                System.exit(1);
            }
        }

        if (args.length >= 2) {
            if (!args[1].isEmpty()) {
                coverageFilePath = args[1];
            }
        }

        if (args.length >= 3) {
            hostName = args[2];
            if (hostName.isEmpty()) {
                hostName = null;
            }
        }

        CoverageDataServer remoteCoverageServer = new CoverageDataServer(port, coverageFilePath, hostName);
        remoteCoverageServer.startServer();
    }
}
