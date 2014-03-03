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
package org.wso2.carbon.automation.extensions.servers.jmsserver.client;

import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class JMSQueueMessageConsumer {
    private Connection connection = null;
    private Session session = null;
    private QueueConnectionFactory connectionFactory = null;
    private Destination destination = null;
    private MessageConsumer consumer = null;

    public JMSQueueMessageConsumer(JMSBrokerConfiguration brokerConfiguration)
            throws NamingException {
        // Create a ConnectionFactory
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, brokerConfiguration.
                getInitialNamingFactory());
        if (brokerConfiguration.getProviderURL().startsWith("amqp://")) {
            //setting property for Qpid running on WSO2 MB
            props.put("connectionfactory.QueueConnectionFactory",
                    brokerConfiguration.getProviderURL());
        } else {
            //setting property for ActiveMQ
            props.setProperty(Context.PROVIDER_URL, brokerConfiguration.getProviderURL());
        }
        Context ctx = new InitialContext(props);
        connectionFactory = (QueueConnectionFactory) ctx.lookup("QueueConnectionFactory");
    }

    /**
     * This will establish  the connection with the given Queue. T
     * his must be called before calling popMessage() to get messages
     *
     * @param queueName Queue name trying to consume
     * @throws JMSException
     * @throws javax.naming.NamingException
     */
    public void connect(String queueName) throws JMSException, NamingException {
        // Create a Connection
        connection = connectionFactory.createConnection();
        connection.start();
        // Create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // Create a MessageConsumer from the Session to the Topic or Queue
        destination = session.createQueue(queueName);
        consumer = session.createConsumer(destination);
    }

    /**
     * This will disconnect  the connection with the given Queue.
     * This must be called after consuming the messages to release
     * the connection
     */
    public void disconnect() {
        if (consumer != null) {
            try {
                consumer.close();
            } catch (JMSException e) {
                //ignore
            }
        }
        if (session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                //ignore
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                //ignore
            }
        }
    }

    /**
     * this will get the first message from the Queue and dequeue the message from the queue
     *
     * @return the message text
     * @throws Exception
     */
    public String popMessage() throws Exception {
        if (consumer == null) {
            throw new Exception("No Consumer with Queue. Please connect");
        }
        Message message = consumer.receive(10000);
        if (message != null) {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                return textMessage.getText();
            } else {
                throw new Exception("Test Framework Exception. Message Type is not a TextMessage");
            }
        } else {
            return null;
        }
    }

    /**
     * @param clzz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T popMessage(Class<T> clzz) throws Exception {
        if (consumer == null) {
            throw new Exception("No Consumer with Queue. Please connect");
        }
        Message message = consumer.receive(10000);
        if (message != null) {
            if (clzz.isInstance(message)) {
                return clzz.cast(message);
            } else {
                throw new Exception("Test Framework Exception. Unexpected Message Type to cast");
            }
        } else {
            return null;
        }
    }

    /**
     * this will get the first message from the Queue and dequeue the message from the queue
     *
     * @return JMS Message object
     * @throws Exception
     */
    public Message popRawMessage() throws Exception {
        if (consumer == null) {
            throw new Exception("No Consumer with Queue. Please connect");
        }
        return consumer.receive(10000);
    }

    /**
     * Returns all the messages in the Queue and dequeue messages from the queue
     *
     * @return messages list
     * @throws Exception
     */
    public List<String> getMessages() throws Exception {
        if (session == null) {
            throw new Exception("No Connection with Queue. Please connect");
        }
        QueueBrowser browser;
        List<String> list = new ArrayList<String>();
        browser = session.createBrowser((Queue) destination);
        Enumeration enu = browser.getEnumeration();
        while (enu.hasMoreElements()) {
            TextMessage message = (TextMessage) enu.nextElement();
            list.add(message.getText());
        }
        browser.close();
        return list;
    }
}
