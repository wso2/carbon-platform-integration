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
package org.wso2.carbon.automation.engine.configurations;

import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.configurations.exceptions.NonExistenceException;
import org.wso2.carbon.automation.engine.context.mappertype.Instance;
import org.wso2.carbon.automation.engine.context.mappertype.Tenant;

/**
 * This class generates the URL s according to the logic compiles with the assigned environment
 */
public class UrlGenerationUtil {

    /**
     * @param instance
     * @return
     * @throws NonExistenceException
     */
    public static String getBackendURL(Instance instance) throws NonExistenceException {

        String backendUrl;
        boolean webContextEnabled = instance.getProperties().containsKey("webContext");
        boolean portDisabled = instance.getPorts().isEmpty();

        String hostName = instance.getHosts().get("default");

        if (!portDisabled) {
            String webContextRoot = instance.getProperty("webContext");
            String httpsPort = instance.getPorts().get("https");
            if (webContextRoot != null && httpsPort != null) {
                backendUrl = "https://" + hostName + ":" + httpsPort + "/" + webContextRoot + "/" + "services/";
            } else if (webContextRoot == null && httpsPort != null) {
                backendUrl = "https://" + hostName + ":" + httpsPort + "/" + "services/";
            } else if (webContextRoot == null) {
                backendUrl = "https://" + hostName + "/" + "services/";
            } else {
                backendUrl = "https://" + hostName + "/" + webContextRoot + "/" + "services/";
            }
        } else if (portDisabled && webContextEnabled) {
            String webContextRoot = instance.getProperty("webContext");
            backendUrl = "https://" + hostName + "/" + webContextRoot + "/" + "services/";
        } else if (!portDisabled && !webContextEnabled) {
            String httpsPort = instance.getPorts().get("https");
            backendUrl = "https://" + hostName + ":" + httpsPort + "/" + "services/";
        } else {
            backendUrl = "https://" + hostName + "/" + "services/";
        }
        return backendUrl;
    }

    /**
     *
     * @param tenant
     * @param instance
     * @param isSecured
     * @return
     */
    public static String getServiceUrl(Tenant tenant,
                                       Instance instance, boolean isSecured) {
        String serviceURL;
        String port;
        String protocol;
        boolean webContextEnabled = instance.getProperties().containsKey("webContext");
        boolean portEnabled = !instance.getPorts().isEmpty();
        String hostName = instance.getHosts().get("default");
        if (isSecured) {
            protocol = "https";
        } else {
            protocol = "http";
        }
        if (isSecured) {
            if (instance.getPorts().containsKey("nhttps")) {
                port = instance.getPorts().get("nhttps");
            } else {
                port = instance.getPorts().get("https");
            }
        } else {
            if (instance.getPorts().containsKey("nhttp")) {
                port = instance.getPorts().get("nhttp");
            } else {
                port = instance.getPorts().get("http");
            }
        }
        if (tenant.getDomain().equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
            if (portEnabled && webContextEnabled) {
                String webContextRoot = instance.getProperty("webContext");
                if (webContextRoot != null && port != null) {
                    serviceURL = protocol + "://" + hostName + ":" + port + "/" + webContextRoot + "/" + "services/";
                } else if (webContextRoot == null && port != null) {
                    serviceURL = protocol + "://" + hostName + ":" + port + "/" + "services";
                } else if (webContextRoot == null) {
                    serviceURL = protocol + "://" + hostName + "/" + "services/";
                } else {
                    serviceURL = protocol + "://" + hostName + "/" + webContextRoot + "/" + "services/";
                }
            } else if (!portEnabled && webContextEnabled) {
                String webContextRoot = instance.getProperty("webContext");
                serviceURL = protocol + "://" + hostName + "/" + webContextRoot + "/" + "services/";
            } else if (portEnabled && !webContextEnabled) {
                serviceURL = protocol + "://" + hostName + ":" + port + "/" + "services/";
            } else {
                serviceURL = protocol + "://" + hostName + "/" + "services/";
            }
        } else {
            String tenantDomain = tenant.getDomain();
            if (portEnabled && webContextEnabled) {
                String webContextRoot = instance.getProperty("webContext");
                if (webContextRoot != null && port != null) {
                    serviceURL = protocol + "://" + hostName + ":" + port + "/" + webContextRoot + "/" + "services/t/" + tenantDomain;
                } else if (webContextRoot == null && port != null) {
                    serviceURL = protocol + "://" + hostName + ":" + port + "/" + "services/t/" + tenantDomain;
                } else if (webContextRoot == null) {
                    serviceURL = protocol + "://" + hostName + "/" + "services/t/" + tenantDomain;
                } else {
                    serviceURL = protocol + "://" + hostName + "/" + webContextRoot + "/" + "services/t/" + tenantDomain;
                }
            } else if (!portEnabled && webContextEnabled) {
                String webContextRoot = instance.getProperty("webContext");
                serviceURL = protocol + "://" + hostName + "/" + webContextRoot + "/" + "services/t/" + tenantDomain;
            } else if (portEnabled && !webContextEnabled) {
                serviceURL = protocol + "://" + hostName + ":" + port + "/" + "services/t/" + tenantDomain;
            } else {
                serviceURL = protocol + "://" + hostName + "/" + "services/t/" + tenantDomain;
            }
        }
        return serviceURL;
    }

    /**
     *
     * @param tenant
     * @param instance
     * @return
     */
    public static String getWebAppURL(Tenant tenant, Instance instance) {
        String webAppURL;
        String httpPort = instance.getPorts().get("http");
        String tenantDomain = tenant.getDomain();
        String hostName = instance.getHosts().get("default");
        boolean portEnabled = instance.getPorts().isEmpty();

        if (!tenant.getDomain().equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
            if (portEnabled && httpPort != null) {
                webAppURL = "http://" + hostName + ":" + httpPort + "/t/" + tenantDomain + "/webapps";
            } else {
                webAppURL = "http://" + hostName + "/t/" + tenantDomain + "/webapps";
            }
        } else {
            if (portEnabled && httpPort != null) {
                webAppURL = "http://" + hostName + ":" + httpPort;
            } else {
                webAppURL = "http://" + hostName;
            }
        }
        return webAppURL;
    }
}
