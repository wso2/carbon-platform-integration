package org.wso2.carbon.automation.test.api.clients.cep;


import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.event.builder.stub.EventBuilderAdminServiceStub;
import org.wso2.carbon.event.builder.stub.types.EventBuilderConfigurationDto;

import java.rmi.RemoteException;

public class EventBuilderAdminServiceClient {
    private static final Log log = LogFactory.getLog(EventBuilderAdminServiceClient.class);
    private final String serviceName = "EventBuilderAdminService";
    private EventBuilderAdminServiceStub eventBuilderAdminServiceStub;
    private String endPoint;

    public EventBuilderAdminServiceClient(String backEndUrl, String sessionCookie) throws
                                                                                   AxisFault {
        this.endPoint = backEndUrl + serviceName;
        eventBuilderAdminServiceStub = new EventBuilderAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, eventBuilderAdminServiceStub);

    }

    public EventBuilderAdminServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        eventBuilderAdminServiceStub = new EventBuilderAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, eventBuilderAdminServiceStub);
    }

    public ServiceClient _getServiceClient() {
        return eventBuilderAdminServiceStub._getServiceClient();
    }

    public int getActiveEventBuilderCount()
            throws RemoteException {
        try {
            EventBuilderConfigurationDto[] configs = eventBuilderAdminServiceStub.getAllActiveEventBuilderConfigurations();
            if (configs == null) {
                return 0;
            } else {
                return configs.length;
            }
        } catch (RemoteException e) {
            throw new RemoteException("RemoteException", e);
        }

    }

    public int getEventBuilderCount()
            throws RemoteException {
        try {
            String[] configs = eventBuilderAdminServiceStub.getAllInactiveEventBuilderConfigurations();
            if (configs == null) {
                return getActiveEventBuilderCount();
            } else {
                return configs.length + getActiveEventBuilderCount();
            }
        } catch (RemoteException e) {
            throw new RemoteException("RemoteException", e);
        }

    }

    public void addEventBuilderConfiguration(EventBuilderConfigurationDto eventBuilderConfigurationDto)
            throws RemoteException {
        try {
            eventBuilderAdminServiceStub.deployEventBuilderConfiguration(eventBuilderConfigurationDto);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeActiveEventBuilderConfiguration(String eventBuilderName)
            throws RemoteException {
        try {
            eventBuilderAdminServiceStub.undeployActiveConfiguration(eventBuilderName);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeInactiveEventBuilderConfiguration(String filePath)
            throws RemoteException {
        try {
            eventBuilderAdminServiceStub.undeployInactiveEventBuilderConfiguration(filePath);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public EventBuilderConfigurationDto getEventBuilderConfiguration(String eventBuilderConfiguration)
            throws RemoteException {
        try {
            return eventBuilderAdminServiceStub.getActiveEventBuilderConfiguration(eventBuilderConfiguration);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }
}
