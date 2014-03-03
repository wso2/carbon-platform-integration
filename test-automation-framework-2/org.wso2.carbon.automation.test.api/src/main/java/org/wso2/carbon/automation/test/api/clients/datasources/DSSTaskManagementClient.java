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
package org.wso2.carbon.automation.test.api.clients.datasources;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.dataservices.task.ui.stub.DSTaskAdminStub;
import org.wso2.carbon.dataservices.task.ui.stub.xsd.DSTaskInfo;

import java.rmi.RemoteException;

public class DSSTaskManagementClient {
    private static final Log log = LogFactory.getLog(DSSTaskManagementClient.class);

    private final String serviceName = "DSTaskAdmin";
    private DSTaskAdminStub taskManagementAdminServiceStub;

    public DSSTaskManagementClient(String backEndUrl, String sessionCookie) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        taskManagementAdminServiceStub = new DSTaskAdminStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, taskManagementAdminServiceStub);
    }
    
    public DSSTaskManagementClient(String backEndUrl, String userName, String password) throws AxisFault {
            String endPoint = backEndUrl + serviceName;
            taskManagementAdminServiceStub = new DSTaskAdminStub(endPoint);
            AuthenticateStub.authenticateStub(userName, password, taskManagementAdminServiceStub);
        }

    public void scheduleTask(DSTaskInfo dSTaskInfo) throws RemoteException {
        taskManagementAdminServiceStub.scheduleTask(dSTaskInfo);
        log.info("ScheduleTask added");
    }

    public void rescheduleTask(DSTaskInfo dSTaskInfo) throws RemoteException {
        taskManagementAdminServiceStub.rescheduleTask(dSTaskInfo);
        log.info("Task rescheduled");
    }

    public void deleteTask(String taskName) throws RemoteException {

        taskManagementAdminServiceStub.deleteTask(taskName);
        log.info("ScheduleTask deleted");
    }

    public boolean isTaskScheduled(String taskName) throws RemoteException {

        return taskManagementAdminServiceStub.isTaskScheduled(taskName);
    }

    public String[] getAllTaskNames() throws RemoteException {
        return taskManagementAdminServiceStub.getAllTaskNames();
    }
}
