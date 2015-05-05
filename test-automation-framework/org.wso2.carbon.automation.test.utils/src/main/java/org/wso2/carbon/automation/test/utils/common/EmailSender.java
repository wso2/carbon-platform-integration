/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.automation.test.utils.common;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class EmailSender {

    private Properties properties;
    private String recipientMail;
    private String senderId;
    private String username;
    private String password;
    private Session session;
    private String subject;
    private String body;
    private List<String> attachmentList = null;


    public EmailSender(Properties properties, String username, String password, String domainName,
                       String recipientMail) {

        this.properties = properties;
        this.senderId = username + "@" + domainName;
        this.password = password;
        this.username = username;
        this.recipientMail = recipientMail;
    }

    public boolean createSession() {

        Authenticator authenticator = new EmailPasswordAuthenticator(username, password);
        session = Session.getInstance(properties, authenticator);

        if (session == null) {
            return false;
        }

        return true;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setAttachmentList(List<String> attachmentList) {
        this.attachmentList = attachmentList;
    }


    public void sendEmail() throws MessagingException {

        Transport transport = session.getTransport("smtp");

        transport.connect();

        Message message = new MimeMessage(session);
        // Set from
        message.setFrom(new InternetAddress(senderId));
        // Set to
        InternetAddress[] address = {new InternetAddress(recipientMail)};
        message.setRecipients(Message.RecipientType.TO, address);
        // Set subject
        message.setSubject(subject);
        // Set time
        message.setSentDate(new Date());
        // Set content
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(body);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);

        if (attachmentList != null) {
            for (String attachment : attachmentList) {

                MimeBodyPart attachmentPart = new MimeBodyPart();

                // Put a file in the second part
                FileDataSource fds = new FileDataSource(attachment);
                attachmentPart.setDataHandler(new DataHandler(fds));
                attachmentPart.setFileName(fds.getName());
                multipart.addBodyPart(attachmentPart);
            }
        }

        message.setContent(multipart);
        // Set complete
        message.saveChanges();
        // Send the message
        transport.sendMessage(message, address);
        transport.close();

    }
}

class EmailPasswordAuthenticator extends Authenticator {

    protected PasswordAuthentication passwordAuthentication;

    public EmailPasswordAuthenticator(String user, String password) {
        this.passwordAuthentication = new PasswordAuthentication(user, password);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return passwordAuthentication;
    }
}
