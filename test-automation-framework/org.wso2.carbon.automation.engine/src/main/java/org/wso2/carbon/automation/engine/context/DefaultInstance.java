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
package org.wso2.carbon.automation.engine.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.automation.engine.configurations.AutomationConfiguration;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class DefaultInstance extends AutomationConfiguration {
    private static final Log log = LogFactory.getLog(DefaultInstance.class);

    public String getTenantDomain(boolean isTenantAdmin, boolean isClustered) {
        String tenantDomain = null;

        try {
            if (isTenantAdmin) {
                tenantDomain = getConfigurationValue(ContextXpathConstants.USER_MANAGEMENT_SUPER_TENANT_DOMAIN);
            } else {
                tenantDomain = getConfigurationValue(ContextXpathConstants.TENANT_DOMAIN);
            }
        } catch (XPathExpressionException e) {
            log.error("Error while reading the super Tenant:" + e.getStackTrace());
        }
        return tenantDomain;
    }


    public String getUserKey(String tenantDomain, boolean isAdminUser) {
        String tenantKey = null;
        String adminUserReplacement = ContextXpathConstants.ADMIN;
        try {

            if (!isAdminUser) {
                adminUserReplacement = ContextXpathConstants.USERS;
            }
            if (tenantDomain.equals(getConfigurationValue(ContextXpathConstants.SUPER_TENANT_DOMAIN))) {

                tenantKey = getConfigurationValue(String.
                        format(ContextXpathConstants.USER_MANAGEMENT_SUPER_TENANT_USER_KEY, adminUserReplacement));
            } else {
                tenantKey = getConfigurationValue(String.format(ContextXpathConstants.USER_MANAGEMENT_TENANT_USER_KEY,
                        tenantDomain, adminUserReplacement));
            }
        } catch (XPathExpressionException e) {
            log.error("Error while reading the Tenant:" + e.getMessage());
        }
        return tenantKey;
    }

    public String getDefaultManager(String productGroup) {
        String managerNode = null;
        try {
            boolean isClustered = Boolean.parseBoolean(getConfigurationValue(
                    String.format(ContextXpathConstants.PRODUCT_GROUP_CLUSTERING_ENABLED, productGroup)));
            String xpathNodeType = ContextXpathConstants.PRODUCT_GROUP_INSTANCE_TYPE;
            NodeList lbWorkerManagerList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.lb_worker_manager));
            NodeList lbManagerNodeList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.lb_manager));
            NodeList managerNodeList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.manager));
            NodeList standAloneNodeList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.standalone));

            if (isClustered) {
                if (lbWorkerManagerList.getLength() >= 1) {
                    managerNode = lbWorkerManagerList.item(0).getAttributes().
                            getNamedItem(ContextXpathConstants.NAME).getTextContent();
                } else if (lbManagerNodeList.getLength() >= 1) {
                    managerNode = lbManagerNodeList.item(0).getAttributes().
                            getNamedItem(ContextXpathConstants.NAME).getTextContent();
                } else if (managerNodeList.getLength() >= 1) {
                    managerNode = managerNodeList.item(0).getAttributes().
                            getNamedItem(ContextXpathConstants.NAME).getTextContent();
                } else {
                    managerNode = standAloneNodeList.item(0).getAttributes().
                            getNamedItem(ContextXpathConstants.NAME).getTextContent();
                }
            } else {
                managerNode = standAloneNodeList.item(0).getAttributes().
                        getNamedItem(ContextXpathConstants.NAME).getTextContent();
            }

        } catch (XPathExpressionException e) {
            log.error("Error while reading the default Manager:" + e.getMessage());
        }
        return managerNode;
    }

    public String getDefaultWorker(String productGroup) {
        String workerNode = null;
        try {
            boolean isClustered = Boolean.parseBoolean(getConfigurationValue(
                    String.format(ContextXpathConstants.PRODUCT_GROUP_CLUSTERING_ENABLED, productGroup)));
            String xpathNodeType = ContextXpathConstants.PRODUCT_GROUP_INSTANCE_TYPE;
            NodeList lbWorkerManagerList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.lb_worker_manager));
            NodeList lbWorkerNodeList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.lb_worker));
            NodeList workerNodeList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.worker));
            NodeList standAloneNodeList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.standalone));

            if (isClustered) {
                if (lbWorkerManagerList.getLength() >= 1) {
                    workerNode = lbWorkerManagerList.item(0).getAttributes().
                            getNamedItem(ContextXpathConstants.NAME).getTextContent();
                } else if (lbWorkerNodeList.getLength() >= 1) {
                    workerNode = lbWorkerNodeList.item(0).getAttributes().
                            getNamedItem(ContextXpathConstants.NAME).getTextContent();
                } else if (workerNodeList.getLength() >= 1) {
                    workerNode = workerNodeList.item(0).getAttributes().
                            getNamedItem(ContextXpathConstants.NAME).getTextContent();
                } else {
                    workerNode = standAloneNodeList.item(0).getAttributes().
                            getNamedItem(ContextXpathConstants.NAME).getTextContent();
                }
            } else {
                workerNode = standAloneNodeList.item(0).getAttributes().
                        getNamedItem(ContextXpathConstants.NAME).getTextContent();
            }
        } catch (XPathExpressionException e) {
            log.error("Error while reading the default worker:" + e.getMessage());
        }
        return workerNode;
    }
}
