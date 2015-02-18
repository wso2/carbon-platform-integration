/*
*Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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


package org.wso2.carbon.automation.test.utils.generic.email;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * simple mail server for test cases using greenMail , http://www.icegreen.com/greenmail/readme.html
 */
public class EmailServerUtil {

    GreenMail greenMail;

    /**
     * start mail Server
     */
    public void startMailServer() {
        // all protocols supported here
        greenMail = new GreenMail();
        greenMail.start ();
    }

    /**
     * send simple text mail
     * @param receiver - email address of receiver
     * @param sender - email address of sender
     * @param subject - email subject
     * @param content - email body
     */
    public void sendTextEmail(String receiver, String sender, String subject, String content) {
        GreenMailUtil.sendTextEmailTest(receiver, sender, subject, content);
    }

    /**
     * get mail message body based on index
     * @param index - mail index
     * @return
     */

    public String getMailBody(int index) {
        return GreenMailUtil.getBody(greenMail.getReceivedMessages()[index]);
    }

    /**
     * get mail message headers based on message index
     * @param index - mail index
     * @return
     */

    public String getMailHeaders( int index) {
        return GreenMailUtil.getHeaders(greenMail.getReceivedMessages()[index]);
    }

    /**
     * get all the received messages in the mail server, this is total messages in the server instance
     * @return
     */

    public MimeMessage[] getReceivedMessages() {
        return greenMail.getReceivedMessages();
    }

    /**
     * stop mail server
     */
    public void stopMailServer () {
        greenMail.stop();
    }

    /**
     * create user in green mail server memory
     * @param mailAddress
     * @param username
     * @param password
     * @return
     */

    public MailUser createUser(String mailAddress, String username, String password) {
        MailUser mailUser = new MailUser(greenMail);
        mailUser.registerUser(greenMail.setUser(mailAddress, username, password));
        return mailUser;
    }

    /**
     * create mime message with given content
     * @param mailString
     * @return
     * @throws MessagingException
     */

    public MimeMessage createMailMessage(String mailString ) throws MessagingException {
        return GreenMailUtil.newMimeMessage(mailString);
    }
}
