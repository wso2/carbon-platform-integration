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
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.ContextXpathConstants;
import org.wso2.carbon.automation.engine.extensions.ExtensionConstants;
import org.wso2.carbon.automation.engine.extensions.TestNGExtensionExecutor;
import org.wso2.carbon.automation.engine.frameworkutils.TestFrameworkUtils;

import java.lang.reflect.InvocationTargetException;
import javax.xml.xpath.XPathExpressionException;

/**
 * Litener for test execution.
 */
public class TestExecutionListener implements IExecutionListener {
    private static final Log log = LogFactory.getLog(TestExecutionListener.class);


    /**
     * class before all test suits execution.
     */
    public void onExecutionStart() {
        //read and build the automation context
        try {
            AutomationContext context = new AutomationContext();
            System.setProperty(FrameworkConstants.EXECUTION_MODE,
                               context.getConfigurationValue(ContextXpathConstants.EXECUTION_ENVIRONMENT));
            TestFrameworkUtils.setKeyStoreProperties(context);
            TestNGExtensionExecutor testNGExtensionExecutor = new TestNGExtensionExecutor();
            testNGExtensionExecutor.initiate();

            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.EXECUTION_LISTENER,
                                                      ExtensionConstants.EXECUTION_LISTENER_ON_START, false);

            //start the server
            log.info("Inside Test Execution Listener - On Execution");
        } catch (InvocationTargetException e) {
            handleException("Error while tear down the execution environment ", e);
        } catch (IllegalAccessException e) {
            handleException("Error while tear down the execution environment ", e);
        } catch (NoSuchMethodException e) {
            handleException("Error while tear down the execution environment ", e);
        } catch (InstantiationException e) {
            handleException("Error while tear down the execution environment ", e);
        } catch (XPathExpressionException e) {
            handleException("Error while tear down the execution environment ", e);
        } catch (ClassNotFoundException e) {
            handleException("Error while tear down the execution environment ", e);
        }

    }

    /**
     * calls after all test suite execution.
     */
    public void onExecutionFinish() {
        try {
            log.info("Inside Test Execution Listener - On Finish");
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.EXECUTION_LISTENER,
                                                      ExtensionConstants.EXECUTION_LISTENER_ON_FINISH, true);
        } catch (NoSuchMethodException e) {
            handleException("Error while tear down the execution environment ", e);
        } catch (IllegalAccessException e) {
            handleException("Error while tear down the execution environment ", e);
        } catch (InvocationTargetException e) {
            handleException("Error while tear down the execution environment ", e);
        }
    }

    private void handleException(String msg, Exception e) {
        log.error("Execution error occurred in TestExecutionListener:-", e);
        throw new RuntimeException(msg, e);
    }
}
