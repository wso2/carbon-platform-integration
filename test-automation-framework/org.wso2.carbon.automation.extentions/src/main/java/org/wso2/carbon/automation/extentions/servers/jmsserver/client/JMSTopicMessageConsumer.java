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
package org.wso2.carbon.automation.extentions.servers.jmsserver.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.extentions.servers.jmsserver.controller.config.JMSBrokerConfiguration;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JMSTopicMessageConsumer implements MessageListener {
    private static final Log log = LogFactory.getLog(JMSTopicMessageConsumer.class);
    private TopicConnection connection = null;
    private TopicSession session = null;
    private TopicConnectionFactory connectionFactory = null;
    private Topic topic = null;
    private TopicSubscriber consumer = null;
    private List<String> messageList = new ArrayList<String>();

    public JMSTopicMessageConsumer(JMSBrokerConfiguration brokerConfiguration)
            throws NamingException {
        // Create a ConnectionFactory
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, brokerConfiguration.
                getInitialNamingFactory());
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
     * This will subscribe with a given topic and started to listen messages from the topic
     *
     * @param topicName name of the topic
     * @throws JMSException
     * @throws javax.naming.NamingException
     */
    public void subscribe(String topicName) throws JMSException, NamingException {
        // Create a Connection
        connection = connectionFactory.createTopicConnection();
        // Create a Session
        session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        // Create a MessageConsumer from the Session to the Topic or Queue
        topic = session.createTopic(topicName);
        consumer = session.createSubscriber(topic);
        consumer.setMessageListener(this);
        connection.start();
    }

    /**
     * This will disconnect  the connection with the given Topic.
     * This must be called after consuming the messages to release
     * the connection
     */
    public void stopConsuming() {
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
     * this will returns all the message received  from the topic after the subscription
     *
     * @return
     * @throws Exception
     */
    public List<String> getMessages() throws Exception {
        return messageList;
    }

    public void onMessage(Message message) {
        if (message != null) {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    addMessage(textMessage.getText());
                } catch (JMSException e) {
                    log.error(e);
                }
            }
        }
    }

    private synchronized void addMessage(String message) {
        messageList.add(message);
    }
}
