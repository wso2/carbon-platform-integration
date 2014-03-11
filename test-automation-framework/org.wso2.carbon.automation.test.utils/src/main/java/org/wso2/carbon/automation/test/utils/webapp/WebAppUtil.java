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
package org.wso2.carbon.automation.test.utils.webapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebAppUtil {
    private static final Log log = LogFactory.getLog(WebAppUtil.class);

    protected static boolean webappTest(String url, String content) throws IOException {
        BufferedReader in;
        boolean webappStatus = false;
        URL u = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int code = connection.getResponseCode(); //get response code to check whether it is 200 or 404
        //check connection for success
        if (code == 200) {
            log.info("Connected to webapp successfully");
            in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains(content)) {
                    log.info("Webapp output text " + content + " found");
                    webappStatus = true;
                    break;
                }
            }
            in.close();
        } else {
            if (log.isDebugEnabled()) {
                log.debug("webapp connection returned HTTP " + code + " error");
            }
        }
        return webappStatus;
    }

    public static void waitForWebAppDeployment(String serviceUrl, String content) throws Exception {
        int serviceTimeOut = 0;
        try {
            while (!webappTest(serviceUrl, content)) {
                if (serviceTimeOut == 0) {
                } else if (serviceTimeOut > 100) { //Check for the service for 100 seconds
                    // if Service not available assertfalse;
                    throw new Exception(serviceUrl + " webapp is not found");
                }
                try {
                    Thread.sleep(500);
                    serviceTimeOut++;
                } catch (InterruptedException ignored) {
                }
            }
        } catch (IOException e) {
            log.error("Unable to wait for webapp deployment: IO Exception" + e);
            throw new IOException("Unable to wait for webapp deployment" + e);
        }
    }

    public static void waitForWebAppUnDeployment(String serviceUrl, String content)
            throws Exception {
        int serviceTimeOut = 0;
        try {
            while (webappTest(serviceUrl, content)) {
                if (serviceTimeOut == 0) {
                } else if (serviceTimeOut > 60) { //Check for the service for 100 seconds
                    throw new Exception("webapp undeployment failed");
                }
                try {
                    Thread.sleep(500);
                    serviceTimeOut++;
                } catch (InterruptedException ignored) {
                }
            }
        } catch (IOException e) {
            log.error("Unable to wait for webapp deployment" + e);
            throw new IOException("Unable to wait for webapp deployment" + e);
        }
    }
}
