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
import org.wso2.carbon.automation.engine.context.ContextXpathConstants;
import org.wso2.carbon.automation.engine.context.InstanceType;
import org.wso2.carbon.automation.engine.context.beans.Instance;
import org.wso2.carbon.automation.engine.context.beans.Tenant;

import javax.xml.xpath.XPathExpressionException;

/**
 * This class generates the URL s according to the logic compiles with the assigned environment
 */
public class UrlGenerationUtil {

    /**
     * give the backend URL for the provided instance
     *
     * @param instance
     * @return
     */
    public static String getBackendURL(Instance instance) {

        String backendUrl;
        boolean webContextEnabled = instance.getProperties().containsKey(ContextXpathConstants.PRODUCT_GROUP_WEBCONTEXT);
        boolean portDisabled = instance.getPorts().isEmpty();

        String hostName = getManagerHost(instance);

        if (!portDisabled) {
            String webContextRoot = instance.getProperty(ContextXpathConstants.PRODUCT_GROUP_WEBCONTEXT);
            String httpsPort = instance.getPorts().get(ContextXpathConstants.PRODUCT_GROUP_PORT_HTTPS);
            if (httpsPort == null) {
                httpsPort = instance.getPorts().get(ContextXpathConstants.PRODUCT_GROUP_PORT_NHTTPS);
            }
            if (webContextRoot != null && httpsPort != null) {
                backendUrl = "https://" + hostName + ":" + httpsPort + "/" + webContextRoot + "/" + "services/";
            } else if (webContextRoot == null && httpsPort != null) {
                backendUrl = "https://" + hostName + ":" + httpsPort + "/" + "services/";
            } else if (webContextRoot == null) {
                backendUrl = "https://" + hostName + "/" + "services/";
            } else {
                backendUrl = "https://" + hostName + "/" + webContextRoot + "/" + "services/";
            }
        } else if (webContextEnabled) {
            String webContextRoot = instance.getProperty(ContextXpathConstants.PRODUCT_GROUP_WEBCONTEXT);
            backendUrl = "https://" + hostName + "/" + webContextRoot + "/" + "services/";
        } else {
            String httpsPort = instance.getPorts().get(ContextXpathConstants.PRODUCT_GROUP_PORT_HTTPS);
            backendUrl = "https://" + hostName + ":" + httpsPort + "/" + "services/";
        }
        return backendUrl;
    }

    /**
     * Returns the service URL
     *
     * @param tenant
     * @param instance
     * @param isSecured
     * @return
     */
    public static String getServiceURL(Tenant tenant,
                                       Instance instance, boolean isSecured) throws XPathExpressionException {
        String serviceURL;
        String port;
        String protocol;
        boolean webContextEnabled = instance.getProperties().containsKey(ContextXpathConstants.PRODUCT_GROUP_WEBCONTEXT);
        boolean portEnabled = !instance.getPorts().isEmpty();
        boolean isNonBlockingEnabled = instance.isNonBlockingTransportEnabled();
        String hostName = getWorkerHost(instance);
        if (isSecured) {
            protocol = ContextXpathConstants.PRODUCT_GROUP_PORT_HTTPS;
        } else {
            protocol = ContextXpathConstants.PRODUCT_GROUP_PORT_HTTP;
        }
        if (isSecured) {
            if (isNonBlockingEnabled) {
                port = instance.getPorts().get(ContextXpathConstants.PRODUCT_GROUP_PORT_NHTTPS);
            } else {
                port = instance.getPorts().get(ContextXpathConstants.PRODUCT_GROUP_PORT_HTTPS);
            }
        } else {
            if (isNonBlockingEnabled) {
                port = instance.getPorts().get(ContextXpathConstants.PRODUCT_GROUP_PORT_NHTTP);
            } else {
                port = instance.getPorts().get(ContextXpathConstants.PRODUCT_GROUP_PORT_HTTP);
            }
        }
        if (tenant.getDomain().equals(AutomationConfiguration.
                getConfigurationValue(ContextXpathConstants.SUPER_TENANT_DOMAIN))) {
            if (portEnabled && webContextEnabled) {
                String webContextRoot = instance.getProperty(ContextXpathConstants.PRODUCT_GROUP_WEBCONTEXT);
                if (webContextRoot != null && port != null) {
                    serviceURL = protocol + "://" + hostName + ":" + port + "/" + webContextRoot + "/" + "services";
                } else if (webContextRoot == null && port != null) {
                    serviceURL = protocol + "://" + hostName + ":" + port + "/" + "services";
                } else if (webContextRoot == null) {
                    serviceURL = protocol + "://" + hostName + "/" + "services";
                } else {
                    serviceURL = protocol + "://" + hostName + "/" + webContextRoot + "/" + "services";
                }
            } else if (!portEnabled && webContextEnabled) {
                String webContextRoot = instance.getProperty("webContext");
                serviceURL = protocol + "://" + hostName + "/" + webContextRoot + "/" + "services";
            } else if (portEnabled && !webContextEnabled) {
                serviceURL = protocol + "://" + hostName + ":" + port + "/" + "services";
            } else {
                serviceURL = protocol + "://" + hostName + "/" + "services";
            }
        } else {
            String tenantDomain = tenant.getDomain();
            if (portEnabled && webContextEnabled) {
                String webContextRoot = instance.getProperty(ContextXpathConstants.PRODUCT_GROUP_WEBCONTEXT);
                if (webContextRoot != null && port != null) {
                    serviceURL = protocol + "://" + hostName + ":" + port + "/" + webContextRoot + "/" + "services/t/" +
                            tenantDomain;
                } else if (webContextRoot == null && port != null) {
                    serviceURL = protocol + "://" + hostName + ":" + port + "/" + "services/t/" + tenantDomain;
                } else if (webContextRoot == null) {
                    serviceURL = protocol + "://" + hostName + "/" + "services/t/" + tenantDomain;
                } else {
                    serviceURL = protocol + "://" + hostName + "/" + webContextRoot + "/" + "services/t/" + tenantDomain;
                }
            } else if (!portEnabled && webContextEnabled) {
                String webContextRoot = instance.getProperty(ContextXpathConstants.PRODUCT_GROUP_WEBCONTEXT);
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
     * Returns the WebAppURL
     *
     * @param tenant
     * @param instance
     * @return
     */
    public static String getWebAppURL(Tenant tenant, Instance instance) throws XPathExpressionException {
        String webAppURL;
        String httpPort = instance.getPorts().get(ContextXpathConstants.PRODUCT_GROUP_PORT_HTTP);
        String tenantDomain = tenant.getDomain();
        String hostName = getWorkerHost(instance);
        if (!tenant.getDomain().equals(AutomationConfiguration.
                getConfigurationValue(ContextXpathConstants.SUPER_TENANT_DOMAIN))) {
            if (httpPort != null) {
                webAppURL = "http://" + hostName + ":" + httpPort + "/t/" + tenantDomain + "/webapps";
            } else {
                webAppURL = "http://" + hostName + "/t/" + tenantDomain + "/webapps";
            }
        } else {
            if (httpPort != null) {
                webAppURL = "http://" + hostName + ":" + httpPort;
            } else {
                webAppURL = "http://" + hostName;
            }
        }
        return webAppURL;
    }

    /**
     * This method gives the manager host of the given productGroup instance
     *
     * @param instance
     * @return
     */
    public static String getManagerHost(Instance instance) {
        String managerHost = "";
        String instanceType = instance.getType();
        if (instanceType.equals(InstanceType.standalone.name())) {
            managerHost = instance.getHosts().get(ContextXpathConstants.DEFAULT);
        } else if (instance.getType().equals(InstanceType.lb_worker_manager.name())) {
            managerHost = instance.getHosts().get(ContextXpathConstants.MANAGER);
        }
        return managerHost;
    }

    /**
     * This method gives the worker host of the given productGroup instance
     *
     * @param instance
     * @return
     */
    public static String getWorkerHost(Instance instance) {
        String workerHost = "";
        String instanceType = instance.getType();
        if (instanceType.equals(InstanceType.standalone.name())) {
            workerHost = instance.getHosts().get(ContextXpathConstants.DEFAULT);
        } else if (instance.getType().equals(InstanceType.lb_worker_manager.name())) {
            workerHost = instance.getHosts().get(ContextXpathConstants.WORKER);
        }
        return workerHost;
    }

}
