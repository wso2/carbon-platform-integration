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

package org.wso2.carbon.automation.test.api.clients.soap.tracer;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.tracer.stub.TracerAdminStub;
import org.wso2.carbon.tracer.stub.types.carbon.MessagePayload;
import org.wso2.carbon.tracer.stub.types.carbon.TracerServiceInfo;

import java.rmi.RemoteException;

/*
Client class for TracerAdminStub, which implement public methods for tests.
 */
public class TracerAdminClient {

    private final Log log = LogFactory.getLog(TracerAdminClient.class);

    private TracerAdminStub tracerAdminStub;
    private final String serviceName = "TracerAdmin";

    public TracerAdminClient(String backendServerURL, String sessionCookie) throws AxisFault {

        String serviceURL = backendServerURL + serviceName;
        tracerAdminStub = new TracerAdminStub(serviceURL);
        AuthenticateStub.authenticateStub(sessionCookie, tracerAdminStub);

    }

    public TracerAdminClient(String backendServerURL, String userName, String password)
            throws AxisFault {

        String serviceURL = backendServerURL + serviceName;
        tracerAdminStub = new TracerAdminStub(serviceURL);
        AuthenticateStub.authenticateStub(userName, password, tracerAdminStub);

    }

    public TracerServiceInfo getMessages(int numberOfMessages, String filter)
            throws RemoteException {

        TracerServiceInfo tracerServiceInfo = tracerAdminStub.getMessages(numberOfMessages, filter);
        MessagePayload message = tracerServiceInfo.getLastMessage();
        escapeHtml(message);
        return tracerServiceInfo;

    }

    public TracerServiceInfo setMonitoring(String flag) throws RemoteException {

        return tracerAdminStub.setMonitoring(flag);

    }

    public void clearAllSoapMessages() throws RemoteException {

        tracerAdminStub.clearAllSoapMessages();

    }

    public MessagePayload getMessage(String serviceName,
                                     String operationName,
                                     long messageSequence) throws RemoteException {
        MessagePayload message = tracerAdminStub.getMessage(serviceName, operationName, messageSequence);
        escapeHtml(message);
        return message;

    }

    private void escapeHtml(MessagePayload message) {
        if (message != null) {
            if (message.getRequest() != null) {
                String req = StringEscapeUtils.escapeHtml(removeXmlProlog(message.getRequest()));
                message.setRequest(req);
            }
            if (message.getResponse() != null) {
                String resp = StringEscapeUtils.escapeHtml(removeXmlProlog(message.getResponse()));
                message.setResponse(resp);
            }
        }
    }

    private String removeXmlProlog(String xml) {
        xml = xml.trim();
        if (xml.indexOf("<?xml") == 0) {
            xml = xml.substring(xml.indexOf(">") + 1);
        }
        return xml;
    }

}
