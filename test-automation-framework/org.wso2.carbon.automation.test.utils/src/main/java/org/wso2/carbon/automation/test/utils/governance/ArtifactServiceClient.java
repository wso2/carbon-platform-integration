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
package org.wso2.carbon.automation.test.utils.governance;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.Stub;
import org.jaxen.JaxenException;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.test.api.clients.authenticators.AuthenticatorClient;

import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;

public class ArtifactServiceClient {
    public static OMElement sendAndReceive(String backEndURL, String artifactServiceEPR,
                                           String action, String payLoad, String userName,
                                           String password, String hostName) throws RemoteException,
            LoginAuthenticationExceptionException,
            XMLStreamException {
        OMElement omElement;
        AuthenticatorClient authenticatorClient = new AuthenticatorClient(backEndURL);
        Stub stub = authenticatorClient.getAuthenticationAdminStub();
        ServiceClient client = stub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        authenticatorClient.login(userName, password, hostName);
        options.setTo(new EndpointReference(artifactServiceEPR));
        options.setAction(action);
        options.setManageSession(true);
        omElement = client.sendReceive(AXIOMUtil.stringToOM(payLoad));
        return omElement;
    }

    public static String getArtifactId(OMElement omElement) throws JaxenException {
        String artifactId;
        AXIOMXPath expression = new AXIOMXPath("//ns:return");
        expression.addNamespace("ns", omElement.getNamespace().getNamespaceURI());
        artifactId = ((OMElement) expression.selectSingleNode(omElement)).getText();
        return artifactId;
    }
}
