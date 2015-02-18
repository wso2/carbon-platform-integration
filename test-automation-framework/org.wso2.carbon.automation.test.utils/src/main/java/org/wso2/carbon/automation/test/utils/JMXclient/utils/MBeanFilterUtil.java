/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.automation.test.utils.JMXclient.utils;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.utils.JMXclient.JMXClientConstants;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * provide methods to access to a remote VM, and get the ObjectName or ObjectsNames which is registered in MBean server
 */
public class MBeanFilterUtil {

    private static final Log log = LogFactory.getLog(MBeanFilterUtil.class);

    /**
     * Creates connector address using java process Id. first tries to get connector address from
     * agent properties and if it is <code>null</code> crates the address manually and returns it.
     *
     * @param processId Java process Id.
     * @return Connector address.
     * @throws java.io.IOException                                       if attaching VM using <tt>pid</tt> or
     *                                                           failed get agent properties from remote VM or
     *                                                           failed to load agent results in an <tt>IOException</tt>.
     * @throws com.sun.tools.attach.AttachNotSupportedException  if attaching VM using <tt>pid</tt> failure results in an
     *                                                           <tt>AttachNotSupportedException</tt>.
     * @throws com.sun.tools.attach.AgentLoadException           if load agent results in an
     *                                                           <tt>AgentLoadException</tt>.
     * @throws com.sun.tools.attach.AgentInitializationException if load agent failure results in an
     *                                                           <tt>AgentInitializationException</tt>.
     */
    public String getConnectorAddress(int processId)
            throws IOException, AttachNotSupportedException, AgentLoadException,
                   AgentInitializationException {
        VirtualMachine remoteVirtualMachine = VirtualMachine.attach(Integer.toString(processId));
        String connectorAddress = remoteVirtualMachine.getAgentProperties().getProperty(JMXClientConstants.LOCAL_CONNECTOR_ADDRESS);

        if (connectorAddress == null) {
            String agent = remoteVirtualMachine.getSystemProperties().getProperty(JMXClientConstants.JAVA_HOME) + File.separator
                           + JMXClientConstants.LIBRARY + File.separator + JMXClientConstants.MANAGEMENT_AGENT_JAR;
            remoteVirtualMachine.loadAgent(agent);
            connectorAddress = remoteVirtualMachine.getAgentProperties().getProperty(JMXClientConstants.LOCAL_CONNECTOR_ADDRESS);
        }

        return connectorAddress;
    }

    /**
     * Connects to the MBean server using connector address and return the connection if
     * connected successfully. else it gives <tt>IOException</tt>.
     *
     * @param connectorAddress Address of remote VM
     * @return MBean server connection
     * @throws java.io.IOException if fails to connect to server
     *                     can be results in an <tt>IOException</tt>.
     */
    public MBeanServerConnection getMBeanServerConnection(String connectorAddress)
            throws IOException {
        JMXServiceURL serviceUrl = new JMXServiceURL(connectorAddress);
        JMXConnector connector = JMXConnectorFactory.connect(serviceUrl);
        return connector.getMBeanServerConnection();
    }

    /**
     * Get, Objectname object using name of the object
     *
     * @param mBeanServerConnection MBean server connection
     * @return Objectname object set which has only one value
     * @throws java.io.IOException                           if connection failure or query name request failure can be
     *                                                       results in an <tt>IOException</tt>.
     * @throws javax.management.MalformedObjectNameException if requested thread MXBean o
     *                                                       <p>"java.lang:type=Threading"</p>object malformed, can be
     *                                                       can be results in an <tt>MalformedObjectNameException</tt>.
     */

    public ObjectName getRegisteredMBeanByObjectName(MBeanServerConnection mBeanServerConnection,
                                                     String nameOfObject)
            throws MalformedObjectNameException, IOException, IllegalArgumentException {
        if (nameOfObject != null) {
            ObjectName objectName = new ObjectName(nameOfObject);
            Set<ObjectName> registeredObjectNames = mBeanServerConnection.queryNames(objectName, null);
            if (!registeredObjectNames.isEmpty()) {
                return registeredObjectNames.toArray(new ObjectName[registeredObjectNames.size()])[0];
            }else{
                log.info("Object name is not registered in the MBean server");
            }
        }
        throw new IllegalArgumentException("name Of Object is not valid");
    }

    /**
     * Get, Set of Objectname objects using name of the object and filtering query
     *
     * @param mBeanServerConnection MBean server connection
     * @return Objectname object set which has only one value
     * @throws java.io.IOException                           if connection failure or query name request failure can be
     *                                                       results in an <tt>IOException</tt>.
     * @throws javax.management.MalformedObjectNameException if requested thread MXBean o
     *                                                       <p>"java.lang:type=Threading"</p>object malformed, can be
     *                                                       can be results in an <tt>MalformedObjectNameException</tt>.
     */
    public Set<ObjectName> getRegisteredMBeanObjectNames(
            MBeanServerConnection mBeanServerConnection,
            String ObjectNamePattern, QueryExp query)
            throws MalformedObjectNameException, IOException {
        ObjectName objectName = new ObjectName(ObjectNamePattern);
        return mBeanServerConnection.queryNames(objectName, query);
    }

}
