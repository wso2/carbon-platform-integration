/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.automation.extensions.jmeter.test;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.jmeter.JMeterTest;
import org.wso2.carbon.automation.extensions.jmeter.JMeterTestManager;

import java.io.File;

public class JmeterTestRunnerTestCase {

    @Test(description = "JMeter Test Runner Test Execution Test")
    public void testJmeterScriptFailureCase()  throws Exception {

        File file = new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator + "jmeter"
                             + File.separator + "sample-jmeter-script.jmx");

        JMeterTest script = new JMeterTest(file);
        JMeterTestManager manager = new JMeterTestManager();
        try {
            manager.runTest(script);
        } catch (AutomationFrameworkException e) {
            //verifying the script failures
            Assert.assertTrue(e.getMessage().contains("Test Failed"), "Error message not containing Test Failed");
            Assert.assertTrue(e.getMessage().contains("Error"), "Error message not containing Error");

        }
    }

}
