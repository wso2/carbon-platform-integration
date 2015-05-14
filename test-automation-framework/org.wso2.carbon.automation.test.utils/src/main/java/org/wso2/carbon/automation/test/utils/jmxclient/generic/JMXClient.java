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
package org.wso2.carbon.automation.test.utils.jmxclient.generic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;

import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Hashtable;

public class JMXClient {
    private MBeanServerConnection mbsc = null;
    private String userName;
    private String password;
    private ObjectName objectName;

    private JMXConnector jmxc;
    private static final Log log = LogFactory.getLog(JMXClient.class);

    /**
     * @param serviceConnectionName name of the service
     * @param userName              user name
     * @param password              password
     * @throws MalformedObjectNameException throws if the connection unsuccessful
     */
    public JMXClient(String serviceConnectionName, String userName, String password)
            throws MalformedObjectNameException {
        this.objectName = new ObjectName(serviceConnectionName);
        this.userName = userName;
        this.password = password;
    }

    /**
     * connect to org.wso2.carbon for JMX monitoring
     *
     * @return return MBeanServerConnection
     * @throws java.io.IOException                           error in making connection
     * @throws javax.management.MalformedObjectNameException error in making connection
     */
    public MBeanServerConnection connect(String jmxServerURL)
            throws IOException, MalformedObjectNameException {

        //need to read rmi ports from environment config
        JMXServiceURL url = new JMXServiceURL(jmxServerURL);

        Hashtable<String, String[]> hashT = new Hashtable<String, String[]>();
        String[] credentials = new String[]{userName, password};
        hashT.put("jmx.remote.credentials", credentials);

        jmxc = JMXConnectorFactory.connect(url, hashT);
        mbsc = jmxc.getMBeanServerConnection();

        if (mbsc != null) {
            return mbsc;
        }
        return null;
    }

    /**
     * Disconnect JMX connection
     */
    public void disconnect() {
        if (jmxc != null) {
            log.info("Closing jmx client connection...............");
            try {
                jmxc.close();
            } catch (IOException e) {
                log.warn("Unable to close the connection");
            }
        }
        if (mbsc != null) {
            mbsc = null;
        }
    }

    /**
     * Invoke operation of a service
     *
     * @param operationName operation name to be invoked
     * @param params        parameters for the operation
     * @return results of the operation invocation
     * @throws ReflectionException       throws if operation invocation fails
     * @throws MBeanException            throws if operation invocation fails
     * @throws InstanceNotFoundException throws if operation invocation fails
     * @throws IOException               throws if operation invocation fails
     */
    public Object invoke(String operationName, Object[] params)
            throws ReflectionException, MBeanException, InstanceNotFoundException, IOException {
        return mbsc.invoke(objectName, operationName, params, new String[]{String.class.getName()});
    }

    /**
     * Get values of attribute exposed for management
     *
     * @param attribute name of the attribute
     * @return attribute value
     * @throws AttributeNotFoundException throws if attribute invocation fails
     * @throws MBeanException             throws if attribute invocation fails
     * @throws ReflectionException        throws if attribute invocation fails
     * @throws InstanceNotFoundException  throws if attribute invocation fails
     * @throws IOException                throws if attribute invocation fails
     */
    public Object getAttribute(String attribute)
            throws AttributeNotFoundException, MBeanException, ReflectionException,
                   InstanceNotFoundException, IOException {
        return mbsc.getAttribute(objectName, attribute);
    }
}