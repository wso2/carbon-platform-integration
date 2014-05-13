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

public class JMSBrokerConfigurationProvider {
    private static JMSBrokerConfiguration configuration = null;
    private static JMSBrokerConfigurationProvider instance = new JMSBrokerConfigurationProvider();


    private JMSBrokerConfigurationProvider() {
        configuration = getJMSBrokerConfiguration();
    }

    public static JMSBrokerConfigurationProvider getInstance() {
        return instance;
    }


    public JMSBrokerConfiguration getBrokerConfiguration() {
        return configuration;
    }


    private JMSBrokerConfiguration getJMSBrokerConfiguration() {
        JMSBrokerConfiguration jmsBrokerConfiguration = new JMSBrokerConfiguration();
//        if (new EnvironmentBuilder().getFrameworkSettings().getEnvironmentSettings().is_builderEnabled()) {
//            //setting activemq configuration
        jmsBrokerConfiguration.
                setInitialNamingFactory("org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        jmsBrokerConfiguration.setProviderURL("tcp://127.0.0.1:61616");

//        } else {
//            //setting wso2mb configuration
//            ProductVariables mbServer = FrameworkFactory.getFrameworkProperties(FrameworkConstants.MB_SERVER_NAME)
//                    .getProductVariables();
//          //  InitialContextPublisher.getContext().getUserManagerContext();
//            UserInfo userInfo = UserListCsvReader.getUserInfo(0);
//            String userName = userInfo.;
//            String password = userInfo.getPassword();
//            String qpidPort = mbServer.getQpidPort();
//            String hostaName = mbServer.getHostName();
//            jmsBrokerConfiguration.setInitialNamingFactory("org.wso2.andes.jndi.PropertiesFileInitialContextFactory");
//            jmsBrokerConfiguration.setProviderURL("amqp://" + userName + ":" + password + "@clientID/carbon?brokerlist='tcp://"
//                                                  + hostaName + ":" + qpidPort + "'");
//        }
//
        return jmsBrokerConfiguration;
    }
}
