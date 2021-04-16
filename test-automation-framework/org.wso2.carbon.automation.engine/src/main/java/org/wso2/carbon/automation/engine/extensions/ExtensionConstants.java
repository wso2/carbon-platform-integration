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
package org.wso2.carbon.automation.engine.extensions;

public class ExtensionConstants {
    public static final String EXECUTION_LISTENER = "platformExecutionManager";
    public static final String EXECUTION_LISTENER_ON_START = "onExecutionStart";
    public static final String EXECUTION_LISTENER_ON_FINISH = "onExecutionFinish";
    public static final String REPORT_LISTENER = "PlatformReportManager";
    public static final String REPORT_LISTENER_GENERATE_REPORT = "generateReport";
    public static final String SUITE_LISTENER = "PlatformSuiteManager";
    public static final String SUITE_LISTENER_ON_START = "onStart";
    public static final String SUITE_LISTENER_ON_FINISH = "onFinish";
    public static final String TEST_LISTENER = "PlatformTestManager";
    public static final String TEST_LISTENER_ON_TEST_START = "onTestStart";
    public static final String TEST_LISTENER_ON_SUCCESS = "onTestSuccess";
    public static final String TEST_LISTENER_ON_FAILURE = "onTestFailure";
    public static final String TEST_LISTENER_ON_SKIPPED = "onTestSkipped";
    public static final String TEST_LISTENER_ON_FAILED_BUT_PASSED = "onTestFailedButWithinSuccessPercentage";
    public static final String TEST_LISTENER_ON_START = "onStart";
    public static final String TEST_LISTENER_ON_FINISH = "onFinish";
    public static final String TRANSFORM_LISTENER = "PlatformAnnotationTransferManager";
    public static final String TRANSFORM_LISTENER_TRANSFORM = "transform";
    public static final String CLASS_NAME = "name";
    public static final String LISTENER_EXTENSION = "//listenerExtensions";
    public static final String SEVER_STARTUP_SCRIPT_NAME = "wso2server";
    public static final String STARTUP_SCRIPT = "startupScript";
}
