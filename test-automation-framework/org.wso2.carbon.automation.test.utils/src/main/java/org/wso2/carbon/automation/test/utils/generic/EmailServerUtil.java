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


package org.wso2.carbon.automation.test.utils.generic;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import javax.mail.internet.MimeMessage;

public class EmailServerUtil {

    GreenMail greenMail = null;

    public void startMailServer() {
        greenMail = new GreenMail ( ServerSetupTest.SMTP );
        greenMail.start ();
    }

    public void sendTextEmail(String receiver, String sender, String subject, String content) {
        GreenMailUtil.sendTextEmailTest(receiver, sender, subject, content);
    }

    public String getMailBody(int index) {
        return GreenMailUtil.getBody(greenMail.getReceivedMessages()[index]);
    }

    public String getMailHeaders( int index) {
        return GreenMailUtil.getHeaders(greenMail.getReceivedMessages()[index]);
    }

    public MimeMessage[] getReceivedMessages() {
        return greenMail.getReceivedMessages();
    }

    public void stopMailServer () {
        greenMail.stop();
    }
}
