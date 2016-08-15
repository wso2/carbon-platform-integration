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
package org.wso2.carbon.automation.distributed.testlisteners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.wso2.carbon.automation.distributed.extentions.ExtensionConstants;
import org.wso2.carbon.automation.distributed.extentions.TestNGExtensionExecutor;

import java.lang.reflect.InvocationTargetException;

/**
 * Test manager listener for TestNg.
 */
public class TestManagerListener implements ITestListener {
    private static final Log log = LogFactory.getLog(TestManagerListener.class);

    public void onTestStart(ITestResult iTestResult) {
        log.info("=================== Running the test method " + iTestResult.getTestClass().getName() + "."
                 + iTestResult.getMethod().getMethodName() + " ===================");
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                                                      ExtensionConstants.TEST_LISTENER_ON_TEST_START, false);
        } catch (NoSuchMethodException e) {
            handleException("Error while running tests", e);
        } catch (IllegalAccessException e) {
            handleException("Error while running tests", e);
        } catch (InvocationTargetException e) {
            handleException("Error while running tests", e);
        }
    }

    public void onTestSuccess(ITestResult iTestResult) {

        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                                                      ExtensionConstants.TEST_LISTENER_ON_SUCCESS, false);
        } catch (NoSuchMethodException e) {
            handleException("Error while running tests", e);
        } catch (IllegalAccessException e) {
            handleException("Error while running tests", e);
        } catch (InvocationTargetException e) {
            handleException("Error while running tests", e);
        }
        log.info("=================== On test success " + iTestResult.getTestClass().getName() + "."
                 + iTestResult.getMethod().getMethodName() + " ===================");
    }

    public void onTestFailure(ITestResult iTestResult) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                                                      ExtensionConstants.TEST_LISTENER_ON_FAILURE, false);

            if (iTestResult.getThrowable() != null) {
                StackTraceElement[] stArr = iTestResult.getThrowable().getStackTrace();
                for (int x = 0; x < stArr.length; x++) {
                    log.error(stArr[x].toString());
                }
            }

        } catch (NoSuchMethodException e) {
            handleException("Error while running tests", e);
        } catch (IllegalAccessException e) {
            handleException("Error while running tests", e);
        } catch (InvocationTargetException e) {
            handleException("Error while running tests", e);
        }
        log.info("=================== On test failure " + iTestResult.getTestClass().getName() + "."
                 + iTestResult.getMethod().getMethodName() + " ===================");
    }

    public void onTestSkipped(ITestResult iTestResult) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                                                      ExtensionConstants.TEST_LISTENER_ON_SKIPPED, false);
        } catch (NoSuchMethodException e) {
            handleException("Error while running tests", e);
        } catch (IllegalAccessException e) {
            handleException("Error while running tests", e);
        } catch (InvocationTargetException e) {
            handleException("Error while running tests", e);
        }
        log.info("=================== On test skipped " + iTestResult.getTestClass().getName() + "."
                 + iTestResult.getMethod().getMethodName() + " ===================");
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        log.info("On test failed but within success percentage...");
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                                                      ExtensionConstants.TEST_LISTENER_ON_FAILED_BUT_PASSED, false);
        } catch (NoSuchMethodException e) {
            handleException("Error while running tests", e);
        } catch (IllegalAccessException e) {
            handleException("Error while running tests", e);
        } catch (InvocationTargetException e) {
            handleException("Error while running tests", e);
        }

    }

    public void onStart(ITestContext iTestContext) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                                                      ExtensionConstants.TEST_LISTENER_ON_START, false);
        } catch (NoSuchMethodException e) {
            handleException("Error while running tests", e);
        } catch (IllegalAccessException e) {
            handleException("Error while running tests", e);
        } catch (InvocationTargetException e) {
            handleException("Error while running tests", e);
        }
    }

    public void onFinish(ITestContext iTestContext) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TEST_LISTENER,
                                                      ExtensionConstants.TEST_LISTENER_ON_FINISH, true);
        } catch (NoSuchMethodException e) {
            handleException("Error while running tests", e);
        } catch (IllegalAccessException e) {
            handleException("Error while running tests", e);
        } catch (InvocationTargetException e) {
            handleException("Error while running tests", e);
        }
    }

    private void handleException(String msg, Exception e) {
        log.error("Execution error occurred in TestManagerListener:-", e);
        throw new RuntimeException(msg, e);
    }
}



