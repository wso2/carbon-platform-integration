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
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.wso2.carbon.automation.engine.extensions.ExtensionConstants;
import org.wso2.carbon.automation.engine.extensions.TestNGExtensionExecutor;

public class TestManagerListener implements ITestListener {
    private static final Log log = LogFactory.getLog(TestManagerListener.class);

    public void onTestStart(ITestResult iTestResult) {
        log.info("Running the test method --- " + iTestResult.getTestClass().getName() + "."
                + iTestResult.getMethod().getMethodName() + " ----");
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                    ExtensionConstants.TEST_LISTENER_ON_TEST_START, false);
        } catch (Exception e) {
            handleException("Error while running tests", e);
        }
    }

    public void onTestSuccess(ITestResult iTestResult) {
        log.info("On test success...");
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                    ExtensionConstants.TEST_LISTENER_ON_SUCCESS, false);
        } catch (Exception e) {
            handleException("Error while running tests", e);
        }
    }

    public void onTestFailure(ITestResult iTestResult) {
        log.info("On test failure...");
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                    ExtensionConstants.TEST_LISTENER_ON_FAILURE, false);
        } catch (Exception e) {
            handleException("Error while running tests", e);
        }
    }

    public void onTestSkipped(ITestResult iTestResult) {
        log.info("On test skipped...");
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                    ExtensionConstants.TEST_LISTENER_ON_SKIPPED, false);
        } catch (Exception e) {
            handleException("Error while running tests", e);
        }
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        log.info("On test failed but within success percentage...");
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                    ExtensionConstants.TEST_LISTENER_ON_FAILED_BUT_PASSED, false);
        } catch (Exception e) {
            handleException("Error while running tests", e);
        }
    }

    public void onStart(ITestContext iTestContext) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                    ExtensionConstants.TEST_LISTENER_ON_START, false);
        } catch (Exception e) {
            handleException("Error while running tests", e);
        }
    }

    public void onFinish(ITestContext iTestContext) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                    ExtensionConstants.TEST_LISTENER_ON_FINISH, true);
        } catch (Exception e) {
            handleException("Error while running tests", e);
        }
    }

    private void handleException(String msg, Exception e) {
        throw new RuntimeException(msg, e);
    }
}
