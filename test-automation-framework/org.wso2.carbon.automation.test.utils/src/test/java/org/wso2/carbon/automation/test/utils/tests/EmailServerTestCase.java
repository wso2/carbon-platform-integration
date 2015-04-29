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

package org.wso2.carbon.automation.test.utils.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.test.utils.generic.email.EmailServerUtil;

import static org.testng.Assert.assertEquals;

/**
 * simple test case for greenmail server
 */
public class EmailServerTestCase {

    static final Log log = LogFactory.getLog(EmailServerTestCase.class);
    EmailServerUtil emailServerUtil;

    @BeforeClass(alwaysRun = true)
    public void init() {
        emailServerUtil = new EmailServerUtil();
        emailServerUtil.startMailServer();
    }

    @Test(groups = "email.unit.test", description = "tests basic email server functionality")
    public void emailTransportTest() throws InterruptedException {
        Thread.sleep(1000); //wait 1sec before staring the server
        for (int i = 0; i < 5; i++) {
            emailServerUtil.sendTextEmail("to@localhost.com", "from@localhost.com", "subject", "body");
        }

        assertEquals(emailServerUtil.getReceivedMessages().length, 5, "email messages not received");
    }

    @AfterClass(alwaysRun = true)
    public void clean() {
        emailServerUtil.stopMailServer();
    }
}
