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
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;
import org.wso2.carbon.automation.engine.extensions.ExtensionConstants;
import org.wso2.carbon.automation.engine.extensions.TestNGExtensionExecutor;

import java.util.List;

public class TestReportListener implements IReporter {
    private static final Log log = LogFactory.getLog(TestReportListener.class);
    private TestNGExtensionExecutor testNGExtensionExecutor;

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> iSuites, String s) {
        try {
            TestNGExtensionExecutor.executeExtensible(ExtensionConstants.REPORT_LISTENER,
                                                      ExtensionConstants.REPORT_LISTENER_GENERATE_REPORT, false);
        } catch (Exception e) {
            handleException("Error when generating testNG custom reports..", e);
        }
    }

    private void handleException(String msg, Exception e) {
        log.error("Execution error occurred in TestReportListener:-" , e);
        throw new RuntimeException(msg, e);
    }
}
