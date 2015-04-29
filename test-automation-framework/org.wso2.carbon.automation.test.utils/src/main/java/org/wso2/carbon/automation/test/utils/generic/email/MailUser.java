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


import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.StoredMessage;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.user.UserException;
import com.icegreen.greenmail.util.GreenMail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.Flags;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * this is user created in the mail server, messages are stored in memory
 */

public class MailUser {

    static final Log log = LogFactory.getLog(MailUser.class);
    GreenMailUser greenMailUser;
    GreenMail greenMail;

    public MailUser (GreenMail greenMail) {
        this.greenMail = greenMail;
    }

    /**
     * create email user within the memory
     * @param greenMailUser
     */

    public void registerUser(GreenMailUser greenMailUser) {
        this.greenMailUser = greenMailUser;
    }

    /**
     * send message to users inbox
     * @param mimeMessage
     * @throws RuntimeException
     */

    public void sendMessage(MimeMessage mimeMessage) throws RuntimeException {
        try {
            greenMailUser.deliver(mimeMessage);
        } catch (UserException e) {
            log.error("Error occurred while delivering user mail via greenmail", e);
            throw new RuntimeException("Error occurred while delivering user mail via greenmail", e);
        }
    }

    /**
     * get all messages count
     * @return
     * @throws RuntimeException
     */

    public int getMessageCount() throws RuntimeException {

        int mailCount = 0;
        try {
            mailCount = greenMail.getManagers().getUserManager().getImapHostManager().getInbox(greenMailUser).getMessageCount();
        } catch (FolderException e) {
            log.error("Error occurred while retrieving user mail box via greenmail", e);
            throw new RuntimeException("Error occurred while retrieving user mail box via greenmail", e);
        }

        return mailCount;
    }

    /**
     * get unread message count
     * @return
     * @throws RuntimeException
     */

    public int getUnReadMessageCount() throws RuntimeException {

        int mailCount = 0;
        try {
            mailCount = greenMail.getManagers().getUserManager().getImapHostManager().getInbox(greenMailUser).getUnseenCount();
        } catch (FolderException e) {
            log.error("Error occurred while retrieving user mail box via greenmail", e);
            throw new RuntimeException("Error occurred while retrieving user mail box via greenmail", e);
        }

        return mailCount;
    }

    /**
     * get unread mail messages as MimeMessages
     * @return
     * @throws RuntimeException
     */

    public List<MimeMessage> getUnreadMails() throws RuntimeException {

        List<StoredMessage> storedMessageList;
        List<MimeMessage> mimeMessages = new ArrayList<MimeMessage>();
        try {
            storedMessageList = greenMail.getManagers().getUserManager().getImapHostManager().
                    getInbox(greenMailUser).getMessages();

            for (StoredMessage storedMessage : storedMessageList) {
                if (!storedMessage.getFlags().contains(Flags.Flag.SEEN)) {
                    mimeMessages.add(storedMessage.getMimeMessage());
                }
            }

        } catch (FolderException e) {
            log.error("Error occurred while retrieving user mail box messages greenmail", e);
            throw new RuntimeException("Error occurred while retrieving user mail box messages greenmail", e);
        }

        return mimeMessages;
    }

    /**
     * delete all messages in inbox
     * @throws RuntimeException
     */
    public void deleteAllMessages() throws RuntimeException{
        try {
            greenMail.getManagers().getUserManager().getImapHostManager().
                    getInbox(greenMailUser).deleteAllMessages();
        } catch (FolderException e) {
            log.error("Error occurred while retrieving user mail box messages greenmail", e);
            throw new RuntimeException("Error occurred while retrieving user mail box messages greenmail", e);
        }
    }

}
