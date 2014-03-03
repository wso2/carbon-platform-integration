package org.wso2.carbon.automation.test.api.clients.cep;


import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.event.output.adaptor.manager.stub.OutputEventAdaptorManagerAdminServiceStub;
import org.wso2.carbon.event.output.adaptor.manager.stub.types.OutputEventAdaptorConfigurationInfoDto;
import org.wso2.carbon.event.output.adaptor.manager.stub.types.OutputEventAdaptorFileDto;
import org.wso2.carbon.event.output.adaptor.manager.stub.types.OutputEventAdaptorPropertiesDto;
import org.wso2.carbon.event.output.adaptor.manager.stub.types.OutputEventAdaptorPropertyDto;

import java.rmi.RemoteException;

public class OutputEventAdaptorManagerAdminServiceClient {
    private static final Log log = LogFactory.getLog(OutputEventAdaptorManagerAdminServiceClient.class);
    private final String serviceName = "OutputEventAdaptorManagerAdminService";
    private OutputEventAdaptorManagerAdminServiceStub outputEventAdaptorManagerAdminServiceStub;
    private String endPoint;

    public OutputEventAdaptorManagerAdminServiceClient(String backEndUrl, String sessionCookie)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        outputEventAdaptorManagerAdminServiceStub = new OutputEventAdaptorManagerAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, outputEventAdaptorManagerAdminServiceStub);

    }

    public OutputEventAdaptorManagerAdminServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        outputEventAdaptorManagerAdminServiceStub = new OutputEventAdaptorManagerAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, outputEventAdaptorManagerAdminServiceStub);

    }

    public ServiceClient _getServiceClient() {
        return outputEventAdaptorManagerAdminServiceStub._getServiceClient();
    }

    public String[] getAllOutputEventAdaptorNames() throws RemoteException {
        String[] inputTransportAdaptorNames = null;
        try {
            OutputEventAdaptorConfigurationInfoDto[] inputTransportAdaptorConfigurationInfoDtos = outputEventAdaptorManagerAdminServiceStub.getAllActiveOutputEventAdaptorConfiguration();
            inputTransportAdaptorNames = new String[inputTransportAdaptorConfigurationInfoDtos.length];
            for (int i = 0; i < inputTransportAdaptorConfigurationInfoDtos.length; i++) {
                inputTransportAdaptorNames[i] = inputTransportAdaptorConfigurationInfoDtos[i].getEventAdaptorName();
            }
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException("RemoteException", e);

        }
        return inputTransportAdaptorNames;
    }

    public OutputEventAdaptorPropertiesDto getOutputEventAdaptorProperties(String transportAdaptorName) throws RemoteException {
        try {
            return outputEventAdaptorManagerAdminServiceStub.getActiveOutputEventAdaptorConfiguration(transportAdaptorName);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public int getActiveOutputEventAdaptorConfigurationCount()
            throws RemoteException {
        try {
            OutputEventAdaptorConfigurationInfoDto[] configs = outputEventAdaptorManagerAdminServiceStub.getAllActiveOutputEventAdaptorConfiguration();
            if (configs == null) {
                return 0;
            } else {
                return configs.length;
            }
        } catch (RemoteException e) {
            throw new RemoteException("RemoteException", e);
        }
    }

    public int getOutputEventAdaptorConfigurationCount()
            throws RemoteException {
        try {
            OutputEventAdaptorFileDto[] configs = outputEventAdaptorManagerAdminServiceStub.getAllInactiveOutputEventAdaptorConfiguration();
            if (configs == null) {
                return getActiveOutputEventAdaptorConfigurationCount();
            } else {
                return configs.length + getActiveOutputEventAdaptorConfigurationCount();
            }
        } catch (RemoteException e) {
            throw new RemoteException("RemoteException", e);
        }
    }

    public OutputEventAdaptorConfigurationInfoDto[] getActiveOutputEventAdaptorConfigurations()
            throws RemoteException {
        try {
            return outputEventAdaptorManagerAdminServiceStub.getAllActiveOutputEventAdaptorConfiguration();
        } catch (RemoteException e) {
            throw new RemoteException("RemoteException", e);
        }
    }

    public void addOutputEventAdaptorConfiguration(String transportAdaptorName, String transportAdaptorType,
                                                   OutputEventAdaptorPropertyDto[] transportAdaptorProperty) throws RemoteException {
        try {
            outputEventAdaptorManagerAdminServiceStub.deployOutputEventAdaptorConfiguration(transportAdaptorName, transportAdaptorType, transportAdaptorProperty);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeActiveOutputEventAdaptorConfiguration(String transportAdaptorName) throws RemoteException {
        try {
            outputEventAdaptorManagerAdminServiceStub.undeployActiveOutputEventAdaptorConfiguration(transportAdaptorName);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }

    public void removeInactiveOutputEventAdaptorConfiguration(String filePath) throws RemoteException {
        try {
            outputEventAdaptorManagerAdminServiceStub.undeployInactiveOutputEventAdaptorConfiguration(filePath);
        } catch (RemoteException e) {
            log.error("RemoteException", e);
            throw new RemoteException();
        }
    }
}
