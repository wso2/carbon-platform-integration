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

package org.wso2.carbon.automation.test.api.clients.identity.sso.saml;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.identity.sso.saml.stub.IdentitySAMLSSOConfigServiceIdentityException;
import org.wso2.carbon.identity.sso.saml.stub.IdentitySAMLSSOConfigServiceStub;

import java.rmi.RemoteException;

public class SAMLSSOConfigServiceClient {

    private static final Log log = LogFactory.getLog(SAMLSSOConfigServiceClient.class);

    private final String serviceName = "SAMLSSOConfigService";
    private IdentitySAMLSSOConfigServiceStub identitySAMLSSOConfigServiceStub;
    private String endPoint;

    public SAMLSSOConfigServiceClient(String backEndUrl, String sessionCookie)
            throws RemoteException {
        this.endPoint = backEndUrl + serviceName;
        try {
            identitySAMLSSOConfigServiceStub = new IdentitySAMLSSOConfigServiceStub(endPoint);
        } catch (AxisFault axisFault) {
            log.error("Error on initializing stub : " + axisFault.getMessage());
            throw new RemoteException("Error on initializing stub : ", axisFault);
        }
        AuthenticateStub.authenticateStub(sessionCookie, identitySAMLSSOConfigServiceStub);
    }

    public SAMLSSOConfigServiceClient(String backEndUrl, String userName, String password)
            throws RemoteException {
        this.endPoint = backEndUrl + serviceName;
        try {
            identitySAMLSSOConfigServiceStub = new IdentitySAMLSSOConfigServiceStub(endPoint);
        } catch (AxisFault axisFault) {
            log.error("Error on initializing stub : " + axisFault.getMessage());
            throw new RemoteException("Error on initializing stub : ", axisFault);
        }
        AuthenticateStub.authenticateStub(userName, password, identitySAMLSSOConfigServiceStub);

    }

    public String[] getClaimURIs()
            throws RemoteException, IdentitySAMLSSOConfigServiceIdentityException {
        try {
            return identitySAMLSSOConfigServiceStub.getClaimURIs();
        } catch (RemoteException e) {
            throw new RemoteException("Error while getting claim URIs ", e);
        } catch (IdentitySAMLSSOConfigServiceIdentityException e) {
            throw new IdentitySAMLSSOConfigServiceIdentityException("Error while getting claim URIs ", e);
        }

    }

}
