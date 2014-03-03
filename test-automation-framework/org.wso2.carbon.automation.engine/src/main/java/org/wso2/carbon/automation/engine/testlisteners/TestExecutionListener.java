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
package org.wso2.carbon.automation.engine.testlisteners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.IExecutionListener;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.extentions.ExtentionConstants;
import org.wso2.carbon.automation.engine.extentions.TestNGExtensionExecutor;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

import javax.xml.xpath.XPathExpressionException;
import java.lang.reflect.InvocationTargetException;

public class TestExecutionListener implements IExecutionListener {
    // ServerManager serverManager;
    private TestNGExtensionExecutor testNGExtensionExecutor;
    private static final Log log = LogFactory.getLog(TestExecutionListener.class);


    /**
     * class before all test suits execution
     */
    public void onExecutionStart() {
        //read and build the automation context
        try {
            AutomationContext context = new AutomationContext();
            this.setKeyStoreProperties(context);
            testNGExtensionExecutor = new TestNGExtensionExecutor();
            testNGExtensionExecutor.initiate();
            TestNGExtensionExecutor.executeExtensible(ExtentionConstants.EXECUTION_LISTNER,
                    ExtentionConstants.EXECUTION_LISTENER_ONSTAGE);
            //start the server
            log.info("Test Execution Listener");
            /*if (AutomationConfiguration.getExecutionEnvironment().equals(Platforms.product.name())) {
                serverManager = new ServerManager();
                serverManager.startServer();
            }*/
            // UserPopulateHandler.populateUsers();
        } catch (Exception e1) {
            handleException("Error on initializing system ", e1);
        }
    }

    /**
     * calls after all test suite execution
     */
    public void onExecutionFinish() {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtentionConstants.EXECUTION_LISTNER,
                    ExtentionConstants.EXECUTION_LISTENER_ONFINISH);
            log.info("Loaded the classList of the " + " listener");
            //delete populated users
            //       UserPopulateHandler.deleteUsers();
            Thread.sleep(4000);
            // serverManager.shutdown();
        } catch (IllegalAccessException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (NoSuchMethodException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (InvocationTargetException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (Exception e) {
            handleException("Error while deleting users", e);
        }
    }

    private void handleException(String msg, Exception e) {
        throw new RuntimeException(msg, e);
    }
    public static void setKeyStoreProperties(AutomationContext context) throws XPathExpressionException {
        ;
        System.setProperty("javax.net.ssl.trustStore", FrameworkPathUtil.getSystemResourceLocation()
                +context.getConfigurationValue("//keystore/fileName/text()"));
        System.setProperty("javax.net.ssl.trustStorePassword",
                context.getConfigurationValue("//keystore/keyPassword/text()"));
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");

        if (log.isDebugEnabled()) {
            log.debug("javax.net.ssl.trustStore :" + System.getProperty("javax.net.ssl.trustStore"));
            log.debug("javax.net.ssl.trustStorePassword :" + System.getProperty("javax.net.ssl.trustStorePassword"));
            log.debug("javax.net.ssl.trustStoreType :" + System.getProperty("javax.net.ssl.trustStoreType"));
        }
    }
}