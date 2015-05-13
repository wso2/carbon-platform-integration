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
package org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config;

import org.apache.activemq.broker.TransportConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class JMSBrokerConfigurationProvider {
    private static final Log log = LogFactory.getLog(JMSBrokerConfigurationProvider.class);
    private static JMSBrokerConfigurationProvider instance = new JMSBrokerConfigurationProvider();
    private List<TransportConnector> connectors = new ArrayList<TransportConnector>();
    private JMSBrokerConfiguration tcp = new JMSBrokerConfiguration();
    private JMSBrokerConfiguration mqtt = new JMSBrokerConfiguration();


    private JMSBrokerConfigurationProvider() {
        setTransportConnectors();
        setBrokerConfig();
    }

    public static JMSBrokerConfigurationProvider getInstance() {
        return instance;
    }


    public JMSBrokerConfiguration getBrokerConfiguration() {
        return getJMSBrokerConfiguration("tcp");
    }

    /**
     * Provide the transport configuration info
     * @param transport(tcp,mqtt)
     * @return JMSBrokerConfiguration
     */
    public JMSBrokerConfiguration getBrokerConfiguration(String transport) {
        return getJMSBrokerConfiguration(transport);
    }

    /**
     * provide the transport connectors available
     * @return List of TransportConnector
     */
    public List<TransportConnector> getTransportConnectors() {
        return connectors;
    }

    private TransportConnector getTCPConnector() throws URISyntaxException {
        //setting the tcp transport configurations
        TransportConnector tcp = new TransportConnector();
        tcp.setName("tcp");
        tcp.setUri(new URI("tcp://127.0.0.1:61616"));
        return tcp;
    }

    private TransportConnector getMQTTConnector() throws URISyntaxException{
        //setting the mqtt transport configurations
        TransportConnector mqtt = new TransportConnector();
        mqtt.setName("mqtt");
        mqtt.setUri(new URI("mqtt://localhost:1883"));
        return mqtt;
    }

    private void setTransportConnectors() {

        try {
            connectors.add(getTCPConnector());
            connectors.add(getMQTTConnector());
        } catch (URISyntaxException e) {
            log.error("Invalid URI", e);
        }


    }


    private JMSBrokerConfiguration getJMSBrokerConfiguration(String transportName) {
        if("tcp".equalsIgnoreCase(transportName)){
            return tcp;
        } else if("mqtt".equalsIgnoreCase(transportName)){
            return mqtt;
        } else {
            JMSBrokerConfiguration jmsBrokerConfiguration = new JMSBrokerConfiguration();
            jmsBrokerConfiguration.
                    setInitialNamingFactory("org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            for(TransportConnector con:getTransportConnectors()) {
                if(transportName.equalsIgnoreCase(con.getName()))
                    jmsBrokerConfiguration.setProviderURL(con.getUri().toString());
            }
            return jmsBrokerConfiguration;
        }
    }

    private void setBrokerConfig() {

        tcp.setInitialNamingFactory("org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        try {
            tcp.setProviderURL(getTCPConnector().getUri().toString());
        } catch (URISyntaxException e) {
            log.error("Invalid URI", e);
        }

        mqtt.setInitialNamingFactory("org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        try {
            mqtt.setProviderURL(getMQTTConnector().getUri().toString());
        } catch (URISyntaxException e) {
            log.error("Invalid URI", e);
        }

    }


}
