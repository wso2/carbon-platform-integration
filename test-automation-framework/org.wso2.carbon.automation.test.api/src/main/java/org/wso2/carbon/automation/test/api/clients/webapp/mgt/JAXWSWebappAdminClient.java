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

package org.wso2.carbon.automation.test.api.clients.webapp.mgt;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.webapp.mgt.stub.WebappAdminStub;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.SessionsWrapper;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.WebappMetadata;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.WebappUploadData;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.WebappsWrapper;

import javax.activation.DataHandler;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

public class JAXWSWebappAdminClient {
    private final Log log = LogFactory.getLog(JAXWSWebappAdminClient.class);
    public static final int MILLISECONDS_PER_MINUTE = 60 * 1000;
    private WebappAdminStub webappAdminStub;
    private final String serviceName = "JaxwsWebappAdmin";

    public JAXWSWebappAdminClient(String backendServerURL, String sessionCookie) throws AxisFault {

        String serviceURL = backendServerURL + serviceName;
        webappAdminStub = new WebappAdminStub(serviceURL);

        AuthenticateStub.authenticateStub(sessionCookie, webappAdminStub);
    }

    public JAXWSWebappAdminClient(String backendServerURL, String userName, String password)
            throws AxisFault {

        String serviceURL = backendServerURL + serviceName;
        webappAdminStub = new WebappAdminStub(serviceURL);

        AuthenticateStub.authenticateStub(userName, password, webappAdminStub);
    }

    public WebappsWrapper getPagedWebappsSummary(String webappSearchString,
                                                 String webappState, String webappType,
                                                 int pageNumber) throws AxisFault {
        try {
            return webappAdminStub.getPagedWebappsSummary(webappSearchString, webappState, webappType, pageNumber);
        } catch (RemoteException e) {
            handleException("cannot.get.webapp.data", e);
        }
        return null;
    }

    public WebappMetadata getStartedWebapp(String webappFileName) throws AxisFault {
        try {
            return webappAdminStub.getStartedWebapp(webappFileName);
        } catch (RemoteException e) {
            handleException("cannot.get.started.webapp.data", e);
        }
        return null;
    }

    public WebappMetadata getStoppedWebapp(String webappFileName) throws AxisFault {
        try {
            return webappAdminStub.getStoppedWebapp(webappFileName);
        } catch (RemoteException e) {
            handleException("cannot.get.stopped.webapp.data", e);
        }
        return null;
    }

    public void deleteAllStartedWebapps() throws AxisFault {
        try {
            webappAdminStub.deleteAllStartedWebapps();
        } catch (RemoteException e) {
            handleException("cannot.delete.webapps", e);
        }
    }

    public void deleteAllStoppedWebapps() throws AxisFault {
        try {
            webappAdminStub.deleteAllStoppedWebapps();
        } catch (RemoteException e) {
            handleException("cannot.delete.webapps", e);
        }
    }

    public void deleteStartedWebapps(String[] webappFileNames) throws AxisFault {
        try {
            webappAdminStub.deleteStartedWebapps(webappFileNames);
        } catch (RemoteException e) {
            handleException("cannot.delete.webapps", e);
        }
    }

    public void deleteStoppedWebapps(String[] webappFileNames) throws AxisFault {
        try {
            webappAdminStub.deleteStoppedWebapps(webappFileNames);
        } catch (RemoteException e) {
            handleException("cannot.delete.webapps", e);
        }
    }

    public WebappsWrapper getPagedFaultyWebappsSummary(String webappSearchString,String webappType,
                                                       int pageNumber) throws AxisFault {
        try {
            return webappAdminStub.getPagedFaultyWebappsSummary(webappSearchString, webappType, pageNumber);
        } catch (RemoteException e) {
            handleException("cannot.get.webapp.data", e);
        }
        return null;
    }

    public void deleteFaultyWebapps(String[] webappFileNames) throws AxisFault {
        try {
            webappAdminStub.deleteFaultyWebapps(webappFileNames);
        } catch (RemoteException e) {
            handleException("cannot.delete.all.faulty.webapps", e);
        }
    }

    public void deleteAllFaultyWebapps() throws AxisFault {
        try {
            webappAdminStub.deleteAllFaultyWebapps();
        } catch (RemoteException e) {
            handleException("cannot.delete.all.faulty.webapps", e);
        }
    }

    public void reloadAllWebapps() throws AxisFault {
        try {
            webappAdminStub.reloadAllWebapps();
        } catch (RemoteException e) {
            handleException("cannot.reload.webapps", e);
        }
    }

    public void reloadWebapps(String[] webappFileNames) throws AxisFault {
        try {
            webappAdminStub.reloadWebapps(webappFileNames);
        } catch (RemoteException e) {
            handleException("cannot.reload.webapps", e);
        }
    }

    public void stopAllWebapps() throws AxisFault {
        try {
            webappAdminStub.stopAllWebapps();
        } catch (RemoteException e) {
            handleException("cannot.stop.webapps", e);
        }
    }

    public void stopWebapps(String[] webappFileNames) throws AxisFault {
        try {
            webappAdminStub.stopWebapps(webappFileNames);
        } catch (RemoteException e) {
            handleException("cannot.stop.webapps", e);
        }
    }

    public void startAllWebapps() throws AxisFault {
        try {
            webappAdminStub.startAllWebapps();
        } catch (RemoteException e) {
            handleException("cannot.start.webapps", e);
        }
    }

    public void startWebapps(String[] webappFileNames) throws AxisFault {
        try {
            webappAdminStub.startWebapps(webappFileNames);
        } catch (RemoteException e) {
            handleException("cannot.start.webapps", e);
        }
    }

    public SessionsWrapper getActiveSessionsInWebapp(String webappFileName,
                                                     int pageNumber) throws AxisFault {
        try {
            return webappAdminStub.getActiveSessions(webappFileName, pageNumber);
        } catch (RemoteException e) {
            handleException("cannot.get.active.sessions", e);
        }
        return null;
    }

    public void expireSessionsInWebapps(String[] webappFileNames) throws AxisFault {
        try {
            webappAdminStub.expireSessionsInWebapps(webappFileNames);
        } catch (RemoteException e) {
            handleException("cannot.expire.all.sessions.in.webapps", e);
        }
    }

    public void expireSessionsInWebapp(String webappFileName,
                                       float maxSessionLifetimeMinutes) throws AxisFault {
        try {
            // We have to send session life time in milliseconds to the BE
            long maxSessionLifetimeMillis = (long) (maxSessionLifetimeMinutes *
                                                    MILLISECONDS_PER_MINUTE);
            webappAdminStub.expireSessionsInWebapp(webappFileName, maxSessionLifetimeMillis);
        } catch (RemoteException e) {
            handleException("cannot.expire.all.sessions.in.webapps", e);
        }
    }

    public void expireSessionsInWebapp(String webappFileName,
                                       String[] sessionIDs) throws AxisFault {
        try {
            webappAdminStub.expireSessions(webappFileName, sessionIDs);
        } catch (RemoteException e) {
            handleException("cannot.expire.all.sessions.in.webapps", e);
        }
    }

    public void expireSessionsInAllWebapps() throws AxisFault {
        try {
            webappAdminStub.expireSessionsInAllWebapps();
        } catch (RemoteException e) {
            handleException("cannot.expire.all.sessions.in.webapps", e);
        }
    }

    public void expireAllSessionsInWebapp(String webappFileName) throws AxisFault {
        try {
            webappAdminStub.expireAllSessions(webappFileName);
        } catch (RemoteException e) {
            handleException("cannot.expire.all.sessions.in.webapps", e);
        }
    }

    public void uploadWebapp(String artifactLocation, String artifactName)
            throws AxisFault, MalformedURLException {
        URL url = new URL("file://" + artifactLocation);
        DataHandler dh = new DataHandler(url);
        WebappUploadData webApp;
        webApp = new WebappUploadData();
        webApp.setFileName(artifactName);
        webApp.setDataHandler(dh);
        try {
            webappAdminStub.uploadWebapp(new WebappUploadData[]{webApp});
        } catch (RemoteException e) {
            handleException("cannot.upload.webapps", e);
        }
    }

    public void downloadWarFileHandler(String fileName,String webappType, HttpServletResponse response)
            throws AxisFault {
        try {
            ServletOutputStream out = response.getOutputStream();
            DataHandler handler = webappAdminStub.downloadWarFileHandler(fileName, webappType);
            if (handler != null) {
                response.setHeader("Content-Disposition", "fileName=" + fileName);
                response.setContentType(handler.getContentType());
                InputStream in = handler.getDataSource().getInputStream();
                int nextChar;
                while ((nextChar = in.read()) != -1) {
                    out.write((char) nextChar);
                }
                out.flush();
                in.close();
            } else {
                out.write("The requested webapp was not found on the server".getBytes());
            }
        } catch (RemoteException e) {
            handleException("error.downloading.war", e);
        } catch (IOException e) {
            handleException("error.downloading.war", e);
        }
    }


    private void handleException(String msg, Exception e) throws AxisFault {
        log.error(msg, e);
        throw new AxisFault(msg, e);
    }
}
