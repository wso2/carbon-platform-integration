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

package org.wso2.carbon.automation.test.api.clients.claim.mgt;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.claim.mgt.stub.ClaimManagementServiceStub;
import org.wso2.carbon.claim.mgt.stub.dto.ClaimDialectDTO;
import org.wso2.carbon.claim.mgt.stub.dto.ClaimMappingDTO;

import java.rmi.RemoteException;

public class ClaimAdminClient {

    private static final Log log = LogFactory.getLog(ClaimAdminClient.class);
    private final String serviceName = "ClaimManagementService";
    private ClaimManagementServiceStub claimManagementServiceStub;

    private String endPoint;

    public ClaimAdminClient(String backEndUrl, String sessionCookie)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        claimManagementServiceStub = new ClaimManagementServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, claimManagementServiceStub);

    }

    public ClaimAdminClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        claimManagementServiceStub = new ClaimManagementServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, claimManagementServiceStub);
    }

    public void addNewClaimMapping(ClaimMappingDTO claimMappingDTO)
            throws RemoteException, Exception {
        try {
            claimManagementServiceStub.addNewClaimMapping(claimMappingDTO);
        } catch (RemoteException e) {
            throw new RemoteException("Unable to add new claim Mapping ", e);
        }
    }

    public void removeClaimMapping(String dialectURI, String claimURI)
            throws RemoteException, Exception {
        try {
            claimManagementServiceStub.removeClaimMapping(dialectURI, claimURI);
        } catch (RemoteException e) {
            throw new RemoteException("Unable to remove claim Mapping ", e);
        }
    }

    public ClaimDialectDTO[] getClaimMappings() throws RemoteException, Exception {
        try {
            return claimManagementServiceStub.getClaimMappings();
        } catch (RemoteException e) {
            throw new RemoteException("Error while getting claim mappings ", e);
        }
    }
}
