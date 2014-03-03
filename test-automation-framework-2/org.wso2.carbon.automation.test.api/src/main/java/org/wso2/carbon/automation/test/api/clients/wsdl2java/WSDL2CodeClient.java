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

package org.wso2.carbon.automation.test.api.clients.wsdl2java;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.wsdl2code.stub.WSDL2CodeServiceStub;
import org.wso2.carbon.wsdl2code.stub.types.carbon.CodegenDownloadData;

import javax.activation.DataHandler;
import java.rmi.RemoteException;

public class WSDL2CodeClient {
    private static final Log log = LogFactory.getLog(WSDL2CodeClient.class);
    public WSDL2CodeServiceStub wsdl2CodeServiceStub;

    public WSDL2CodeClient(String backendServerURL,
                           String sessionCookie) throws AxisFault {

        String backendServiceURL = backendServerURL + "WSDL2CodeService";
        wsdl2CodeServiceStub = new WSDL2CodeServiceStub(backendServiceURL);
        AuthenticateStub.authenticateStub(sessionCookie, wsdl2CodeServiceStub);
    }

    public WSDL2CodeClient(String backendServerURL, String userName, String password)
            throws AxisFault {
        String backendServiceURL = backendServerURL + "WSDL2CodeService";
        wsdl2CodeServiceStub = new WSDL2CodeServiceStub(backendServiceURL);
        AuthenticateStub.authenticateStub(userName, password, wsdl2CodeServiceStub);
    }

    /**
     * invoke the back-end code generation methods and prompt to download the resulting zip file
     * containing generated code
     *
     * @param options - code generation options
     * @throws AxisFault in case of error
     */
    public DataHandler codeGen(String[] options) throws AxisFault {
        try {
            CodegenDownloadData downloadData = wsdl2CodeServiceStub.codegen(options);
            if (downloadData != null) return downloadData.getCodegenFileData();
        } catch (RemoteException e) {
            log.error(e.getMessage(), e);
            throw new AxisFault(e.getMessage(), e);

        }
        return null;
    }

    /**
     * invoke the back-end CXF code generation methods and prompt to download the resulting zip file
     * containing generated code
     *
     * @param options - code generation options
     * @throws AxisFault in case of error
     */
    public DataHandler codeGenForCXF(String[] options) throws AxisFault {
        try {
            CodegenDownloadData downloadData = wsdl2CodeServiceStub.codegenForCXF(options);
            if (downloadData != null) {
                return downloadData.getCodegenFileData();
            }
        } catch (RemoteException e) {
            log.error(e.getMessage(), e);
            throw new AxisFault(e.getMessage(), e);
        }
        return null;
    }
}
