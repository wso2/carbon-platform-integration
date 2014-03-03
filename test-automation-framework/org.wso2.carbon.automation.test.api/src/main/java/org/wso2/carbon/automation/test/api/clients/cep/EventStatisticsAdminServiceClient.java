package org.wso2.carbon.automation.test.api.clients.cep;


import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.event.statistics.stub.client.EventStatisticsAdminServiceStub;

import java.rmi.RemoteException;

public class EventStatisticsAdminServiceClient {
    private static final Log log = LogFactory.getLog(EventStatisticsAdminServiceClient.class);
    private final String serviceName = "EventStatisticsAdminService";
    private EventStatisticsAdminServiceStub eventStatisticsAdminServiceStub;
    private String endPoint;

    public EventStatisticsAdminServiceClient(String backEndUrl, String sessionCookie) throws
            AxisFault {
        this.endPoint = backEndUrl + serviceName;
        eventStatisticsAdminServiceStub = new EventStatisticsAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, eventStatisticsAdminServiceStub);

    }

    public EventStatisticsAdminServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        eventStatisticsAdminServiceStub = new EventStatisticsAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, eventStatisticsAdminServiceStub);

    }

    public EventStatisticsAdminServiceStub.StatsDTO getGlobalCount() throws RemoteException {
        EventStatisticsAdminServiceStub.StatsDTO statsDTO = null;
        try {
            statsDTO = eventStatisticsAdminServiceStub.getGlobalCount();
        } catch (RemoteException e) {
            throw new RemoteException("RemoteException", e);
        }
        return statsDTO;
    }
}
