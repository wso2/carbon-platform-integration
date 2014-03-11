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
package org.wso2.carbon.automation.test.utils.dataservice;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.test.api.clients.dataservices.DataServiceFileUploaderClient;
import org.wso2.carbon.automation.test.api.clients.service.mgt.ServiceAdminClient;
import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.carbon.dataservices.ui.fileupload.stub.ExceptionException;

import javax.activation.DataHandler;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Calendar;

public class DSSTestCaseUtils {
    protected Log log = LogFactory.getLog(getClass());
    private static int SERVICE_DEPLOYMENT_DELAY = 30000;

    public DSSTestCaseUtils() {
        try {
            SERVICE_DEPLOYMENT_DELAY = Integer.parseInt(new AutomationContext().getConfigurationValue("//deploymentDelay"));
        } catch (XPathExpressionException e) {
            log.error("Error reading deploymentDelay from automation.xml", e);
            log.warn("Service deployment Delay configuration not found. Running with default value " + SERVICE_DEPLOYMENT_DELAY + " mils");
        }
    }

    /**
     * Loads the specified resource from the classpath and returns its content as an OMElement.
     *
     * @param path A relative path to the resource file
     * @return An OMElement containing the resource content
     */
    public OMElement loadClasspathResource(String path) throws FileNotFoundException,
            XMLStreamException {
        OMElement documentElement = null;
        FileInputStream inputStream = null;
        XMLStreamReader parser = null;
        StAXOMBuilder builder = null;
        File file = new File((getClass().getResource(path).getPath()));
        if (file.exists()) {
            try {
                inputStream = new FileInputStream((getClass().getResource(path).getPath()));
                parser = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
                //create the builder
                builder = new StAXOMBuilder(parser);
                //get the root element (in this case the envelope)
                documentElement = builder.getDocumentElement().cloneOMElement();
            } finally {
                if (builder != null) {
                    builder.close();
                }
                if (parser != null) {
                    try {
                        parser.close();
                    } catch (XMLStreamException e) {
                        //ignore
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        //ignore
                    }
                }
            }
        } else {
            throw new FileNotFoundException("File Not Exist at " + path);
        }
        return documentElement;
    }

    /**
     * @param classPath A relative path to the resource file
     * @return content of the file as String
     * @throws IOException
     */
    public String getContentFromClassPath(String classPath) throws IOException {
        return FileManager.readFile(new File((getClass().getResource(classPath).getPath())));
    }

    public boolean uploadArtifact(String serviceUrl, String sessionCookie, String fileName,
                                  DataHandler dh)
            throws ExceptionException, RemoteException {
        DataServiceFileUploaderClient adminServiceDataServiceFileUploader =
                new DataServiceFileUploaderClient(serviceUrl, sessionCookie);
        return adminServiceDataServiceFileUploader.uploadDataServiceFile(fileName, dh);
    }

    public boolean isServiceDeployed(String backEndUrl, String sessionCookie, String serviceName)
            throws RemoteException {
        log.info("waiting " + SERVICE_DEPLOYMENT_DELAY + " millis for service deployment");
        boolean isServiceDeployed = false;
        ServiceAdminClient adminServiceService = new ServiceAdminClient(backEndUrl, sessionCookie);
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < SERVICE_DEPLOYMENT_DELAY) {
            if (adminServiceService.isServiceExists(serviceName)) {
                isServiceDeployed = true;
                log.info(serviceName + " Service Deployed in " + time + " millis");
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
        }
        return isServiceDeployed;
    }

    public boolean isServiceFaulty(String backEndUrl, String sessionCookie, String serviceName)
            throws RemoteException {
        boolean isServiceDeployed = false;
        ServiceAdminClient adminServiceService = new ServiceAdminClient(backEndUrl, sessionCookie);
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < SERVICE_DEPLOYMENT_DELAY) {
            if (adminServiceService.isServiceFaulty(serviceName)) {
                isServiceDeployed = true;
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
        return isServiceDeployed;
    }

    public boolean isServiceExist(String backEndUrl, String sessionCookie, String serviceName)
            throws RemoteException {
        ServiceAdminClient adminServiceService = new ServiceAdminClient(backEndUrl, sessionCookie);
        return adminServiceService.isServiceExists(serviceName);
    }

    public boolean isFaultyService(String backEndUrl, String sessionCookie, String serviceName)
            throws RemoteException {
        ServiceAdminClient adminServiceService = new ServiceAdminClient(backEndUrl, sessionCookie);
        return adminServiceService.isServiceFaulty(serviceName);
    }

    public void deleteService(String backEndUrl, String sessionCookie, String serviceName)
            throws RemoteException {
        ServiceAdminClient adminServiceService = new ServiceAdminClient(backEndUrl, sessionCookie);
        if (isFaultyService(backEndUrl, sessionCookie, serviceName)) {
            adminServiceService.deleteFaultyServiceByServiceName(serviceName);
        } else if (isServiceExist(backEndUrl, sessionCookie, serviceName)) {
            adminServiceService.deleteService(new String[]{adminServiceService.getServiceGroup(serviceName)});
        }
    }

    public boolean isServiceDeleted(String backEndUrl, String sessionCookie, String serviceName)
            throws RemoteException {
        log.info("waiting " + SERVICE_DEPLOYMENT_DELAY + " millis for service undeployment");
        ServiceAdminClient adminServiceService = new ServiceAdminClient(backEndUrl, sessionCookie);
        boolean isServiceDeleted = false;
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < SERVICE_DEPLOYMENT_DELAY) {
            if (!adminServiceService.isServiceExists(serviceName)) {
                isServiceDeleted = true;
                log.info(serviceName + " Service undeployed in " + time + " millis");
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
        return isServiceDeleted;
    }
}
