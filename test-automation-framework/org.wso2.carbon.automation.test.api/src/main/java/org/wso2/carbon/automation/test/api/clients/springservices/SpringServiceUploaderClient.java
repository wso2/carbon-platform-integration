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
package org.wso2.carbon.automation.test.api.clients.springservices;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.springservices.stub.ExceptionException;
import org.wso2.carbon.springservices.stub.ServiceUploaderStub;
import org.wso2.carbon.springservices.stub.aarservices.xsd.AARServiceData;

import javax.activation.DataHandler;
import java.rmi.RemoteException;


public class SpringServiceUploaderClient {
    private static final Log log = LogFactory.getLog(SpringServiceUploaderClient.class);

    private ServiceUploaderStub serviceUploaderStub;
    private String serviceName = "ServiceUploader";

    public SpringServiceUploaderClient(String backEndUrl, String sessionCookie) throws AxisFault {

        String endPoint = backEndUrl + serviceName;
        serviceUploaderStub = new ServiceUploaderStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, serviceUploaderStub);
    }

    public SpringServiceUploaderClient(String backEndUrl, String userName, String password) throws AxisFault {

        String endPoint = backEndUrl + serviceName;
        serviceUploaderStub = new ServiceUploaderStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, serviceUploaderStub);
    }

    public void uploadSpringServiceFile(String fileName, DataHandler dh)
            throws ExceptionException, RemoteException {
        AARServiceData aarServiceData;

        aarServiceData = new AARServiceData();
        aarServiceData.setFileName(fileName);
        aarServiceData.setDataHandler(dh);
        aarServiceData.setServiceHierarchy("");

        serviceUploaderStub.uploadService(new AARServiceData[]{aarServiceData});
        log.info("Spring Artifact Uploaded");
    }
}
