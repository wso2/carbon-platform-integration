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
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.wso2.carbon.automation.engine.extensions.ExtensionConstants;
import org.wso2.carbon.automation.engine.extensions.TestNGExtensionExecutor;

/**
 * Listener for Suite Listener.
 */
public class TestSuiteListener implements ISuiteListener {
    private static final Log log = LogFactory.getLog(TestSuiteListener.class);
    private TestNGExtensionExecutor testNGExtensionExecutor;

    public void onStart(ISuite iSuite) {
        log.info("Inside Test Suite Listener - On Start");
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.SUITE_LISTENER,
                    ExtensionConstants.SUITE_LISTENER_ON_START, false);
        } catch (Exception e) {
            handleException("Error when initializing test suite environment ", e);
        }
    }

    public void onFinish(ISuite iSuite) {
        log.info("Inside Test Suite Listener - On Finish");
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.SUITE_LISTENER,
                    ExtensionConstants.SUITE_LISTENER_ON_FINISH, true);
        } catch (Exception e) {
            handleException("Error when tear down test suite environment ", e);
        }
    }

    private void handleException(String msg, Exception e) {
        log.error("Execution error occurred in TestSuiteListener:-" , e);
        throw new RuntimeException(msg, e);
    }
}
