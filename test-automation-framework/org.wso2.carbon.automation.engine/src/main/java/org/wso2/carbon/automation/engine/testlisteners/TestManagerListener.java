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
import org.wso2.carbon.automation.engine.extentions.ExtentionConstants;
import org.wso2.carbon.automation.engine.extentions.TestNGExtensionExecutor;

import java.lang.reflect.InvocationTargetException;

public class TestManagerListener implements ITestListener {
    private static final Log log = LogFactory.getLog(TestManagerListener.class);

    public void onTestStart(ITestResult iTestResult) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtentionConstants.TEST_LISTNER,
                    ExtentionConstants.TEST_LISTNER_ON_TEST_START);
        } catch (IllegalAccessException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (NoSuchMethodException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (InvocationTargetException e) {
            handleException("Error when shutting down the test execution", e);
        }
    }

    public void onTestSuccess(ITestResult iTestResult) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtentionConstants.TEST_LISTNER,
                    ExtentionConstants.TEST_LISTNER_ON_SUCCESS);
        } catch (IllegalAccessException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (NoSuchMethodException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (InvocationTargetException e) {
            handleException("Error when shutting down the test execution", e);
        }
    }

    public void onTestFailure(ITestResult iTestResult) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtentionConstants.TEST_LISTNER,
                    ExtentionConstants.TEST_LISTNER_ON_FAILIURE);
        } catch (IllegalAccessException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (NoSuchMethodException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (InvocationTargetException e) {
            handleException("Error when shutting down the test execution", e);
        }
    }

    public void onTestSkipped(ITestResult iTestResult) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtentionConstants.TEST_LISTNER,
                    ExtentionConstants.TEST_LISTNER_ON_SKIPPED);
        } catch (IllegalAccessException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (NoSuchMethodException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (InvocationTargetException e) {
            handleException("Error when shutting down the test execution", e);
        }
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtentionConstants.TEST_LISTNER,
                    ExtentionConstants.TEST_LISTNER_ON_FAILED_BUT_PASSED);
        } catch (IllegalAccessException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (NoSuchMethodException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (InvocationTargetException e) {
            handleException("Error when shutting down the test execution", e);
        }
    }

    public void onStart(ITestContext iTestContext) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtentionConstants.TEST_LISTNER,
                    ExtentionConstants.TEST_LISTNER_ON_START);
        } catch (IllegalAccessException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (NoSuchMethodException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (InvocationTargetException e) {
            handleException("Error when shutting down the test execution", e);
        }
    }

    public void onFinish(ITestContext iTestContext) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtentionConstants.TEST_LISTNER,
                    ExtentionConstants.TEST_LISTNER_ON_FINISH);
        } catch (IllegalAccessException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (NoSuchMethodException e) {
            handleException("Error when shutting down the test execution", e);
        } catch (InvocationTargetException e) {
            handleException("Error when shutting down the test execution", e);
        }
    }

    private void handleException(String msg, Exception e) {
        throw new RuntimeException(msg, e);
    }
}
