package org.wso2.carbon.automation.test.api.clients.cep;


import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.event.formatter.stub.EventFormatterAdminServiceStub;
import org.wso2.carbon.event.formatter.stub.types.*;

import java.rmi.RemoteException;

public class EventFormatterAdminServiceClient {
    private static final Log log = LogFactory.getLog(EventFormatterAdminServiceClient.class);
    private final String serviceName = "EventFormatterAdminService";
    private EventFormatterAdminServiceStub eventFormatterAdminServiceStub;
    private String endPoint;

    public EventFormatterAdminServiceClient(String backEndUrl, String sessionCookie) throws
                                                                                     AxisFault {
        this.endPoint = backEndUrl + serviceName;
        eventFormatterAdminServiceStub = new EventFormatterAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, eventFormatterAdminServiceStub);

    }

    public EventFormatterAdminServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        eventFormatterAdminServiceStub = new EventFormatterAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, eventFormatterAdminServiceStub);
    }

    public ServiceClient _getServiceClient() {
        return eventFormatterAdminServiceStub._getServiceClient();
    }

    public int getActiveEventFormatterCount()
            throws RemoteException {
        try {
            EventFormatterConfigurationInfoDto[] configs = eventFormatterAdminServiceStub.getAllActiveEventFormatterConfiguration();
            if (configs == null) {
                return 0;
            } else {
                return configs.length;
            }
        } catch (RemoteException e) {
            throw new RemoteException("RemoteException", e);
        }
    }

    public int getEventFormatterCount()
            throws RemoteException {
        try {
            EventFormatterConfigurationFileDto[] configs = eventFormatterAdminServiceStub.getAllInactiveEventFormatterConfiguration();
            if (configs == null) {
                return getActiveEventFormatterCount();
            } else {
                return configs.length + getActiveEventFormatterCount();
            }
        } catch (RemoteException e) {
            throw new RemoteException("RemoteException", e);
        }
    }


    public void addWso2EventFormatterConfiguration(String eventFormatterName, String streamId, String transportAdaptorName, String transportAdaptorType,
                                                   EventOutputPropertyConfigurationDto[] metaData, EventOutputPropertyConfigurationDto[] correlationData,
                                                   EventOutputPropertyConfigurationDto[] payloadData, EventFormatterPropertyDto[] eventFormatterPropertyDtos, boolean mappingEnabled)
            throws RemoteException {
        try {
            eventFormatterAdminServiceStub.deployWSO2EventFormatterConfiguration(eventFormatterName, streamId, transportAdaptorName, transportAdaptorType, metaData, correlationData, payloadData, eventFormatterPropertyDtos, mappingEnabled);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void addXMLEventFormatterConfiguration(String eventFormatterName,
                                                  String streamNameWithVersion,
                                                  String transportAdaptorName,
                                                  String transportAdaptorType,
                                                  String textData,
                                                  EventFormatterPropertyDto[] outputPropertyConfiguration,
                                                  String dataFrom)
            throws RemoteException {
        try {
            eventFormatterAdminServiceStub.deployXmlEventFormatterConfiguration(eventFormatterName, streamNameWithVersion, transportAdaptorName, transportAdaptorType, textData, outputPropertyConfiguration,dataFrom);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeActiveEventFormatterConfiguration(String eventFormatterName)
            throws RemoteException {
        try {
            eventFormatterAdminServiceStub.undeployActiveEventFormatterConfiguration(eventFormatterName);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeInactiveEventFormatterConfiguration(String filePath)
            throws RemoteException {
        try {
            eventFormatterAdminServiceStub.undeployInactiveEventFormatterConfiguration(filePath);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }




    public EventFormatterConfigurationDto getEventFormatterConfiguration(String eventBuilderConfiguration)
            throws RemoteException {
        try {
            return eventFormatterAdminServiceStub.getActiveEventFormatterConfiguration(eventBuilderConfiguration);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }
}
