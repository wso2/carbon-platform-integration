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
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.ContextXpathConstants;
import org.wso2.carbon.automation.engine.extensions.interfaces.ExecutionListenerExtension;
import org.wso2.carbon.utils.ServerConstants;

import javax.xml.xpath.XPathExpressionException;

public class CarbonServerExtension implements ExecutionListenerExtension {
    TestServerManager serverManager;
    private static final Log log = LogFactory.getLog(CarbonServerExtension.class);
    private String executionEnvironment;

    public void initiate() {
        try {
            AutomationContext context = new AutomationContext();
            serverManager = new TestServerManager(context);
            executionEnvironment =
                    context.getConfigurationValue(ContextXpathConstants.EXECUTION_ENVIRONMENT);
        } catch (XPathExpressionException e) {
            handleException("Error while initiating test environment", e);
        }
    }

    public void onExecutionStart() {
        try {
            if (executionEnvironment.equalsIgnoreCase(ExecutionEnvironment.STANDALONE.name())) {
                String carbonHome = serverManager.startServer();
                System.setProperty(ServerConstants.CARBON_HOME, carbonHome);
            }
        } catch (Exception e) {
            handleException("Fail to start carbon server ", e);
        }
    }

    public void onExecutionFinish() {
        try {
            if (executionEnvironment.equalsIgnoreCase(ExecutionEnvironment.STANDALONE.name())) {
                serverManager.stopServer();
            }
        } catch (Exception e) {
            handleException("Fail to stop carbon server ", e);
        }
    }

    private static void handleException(String msg, Exception e) {
        log.error(msg, e);
        throw new RuntimeException(msg, e);
    }
}
