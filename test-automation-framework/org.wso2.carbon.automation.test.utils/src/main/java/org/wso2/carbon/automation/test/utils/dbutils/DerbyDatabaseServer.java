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
package org.wso2.carbon.automation.test.utils.dbutils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.derby.drda.NetworkServerControl;

/**
 * Managing derby database executions
 * Sample JDBC URL = "jdbc:derby://localhost:1527/myDB;create=true"
 */
public class DerbyDatabaseServer {
    private static final Log log = LogFactory.getLog(DerbyDatabaseServer.class);
    private NetworkServerControl networkServerControl = null;
    private boolean isServerStarted = false;

    /**
     * Method will start derby server
     *
     * @throws Exception exception
     */
    public void start() throws Exception {
        if (!isServerRunning()) {
            networkServerControl = new NetworkServerControl();
            networkServerControl.start(null);
            isServerStarted = true;
            log.info("Derby Database Server started");
        }
    }

    /**
     * method will stop derby server which started before
     *
     * @throws Exception exception
     */
    public void stop() throws Exception {
        if (isServerRunning()) {
            networkServerControl.shutdown();
            isServerStarted = false;
            log.info("Derby Database Server Shutdown");
        }
    }

    public boolean isServerRunning() {
        return isServerStarted;
    }

    public String getJdbcUrl() {
        String jdbc = null;
        if (isServerRunning()) {
            try {
                jdbc = "jdbc:derby:" + networkServerControl.getCurrentProperties().getProperty("derby.drda.host")
                        + ":" + networkServerControl.getCurrentProperties().getProperty("derby.drda.portNumber") + "/";
            } catch (Exception e) {
                log.error(e);
            }
        }
        return jdbc;
    }
}
