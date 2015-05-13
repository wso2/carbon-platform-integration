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

import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfiguration;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class JMSTopicMessagePublisher {
    private TopicConnection connection = null;
    private TopicSession session = null;
    private TopicPublisher publisher = null;
    private TopicConnectionFactory connectionFactory = null;

    public JMSTopicMessagePublisher(JMSBrokerConfiguration brokerConfiguration)
            throws NamingException {
        // Create a ConnectionFactory
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                brokerConfiguration.getInitialNamingFactory());
        if (brokerConfiguration.getProviderURL().startsWith("amqp://")) {
            //setting property for Qpid running on WSO2 MB
            props.put("connectionfactory.TopicConnectionFactory",
                    brokerConfiguration.getProviderURL());
        } else {
            //setting property for ActiveMQ
            props.setProperty(Context.PROVIDER_URL, brokerConfiguration.getProviderURL());
        }
        Context ctx = new InitialContext(props);
        connectionFactory = (TopicConnectionFactory) ctx.lookup("TopicConnectionFactory");
    }

    /**
     * This will establish  the connection with the given Topic and message are not persisted.
     * This must be called before calling publish()
     *
     * @param topicName name of the topic
     * @throws JMSException
     * @throws javax.naming.NamingException
     */
    public void connect(String topicName) throws JMSException, NamingException {
        // Create a Connection
        connection = connectionFactory.createTopicConnection();
        connection.start();
        // Create a Session
        session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        // Create a MessageConsumer from the Session to the Queue
        Topic topic = session.createTopic(topicName);
        // Create a MessageProducer from the Session to the Topic or Queue
        publisher = session.createPublisher(topic);
        publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    /**
     * This will establish  the connection with the given Topic.
     * This must be called before calling publish()
     * @param persistMessage to persist message or not
     * @param topicName name of the topic
     * @throws JMSException when failed to connect to Topic
     */
    public void connect(String topicName, boolean persistMessage) throws JMSException {
        // Create a Connection
        connection = connectionFactory.createTopicConnection();
        connection.start();
        // Create a Session
        session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        // Create a MessageConsumer from the Session to the Queue
        Topic topic = session.createTopic(topicName);
        // Create a MessageProducer from the Session to the Topic or Queue
        publisher = session.createPublisher(topic);
        if(persistMessage) {
            publisher.setDeliveryMode(DeliveryMode.PERSISTENT);
        } else {
            publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        }
    }

    /**
     * This will disconnect  the connection with the given Topic.
     * This must be called after publishing the messages to release
     * the connection
     */
    public void disconnect() {
        if (publisher != null) {
            try {
                publisher.close();
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
     * this will publish the message to given topic
     *
     * @param messageContent
     * @throws AutomationFrameworkException
     */
    public void publish(String messageContent) throws AutomationFrameworkException {
        if (publisher == null) {
            throw new AutomationFrameworkException("No Connection with topic. Please connect");
        }
        // Create a messages;
        TextMessage message = null;
        try {
            message = session.createTextMessage(messageContent);
            publisher.publish(message);
        } catch (JMSException e) {
            throw new AutomationFrameworkException("Message creation failed", e);
        }

    }

    /*
    * This Will send byte stream to the destination
    * @param byte content
    * @throws AutomationFrameworkException
     */

    public void sendBytesMessage(byte[] payload) throws AutomationFrameworkException {
        BytesMessage bm = null;
        try {
            bm = session.createBytesMessage();
            bm.writeBytes(payload);
            publisher.publish(bm);
        } catch (JMSException e) {
            throw new AutomationFrameworkException("Byte Message creation failed", e);
        }

    }
}
