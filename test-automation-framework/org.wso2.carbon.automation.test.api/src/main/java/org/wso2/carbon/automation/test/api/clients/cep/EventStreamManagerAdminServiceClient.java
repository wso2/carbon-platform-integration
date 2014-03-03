package org.wso2.carbon.automation.test.api.clients.cep;


import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.event.stream.manager.stub.EventStreamAdminServiceStub;
import org.wso2.carbon.event.stream.manager.stub.types.EventStreamAttributeDto;
import org.wso2.carbon.event.stream.manager.stub.types.EventStreamInfoDto;

import java.rmi.RemoteException;

public class EventStreamManagerAdminServiceClient {
    private static final Log log = LogFactory.getLog(EventStreamManagerAdminServiceClient.class);
    private final String serviceName = "EventStreamAdminService";
    private EventStreamAdminServiceStub eventStreamAdminServiceStub;
    private String endPoint;

    public EventStreamManagerAdminServiceClient(String backEndUrl, String sessionCookie) throws
                                                                                     AxisFault {
        this.endPoint = backEndUrl + serviceName;
        eventStreamAdminServiceStub = new EventStreamAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, eventStreamAdminServiceStub);

    }

    public EventStreamManagerAdminServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        eventStreamAdminServiceStub = new EventStreamAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, eventStreamAdminServiceStub);
    }

    public ServiceClient _getServiceClient() {
        return eventStreamAdminServiceStub._getServiceClient();
    }

    public int getEventStreamCount()
            throws RemoteException {
        try {
            EventStreamInfoDto[] streamInfoDtos = eventStreamAdminServiceStub.getAllEventStreamInfoDto();
            if (streamInfoDtos == null) {
                return 0;
            } else {
                return streamInfoDtos.length;
            }
        } catch (RemoteException e) {
            throw new RemoteException("RemoteException", e);
        }
    }


    public void addEventStream(String streamName, String streamVersion, EventStreamAttributeDto[] metaAttributes,
                               EventStreamAttributeDto[] correlationAttributes,EventStreamAttributeDto[] payloadAttributes, String description, String nickname)
            throws RemoteException {
        try {
            eventStreamAdminServiceStub.addEventStreamInfo(streamName, streamVersion, metaAttributes, correlationAttributes, payloadAttributes, description, nickname);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeEventStream(String streamName, String streamVersion)
            throws RemoteException {
        try {
            eventStreamAdminServiceStub.removeEventStreamInfo(streamName, streamVersion);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public String getStreamDefinitionAsString(String streamId)
            throws RemoteException {
        try {
            return eventStreamAdminServiceStub.getStreamDefinitionAsString(streamId);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public String[] getStreamNames()
            throws RemoteException {
        try {
            return eventStreamAdminServiceStub.getStreamNames();
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }
}
