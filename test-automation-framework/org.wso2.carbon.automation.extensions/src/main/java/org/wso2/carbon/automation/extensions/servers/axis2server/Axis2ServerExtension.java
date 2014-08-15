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
package org.wso2.carbon.automation.extensions.servers.axis2server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.extensions.ExecutionListenerExtension;

public class Axis2ServerExtension extends ExecutionListenerExtension {
    private Axis2ServerManager axis2ServerManager;
    public static final String SIMPLE_STOCK_QUOTE_SERVICE = "SimpleStockQuoteService";
    public static final String SECURE_STOCK_QUOTE_SERVICE = "SecureStockQuoteService";
    public static final String LB_SERVICE_1 = "LBService1";
    public static final String SIMPLE_AXIS2_SERVICE = "Axis2Service";
    private static final Log log = LogFactory.getLog(Axis2ServerExtension.class);

    public void initiate() throws Exception {
    }

    public void onExecutionStart() throws Exception {
        axis2ServerManager = new Axis2ServerManager();
        log.info("Starting Simple Axis2 server");
        axis2ServerManager.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
            log.info("ignored");
        }
        log.info("Deploying services to Axis2 server");
        if (axis2ServerManager.isStarted()) {
            axis2ServerManager.deployService(SIMPLE_STOCK_QUOTE_SERVICE);
            axis2ServerManager.deployService(SECURE_STOCK_QUOTE_SERVICE);
            axis2ServerManager.deployService(LB_SERVICE_1);
            axis2ServerManager.deployService(SIMPLE_AXIS2_SERVICE);
        }
    }

    public void onExecutionFinish() throws Exception {
        log.info("Stopping Simple Axis2 server");
        if (axis2ServerManager.isStarted()) {
            axis2ServerManager.stop();
        }
    }
}
