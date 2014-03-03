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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.adminclients.AuthenticationAdminClient;
import org.wso2.carbon.automation.engine.configurations.AutomationConfiguration;
import org.wso2.carbon.automation.engine.configurations.UrlGenerationUtil;
import org.wso2.carbon.automation.engine.configurations.exceptions.NonExistenceException;
import org.wso2.carbon.automation.engine.context.mappertype.*;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This Class provides context providing interface for the test
 */
public class AutomationContext {
    private String productGroupName;
    private String workerInstanceName;
    private String managerInstanceName;
    private boolean isClustered;
    private boolean isTenantAdmin;
    private boolean isAdmin;
    private String tenantsDomain;
    private String userKey;

    /**
     * The context constructor for where to use with exact instance
     *
     * @param productGroupName
     * @param instanceName
     * @param tenantsDomain
     * @param tenantKey
     */
    public AutomationContext(String productGroupName, String instanceName, String tenantsDomain, String tenantKey)
            throws XPathExpressionException {
        this.productGroupName = productGroupName;
        this.workerInstanceName = instanceName;
        this.managerInstanceName = instanceName;
        this.isClustered = Boolean.parseBoolean(getConfigurationValue(
                String.format("//productGroup[@name='%s']/@clusteringEnabled", productGroupName)));
        this.tenantsDomain = tenantsDomain;
        this.userKey = tenantKey;
    }

    private boolean getIsClustered() throws XPathExpressionException {
        return Boolean.parseBoolean(getConfigurationValue(
                String.format("//productGroup[@name='%s']/@clusteringEnabled", productGroupName)));
    }

    /**
     * Constructor for default instance and tenant selection in between  default instance
     * selected from configuration
     *
     * @param productGroupName
     */
    public AutomationContext(String productGroupName, boolean isTenantAdmin, boolean isAdmin)
            throws XPathExpressionException {
        DefaultInstance defaultInstance = new DefaultInstance();
        this.isTenantAdmin = isTenantAdmin;
        this.isAdmin = isAdmin;
        this.productGroupName = productGroupName;
        this.isClustered = Boolean.parseBoolean(getConfigurationValue(
                String.format("//productGroup[@name='%s']/@clusteringEnabled", productGroupName)));
        this.tenantsDomain = defaultInstance.getTennantDomain(isTenantAdmin, isClustered);
        this.userKey = defaultInstance.getTenant(tenantsDomain, isTenantAdmin);
        this.productGroupName = productGroupName;
        this.managerInstanceName = defaultInstance.getDefaultManager(productGroupName);
        this.workerInstanceName = defaultInstance.getDefaultWorker(productGroupName);

    }

    public AutomationContext() throws XPathExpressionException {
        DefaultInstance defaultInstance = new DefaultInstance();
        this.isTenantAdmin = true;
        this.isAdmin = true;
        this.productGroupName = this.getConfigurationValue("//automation/platform/productGroup[@default='true']/@name");
        this.isClustered = Boolean.parseBoolean(getConfigurationValue(
                String.format("//productGroup[@name='%s']/@clusteringEnabled", productGroupName)));
        this.tenantsDomain = defaultInstance.getTennantDomain(true, isClustered);
        this.userKey = defaultInstance.getTenant(tenantsDomain,isTenantAdmin);
        this.managerInstanceName = defaultInstance.getDefaultManager(productGroupName);
        this.workerInstanceName = defaultInstance.getDefaultWorker(productGroupName);
        System.setProperty("default.product.group", productGroupName);
    }

    /**
     * Provides a Instance applicable for the selected product group instance and tenant
     *
     * @return Instance
     * @throws NonExistenceException
     * @throws XPathExpressionException
     */
    public Instance getInstance() throws NonExistenceException, XPathExpressionException {
        Instance instance = new Instance();
        HashMap<String, String> portMap = new HashMap<String, String>();
        HashMap<String, String> hostMap = new HashMap<String, String>();
        HashMap<String, String> propertyMap = new HashMap<String, String>();
        Node instanceNode = this.getConfigurationNode("//productGroup[@name='" +
                productGroupName + "']/instance[@name='"
                + managerInstanceName + "']");
        NodeList ports = this.getConfigurationNodeList("//productGroup[@name='" +
                productGroupName + "']/instance[@name='"
                + managerInstanceName + "']/ports/port");
        NodeList hosts = this.getConfigurationNodeList("//productGroup[@name='" +
                productGroupName + "']/instance[@name='"
                + managerInstanceName + "']/hosts/host");
        NodeList properties = this.getConfigurationNodeList("//productGroup[@name='" +
                productGroupName + "']/instance[@name='"
                + managerInstanceName + "']/properties/property");

        for (int portNo = 0; portNo <= ports.getLength() - 1; portNo++) {
            Node portNode = ports.item(portNo);
            String portType = portNode.getAttributes().getNamedItem("type").getNodeValue();
            portMap.put(portType, portNode.getFirstChild().getNodeValue());
        }

        for (int hostNo = 0; hostNo <= hosts.getLength() - 1; hostNo++) {
            Node hostsNode = hosts.item(hostNo);
            String hostType = hostsNode.getAttributes().getNamedItem("type").getNodeValue();
            hostMap.put(hostType, hostsNode.getFirstChild().getNodeValue());
        }

        for (int propertyNo = 0; propertyNo <= properties.getLength() - 1; propertyNo++) {
            Node propertyNode = properties.item(propertyNo);
            String propertyType = propertyNode.getAttributes().getNamedItem("name").getNodeValue();
            propertyMap.put(propertyType, propertyNode.getFirstChild().getNodeValue());
        }
        instance.setName(instanceNode.getAttributes().getNamedItem("name").getNodeValue());
        instance.setType(instanceNode.getAttributes().getNamedItem("type").getNodeValue());
        instance.setPorts(portMap);
        instance.setHosts(hostMap);
        instance.setProperties(propertyMap);
        return instance;
    }

    public Instance getDefaultInstance() throws NonExistenceException, XPathExpressionException {
        Instance instance = new Instance();
        HashMap<String, String> portMap = new HashMap<String, String>();
        HashMap<String, String> hostMap = new HashMap<String, String>();
        HashMap<String, String> propertyMap = new HashMap<String, String>();
        Node instanceNode = this.getConfigurationNode("//productGroup[@default='true']/instance[@type='standalone']");
        NodeList ports = this.getConfigurationNodeList("//productGroup[@name='" +
                productGroupName + "']/instance[@name='"
                + managerInstanceName + "']/ports/port");
        NodeList hosts = this.getConfigurationNodeList("//productGroup[@name='" +
                productGroupName + "']/instance[@name='"
                + managerInstanceName + "']/hosts/host");
        NodeList properties = this.getConfigurationNodeList("//productGroup[@name='" +
                productGroupName + "']/instance[@name='"
                + managerInstanceName + "']/properties/property");

        for (int portNo = 0; portNo <= ports.getLength() - 1; portNo++) {
            Node portNode = ports.item(portNo);
            String portType = portNode.getAttributes().getNamedItem("type").getNodeValue();
            portMap.put(portType, portNode.getFirstChild().getNodeValue());
        }

        for (int hostNo = 0; hostNo <= hosts.getLength() - 1; hostNo++) {
            Node hostsNode = hosts.item(hostNo);
            String hostType = hostsNode.getAttributes().getNamedItem("type").getNodeValue();
            hostMap.put(hostType, hostsNode.getFirstChild().getNodeValue());
        }

        for (int propertyNo = 0; propertyNo <= properties.getLength() - 1; propertyNo++) {
            Node propertyNode = properties.item(propertyNo);
            String propertyType = propertyNode.getAttributes().getNamedItem("name").getNodeValue();
            propertyMap.put(propertyType, propertyNode.getFirstChild().getNodeValue());
        }
        instance.setName(instanceNode.getAttributes().getNamedItem("name").getNodeValue());
        instance.setType(instanceNode.getAttributes().getNamedItem("type").getNodeValue());
        instance.setPorts(portMap);
        instance.setHosts(hostMap);
        instance.setProperties(propertyMap);
        return instance;
    }


    /**
     * Return the super tenant with the tenant domain
     *
     * @return Tenant
     * @throws XPathExpressionException
     */
    public Tenant getSuperTenant() throws XPathExpressionException {
        Tenant tenant = new Tenant();
        tenant.setDomain(tenantsDomain);
        Node adminUserNode = this.getConfigurationNode("//superTenant/tenant/admin");
        NodeList adminUserList = adminUserNode.getChildNodes();
        for (int nodeNo = 0; nodeNo <= adminUserList.getLength() - 1; nodeNo++) {
            Node currentNode = adminUserList.item(nodeNo);
            if (currentNode.getNodeName().equals("user")) {
                tenant.setTenantAdmin(extractUser(currentNode));
            }
            ;
        }
        Node userNode = this.getConfigurationNode("//superTenant/tenant/users");
        NodeList childUserList = userNode.getChildNodes();
        for (int nodeNo = 0; nodeNo <= childUserList.getLength() - 1; nodeNo++) {
            Node currentNode = childUserList.item(nodeNo);
            if (currentNode.getNodeName().equals("user")) {
                User tenantUser = extractUser(currentNode);
                tenant.addTenantUsers(tenantUser);
            }
        }
        return tenant;
    }

    private User extractUser(Node currentNode) {
        User tenantUser = new User();
        NodeList userNodeList = currentNode.getChildNodes();
        tenantUser.setKey(currentNode.getAttributes().getNamedItem("key").getNodeValue());
        for (int userItem = 0; userItem <= userNodeList.getLength() - 1; userItem++) {
            if (userNodeList.item(userItem).getNodeName().equals("userName")) {
                tenantUser.setUserName(userNodeList.item(userItem).getTextContent());
            } else if (userNodeList.item(userItem).getNodeName().equals("password")) {
                tenantUser.setPassword(userNodeList.item(userItem).getTextContent());
            } else continue;
        }
        return tenantUser;
    }

    /**
     * Return the tenant with the tenant domain
     *
     * @return Tenant
     * @throws XPathExpressionException
     */
    public Tenant getTenant() throws XPathExpressionException {
        Tenant tenant = new Tenant();
        if (isTenantAdmin && !getIsClustered()) {
            tenant = getSuperTenant();
        } else {
            tenant = getOtherTenant();
        }
        return tenant;
    }

    private Tenant getOtherTenant() throws XPathExpressionException {
        Tenant tenant = new Tenant();
        tenant.setDomain(tenantsDomain);
        Node tenantNode = this.getConfigurationNode("//tenants/tenant[@domain='" + tenantsDomain + "']");
        Node adminUserNode = this.getConfigurationNode("//tenants/tenant[@domain='" + tenantsDomain + "']/admin");
        NodeList adminUserList = adminUserNode.getChildNodes();
        for (int nodeNo = 0; nodeNo <= adminUserList.getLength() - 1; nodeNo++) {
            Node currentNode = adminUserList.item(nodeNo);
            if (currentNode.getNodeName().equals("user")) {
                tenant.setTenantAdmin(extractUser(currentNode));
            }
        }
        Node userNode = this.getConfigurationNode("//tenants/tenant[@domain='" + tenantsDomain + "']/users");
        NodeList childUserList = userNode.getChildNodes();
        for (int nodeNo = 0; nodeNo <= childUserList.getLength() - 1; nodeNo++) {
            Node currentNode = childUserList.item(nodeNo);
            if (currentNode.getNodeName().equals("user")) {
                User tenantUser = extractUser(currentNode);
                tenant.addTenantUsers(tenantUser);
            } else continue;
        }
        return tenant;
    }

    /**
     * Returns applicable Product group
     *
     * @return ProductGroup
     * @throws XPathExpressionException
     * @throws NonExistenceException
     */
    public ProductGroup getProductGroup() throws XPathExpressionException, NonExistenceException {
        ProductGroup productGroup = new ProductGroup();
        Node productGroupNode = this.getConfigurationNode("//productGroup[@name='" + productGroupName + "']");
        productGroup.setGroupName(productGroupNode.getAttributes().getNamedItem("name").getNodeValue());
        productGroup.setClusterEnabled(Boolean.valueOf(productGroupNode.getAttributes()
                .getNamedItem("clusteringEnabled").getNodeValue()));
        NodeList childProductGroupList = productGroupNode.getChildNodes();
        for (int nodeNo = 0; nodeNo <= childProductGroupList.getLength() - 1; nodeNo++) {
            if (childProductGroupList.item(nodeNo).getNodeName().equals("instance")) {
                String instanceName = childProductGroupList.item(nodeNo).getAttributes().getNamedItem("name")
                        .getNodeValue();
                productGroup.addInstance(getInstance(productGroupName, instanceName));
            }
        }
        productGroup.setInstanceMapByType(setInstanceMapByType(childProductGroupList));
        return productGroup;
    }

    /**
     * Applicable tenant USer
     *
     * @return
     * @throws XPathExpressionException
     */
    public User getUser() throws XPathExpressionException {
        User tenantUser = new User();
        if (isTenantAdmin) {
            tenantUser = getSuperTenantUser();
        } else {
            tenantUser = getOtherUser();
        }
        return tenantUser;
    }

    private User getOtherUser() throws XPathExpressionException {
        User tenantUser = new User();
        Node tenantUserNode;
        String userName;
        String password;
        if (isAdmin) {
            tenantUserNode = this.getConfigurationNode("//tenants/tenant[@domain='"
                    + tenantsDomain + "']/admin/user");
            userName = this.getConfigurationValue("//tenants/tenant[@domain='"
                    + tenantsDomain + "']/admin/user/userName");
            password = this.getConfigurationValue("//tenants/tenant[@domain='"
                    + tenantsDomain + "']/admin/user/password");
        } else {
            tenantUserNode = this.getConfigurationNode("//tenants/tenant[@domain='"
                    + tenantsDomain + "']/users/user[@key='" + userKey + "']");
            userName = this.getConfigurationValue("//tenants/tenant[@domain='"
                    + tenantsDomain + "']/users/user[@key='" + userKey + "']/userName");
            password = this.getConfigurationValue("//tenants/tenant[@domain='"
                    + tenantsDomain + "']/users/user[@key='" + userKey + "']/password");
        }
        tenantUser.setUserName(userName);
        tenantUser.setPassword(password);
        tenantUser.setKey(tenantUserNode.getAttributes().getNamedItem("key").getNodeValue());
        return tenantUser;
    }

    /**
     * Applicable tenant admin User
     *
     * @return
     * @throws XPathExpressionException
     */
    private User getSuperTenantUser() throws XPathExpressionException {
        User tenantUser = new User();
        String userName;
        String password;
        Node tenantUserNode;
        if (isAdmin) {
            tenantUserNode = this.getConfigurationNode("//superTenant/tenant/admin/user");
            userName = this.getConfigurationValue("//superTenant/tenant/admin/user/userName");
            password = this.getConfigurationValue("//superTenant/tenant/admin/user/password");
        } else {
            tenantUserNode = this.getConfigurationNode("//superTenant/tenant/users/user[@key='"
                    + userKey + "']");
            userName = this.getConfigurationValue("//superTenant/tenant/users/user[@key='"
                    + userKey + "']/userName/text()");
            password = this.getConfigurationValue("//superTenant/tenant/users/user[@key='"
                    + userKey + "']/password");
        }
        tenantUser.setUserName(userName);
        tenantUser.setPassword(password);
        tenantUser.setKey(tenantUserNode.getAttributes().getNamedItem("key").getNodeValue());
        return tenantUser;
    }

    /**
     * Returns all URLS needed for the test built upon the configuration
     *
     * @return
     * @throws NonExistenceException
     */
    public ContextUrls getContextUrls() throws NonExistenceException, XPathExpressionException {
        ContextUrls contextUrls = new ContextUrls();
        contextUrls.setBackEndUrl(UrlGenerationUtil.getBackendURL(this.getInstance()));
        contextUrls.setServiceUrl(UrlGenerationUtil.getServiceUrl(this.getTenant(), this.getInstance(), false));
        contextUrls.setSecureServiceUrl(UrlGenerationUtil.getServiceUrl(this.getTenant(), this.getInstance(), true));
        contextUrls.setWebAppURL(UrlGenerationUtil.getWebAppURL(this.getTenant(), this.getInstance()));
        return contextUrls;
    }

    public String login() throws NonExistenceException, RemoteException,
            LoginAuthenticationExceptionException, XPathExpressionException {
        String sessionCookie;
        AuthenticationAdminClient authenticationAdminClient
                = new AuthenticationAdminClient(UrlGenerationUtil.getBackendURL(this.getInstance()));
        sessionCookie = authenticationAdminClient.
                login(tenantsDomain, this.getUser().getUserName(), this.getUser().getPassword()
                        , this.getInstance().getHosts().get("default"));
        return sessionCookie;
    }

    /**
     * Provides configuration value
     *
     * @param expression xpath for expected element
     * @return
     * @throws XPathExpressionException
     */
    public String getConfigurationValue(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();
        XPath xPath = XPathFactory.newInstance().newXPath();
        return xPath.compile(expression).evaluate(xmlDocument);
    }

    /**
     * Provides DOM Node
     *
     * @param expression xpath for expected element
     * @return
     * @throws XPathExpressionException
     */
    public Node getConfigurationNode(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();
        XPath xPath = XPathFactory.newInstance().newXPath();
        return (Node) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODE);
    }

    /**
     * Provides DOM NodeList
     *
     * @param expression xpath for expected element
     * @return
     * @throws XPathExpressionException
     */
    public NodeList getConfigurationNodeList(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();
        XPath xPath = XPathFactory.newInstance().newXPath();
        return (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
    }

    private Map<String, ArrayList<Instance>> setInstanceMapByType(NodeList childProductGroupList)
            throws XPathExpressionException, NonExistenceException {
        Map<String, ArrayList<Instance>> instanceMapByType = new HashMap<String, ArrayList<Instance>>();
        for (InstanceType dir : InstanceType.values()) {
            ArrayList<Instance> instanceList = new ArrayList<Instance>();
            for (int nodeNo = 0; nodeNo <= childProductGroupList.getLength() - 1; nodeNo++) {
                if (childProductGroupList.item(nodeNo).getNodeName().equals("instance")) {
                    String instanceName = childProductGroupList.item(nodeNo).getAttributes().getNamedItem("name")
                            .getNodeValue();
                    String type = childProductGroupList.item(nodeNo).getAttributes().getNamedItem("type")
                            .getNodeValue();
                    if (type.equals(dir.name())) {
                        instanceList.add(getInstance(productGroupName, instanceName));
                    }
                }
            }
            if (!instanceList.isEmpty()) {
                instanceMapByType.put(dir.name(), instanceList);
            }
        }
        return instanceMapByType;
    }

    private Instance getInstance(String groupName, String instName) throws NonExistenceException,
            XPathExpressionException {
        Instance instance = new Instance();
        HashMap<String, String> portMap = new HashMap<String, String>();
        HashMap<String, String> hostMap = new HashMap<String, String>();
        HashMap<String, String> propertyMap = new HashMap<String, String>();
        Node instanceNode = this.getConfigurationNode("//productGroup[@name='" + groupName
                + "']/instance[ @name='" + instName + "']");
        NodeList ports = this.getConfigurationNodeList("//productGroup[@name='" + groupName
                + "']/instance[@name='" + instName + "']/ports/port");
        NodeList hosts = this.getConfigurationNodeList("//productGroup[@name='" + groupName
                + "']/instance[@name='" + instName + "']/hosts/host");
        NodeList properties = this.getConfigurationNodeList("//productGroup[@name='" + groupName
                + "']/instance[@name='" + instName + "']/properties/property");

        for (int portNo = 0; portNo <= ports.getLength() - 1; portNo++) {
            Node portNode = ports.item(portNo);
            String portType = portNode.getAttributes().getNamedItem("type").getNodeValue();
            portMap.put(portType, portNode.getFirstChild().getNodeValue());
        }

        for (int hostNo = 0; hostNo <= hosts.getLength() - 1; hostNo++) {
            Node hostsNode = hosts.item(hostNo);
            String hostType = hostsNode.getAttributes().getNamedItem("type").getNodeValue();
            hostMap.put(hostType, hostsNode.getFirstChild().getNodeValue());
        }

        for (int propertyNo = 0; propertyNo <= properties.getLength() - 1; propertyNo++) {
            Node propertyNode = properties.item(propertyNo);
            String propertyType = propertyNode.getAttributes().getNamedItem("name").getNodeValue();
            propertyMap.put(propertyType, propertyNode.getFirstChild().getNodeValue());
        }
        instance.setName(instanceNode.getAttributes().getNamedItem("name").getNodeValue());
        instance.setType(instanceNode.getAttributes().getNamedItem("type").getNodeValue());
        instance.setPorts(portMap);
        instance.setHosts(hostMap);
        instance.setProperties(propertyMap);
        return instance;
    }
}

