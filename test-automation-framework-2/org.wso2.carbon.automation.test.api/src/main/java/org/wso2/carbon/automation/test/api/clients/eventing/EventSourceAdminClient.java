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

package org.wso2.carbon.automation.test.api.clients.eventing;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.eventing.eventsource.stub.EventSourceAdminServiceStub;
import org.wso2.carbon.eventing.eventsource.stub.types.carbon.EventSourceDTO;

import java.rmi.RemoteException;

public class EventSourceAdminClient {
    String ServiceEndPoint = null;
    String SessionCookie = null;

    EventSourceAdminServiceStub eventSourceAdminServiceStub;

    private static final Log log = LogFactory.getLog(EventSourceAdminClient.class);

    public EventSourceAdminClient(String serviceEndPoint, String sessionCookie) {
        this.ServiceEndPoint = serviceEndPoint;
        this.SessionCookie = sessionCookie;
    }

    private EventSourceAdminServiceStub setPackageManagementStub() throws AxisFault {
        final String eventSourceServiceUrl = ServiceEndPoint + "EventSourceAdminService";
        EventSourceAdminServiceStub eventSourceAdminService = null;
        eventSourceAdminService = new EventSourceAdminServiceStub(eventSourceServiceUrl);
        AuthenticateStub.authenticateStub(SessionCookie, eventSourceAdminService);
        return eventSourceAdminService;
    }


    public void addEventSource() throws RemoteException {
        eventSourceAdminServiceStub = this.setPackageManagementStub();
        EventSourceDTO eventSourceDTO = new EventSourceDTO();
        eventSourceDTO.setClassName("org.apache.synapse.eventing.managers.EmbeddedRegistryBasedSubscriptionManager");
        eventSourceDTO.setName("SampleERES");
        eventSourceDTO.setTopicHeaderName("org.wso2.carbon");
        eventSourceDTO.setType("EmbeddedRegistry");
        eventSourceDTO.setTopicHeaderNS("http://ws.apache.org/ns/synapse");
        eventSourceAdminServiceStub.addEventSource(eventSourceDTO);
    }


}
