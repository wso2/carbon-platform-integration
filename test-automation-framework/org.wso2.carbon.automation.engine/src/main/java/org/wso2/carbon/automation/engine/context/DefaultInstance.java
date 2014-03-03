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

public class DefaultInstance {
    private static final Log log = LogFactory.getLog(DefaultInstance.class);

    public String getTennantDomain(boolean isTenantAdmin, boolean isClustered) {
        String tenantDomain = null;

        try {
            if (isTenantAdmin && !isClustered) {
                tenantDomain = getConfigurationValue("//superTenant/tenant/@domain");
            } else {
                tenantDomain = getConfigurationValue("//tenants/tenant/@domain");
            }
        } catch (XPathExpressionException e) {
            log.error("Error while reading the super Tenant:" + e.getMessage());
        }
        return tenantDomain;
    }


    public String getTenant(String tenantDomain, boolean isSuperTenant) {
        String tenantKey = null;
        try {
            if (isSuperTenant) {
                tenantKey = getConfigurationValue("//superTenant/tenant/users/user/@key");
            } else {
                tenantKey = getConfigurationValue("//tenants/tenant[@domain='" + tenantDomain + "']/users/user/@key");
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
                    String.format("//productGroup[@name='%s']/@clusteringEnabled", productGroup)));
            String xpathNodeType = "//productGroup[@name='%s']/instance[@type='%s']";
            NodeList lbManagerNodeList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.lb_manager));
            NodeList managerNodeList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.manager));
            NodeList standAloneNodeList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.standalone));

            if (isClustered) {
                if (lbManagerNodeList.getLength() >= 1) {
                    managerNode = lbManagerNodeList.item(0).getAttributes().getNamedItem("name").getTextContent();
                } else if (managerNodeList.getLength() >= 1) {
                    managerNode = managerNodeList.item(0).getAttributes().getNamedItem("name").getTextContent();
                } else {
                    managerNode = standAloneNodeList.item(0).getAttributes().getNamedItem("name").getTextContent();
                }
            } else {
                managerNode = standAloneNodeList.item(0).getAttributes().getNamedItem("name").getTextContent();
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
                    String.format("//productGroup[@name='%s']/@clusteringEnabled", productGroup)));
            String xpathNodeType = "//productGroup[@name='%s']/instance[@type='%s']";

            NodeList lbWorkerNodeList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.lb_worker));
            NodeList workerNodeList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.worker));
            NodeList standAloneNodeList = getConfigurationNodeList(String.format(xpathNodeType,
                    productGroup, InstanceType.standalone));

            if (isClustered) {
                if (lbWorkerNodeList.getLength() >= 1) {
                    workerNode = lbWorkerNodeList.item(0).getAttributes().getNamedItem("name").getTextContent();
                } else if (workerNodeList.getLength() >= 1) {
                    workerNode = workerNodeList.item(0).getAttributes().getNamedItem("name").getTextContent();
                } else {
                    workerNode = standAloneNodeList.item(0).getAttributes().getNamedItem("name").getTextContent();
                }
            } else {
                workerNode = standAloneNodeList.item(0).getAttributes().getNamedItem("name").getTextContent();
            }
        } catch (XPathExpressionException e) {
            log.error("Error while reading the default worker:" + e.getMessage());
        }
        return workerNode;
    }


    private String getConfigurationValue(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();
        XPath xPath = XPathFactory.newInstance().newXPath();
        return xPath.compile(expression).evaluate(xmlDocument);
    }

    private Node getConfigurationNode(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();
        XPath xPath = XPathFactory.newInstance().newXPath();
        return (Node) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODE);
    }

    private NodeList getConfigurationNodeList(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();
        XPath xPath = XPathFactory.newInstance().newXPath();
        return (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
    }


}
