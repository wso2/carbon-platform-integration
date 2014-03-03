/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.automation.test.api.clients.jaggeryservices;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaggeryjs.jaggery.app.mgt.stub.JaggeryAppAdminStub;
import org.jaggeryjs.jaggery.app.mgt.stub.types.carbon.WebappUploadData;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;

import javax.activation.DataHandler;
import java.net.MalformedURLException;
import java.net.URL;

/*
This class serves as a client to upload jaggery  web application
 */
public class JaggeryApplicationUploaderClient {

    private static final Log log = LogFactory.getLog(JaggeryApplicationUploaderClient.class);
    private JaggeryAppAdminStub jaggeryAppAdminStub;
    private final String serviceName = "JaggeryAppAdmin";

    public JaggeryApplicationUploaderClient(String backEndUrl, String sessionCookie) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        try {
            jaggeryAppAdminStub = new JaggeryAppAdminStub(endPoint);
            AuthenticateStub.authenticateStub(sessionCookie, jaggeryAppAdminStub);
        } catch (AxisFault axisFault) {
            log.error("JaggeryAppAdminStub Initialization fail " + axisFault.getMessage());
            throw new AxisFault("JaggeryAppAdminStub Initialization fail " + axisFault.getMessage());
        }
    }

    public void uploadJaggeryFile(String fileName, String filePath) throws Exception {
        WebappUploadData webappUploadData = new WebappUploadData();
        webappUploadData.setFileName(fileName);
        webappUploadData.setDataHandler(createDataHandler(filePath));
        jaggeryAppAdminStub.uploadWebapp(new WebappUploadData[]{webappUploadData});// uploads to server

    }

    private DataHandler createDataHandler(String filePath) throws MalformedURLException {
        URL url;
        try {
            url = new URL("file://" + filePath);
        } catch (MalformedURLException e) {
            log.error("File path URL is invalid" + e);
            throw new MalformedURLException("File path URL is invalid" + e);
        }
        DataHandler dh = new DataHandler(url);
        return dh;
    }
}
