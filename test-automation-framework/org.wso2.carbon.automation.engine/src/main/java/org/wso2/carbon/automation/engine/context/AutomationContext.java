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
import org.codehaus.plexus.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.configurations.AutomationConfiguration;
import org.wso2.carbon.automation.engine.configurations.ConfigurationErrorChecker;
import org.wso2.carbon.automation.engine.configurations.UrlGenerationUtil;
import org.wso2.carbon.automation.engine.context.beans.*;
import org.wso2.carbon.automation.engine.exceptions.ConfigurationMismatchException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This Class provides context providing interface for the test
 */
public class AutomationContext {
    private static final Log log = LogFactory.getLog(AutomationContext.class);

    private String productGroupName;
    private String workerInstanceName;
    private String managerInstanceName;
    private boolean isClustered;
    private boolean isSuperTenant;
    private boolean isAdminUser;
    private String tenantDomain;
    private String userKey;
    private Tenant superTenant ;
    private Tenant contextTenant ;

    /**
     * The context constructor for where to use with exact instance
     *
     * @param productGroupName - ProductGroup name specified in test framework configuration
     * @param instanceName     - Instance name specified in test framework configuration
     * @param tenantDomainKey  - ProductGroup name specified in test framework configuration
     * @param userKey          - user key of the tenant
     */
    public AutomationContext(String productGroupName, String instanceName, String tenantDomainKey,
                             String userKey)
            throws XPathExpressionException {
        String superTenantReplacement = ContextXpathConstants.TENANTS;
        this.isSuperTenant = false;
        this.isAdminUser = false;
        if (tenantDomainKey.equals(this.getConfigurationValue
                (ContextXpathConstants.USER_MANAGEMENT_SUPER_TENANT_KEY))) {
            this.isSuperTenant = true;
            superTenantReplacement = this.getConfigurationValue
                    (ContextXpathConstants.USER_MANAGEMENT_SUPER_TENANT_KEY);
        }
        if (userKey.equals(ContextXpathConstants.SUPER_ADMIN) || userKey.equals(ContextXpathConstants.ADMIN)) {
            this.isAdminUser = true;
        }
        this.productGroupName = productGroupName;
        //this.workerInstanceName = instanceName;
        this.managerInstanceName = instanceName;
        this.isClustered = Boolean.parseBoolean(getConfigurationValue(
                String.format(ContextXpathConstants.PRODUCT_GROUP_CLUSTERING_ENABLED, productGroupName)));
        this.tenantDomain = getConfigurationValue(String.format(ContextXpathConstants.USER_MANAGEMENT_TENANT_DOMAIN,
                                                                superTenantReplacement, tenantDomainKey));
        this.userKey = userKey;
    }

    /**
     * This constructor is used to create automation context for the combination of given product group, given instance
     * and user
     *
     * @param productGroupName product group
     * @param instanceName     name of the instance
     * @param userMode         can give the combination of the tenant and the user of that tenant
     * @throws XPathExpressionException
     */
    public AutomationContext(String productGroupName, String instanceName, TestUserMode userMode)
            throws XPathExpressionException {
        //admin user of the super tenant
        if (userMode.name().equals(ContextXpathConstants.SUPER_TENANT_ADMIN)) {
            assignParameters(productGroupName, instanceName, true, true);
        } else if (userMode.name().equals(ContextXpathConstants.SUPER_TENANT_USER)) {
            //user of the super tenant
            assignParameters(productGroupName, instanceName, true, false);
        }
        //admin user of tenant other than super tenant
        else if (userMode.name().equals(ContextXpathConstants.TENANT_ADMIN)) {
            assignParameters(productGroupName, instanceName, false, true);
        }
        //user of a tenant other than super tenant
        else if (userMode.name().equals(ContextXpathConstants.TENANT_USER)) {
            assignParameters(productGroupName, instanceName, false, false);
        }
    }

    /**
     * This constructor is used to create automation context for the combination of given product group and user
     *
     * @param productGroupName product group
     * @param testUserMode     can give the combination of the tenant and the user of that tenant
     * @throws XPathExpressionException
     */
    public AutomationContext(String productGroupName, TestUserMode testUserMode)
            throws XPathExpressionException {

        //admin user of the super tenant
        if (testUserMode.name().equals(ContextXpathConstants.SUPER_TENANT_ADMIN)) {
            assignParameters(productGroupName, null, true, true);
            //user of the super tenant
        } else if (testUserMode.name().equals(ContextXpathConstants.SUPER_TENANT_USER)) {
            assignParameters(productGroupName, null, true, false);
            //admin user of tenant other than super tenant
        } else if (testUserMode.name().equals(ContextXpathConstants.TENANT_ADMIN)) {
            assignParameters(productGroupName, null, false, true);
        }
        //user of a tenant other than super tenant
        else if (testUserMode.name().equals(ContextXpathConstants.TENANT_USER)) {
            assignParameters(productGroupName, null, false, false);
        }
    }

    public AutomationContext() throws XPathExpressionException {
        DefaultInstance defaultInstance = new DefaultInstance();
        this.isSuperTenant = true;
        this.isAdminUser = true;
        this.productGroupName = this.getConfigurationValue(ContextXpathConstants.PRODUCT_GROUP_DEFAULT_NAME);
        log.warn("BBBB productGroupName : " + this.productGroupName);

//        tryLoadXML();
//
//        if (StringUtils.isBlank(productGroupName)) {
//            productGroupName = "DSS";
//        }

        log.warn("CCCC productGroupName : " + this.productGroupName);
        this.isClustered = Boolean.parseBoolean(getConfigurationValue(
                String.format(ContextXpathConstants.PRODUCT_GROUP_CLUSTERING_ENABLED, productGroupName)));
        this.tenantDomain = defaultInstance.getTenantDomain(true, isClustered);
        this.userKey = defaultInstance.getUserKey(tenantDomain, true);
        this.managerInstanceName = defaultInstance.getDefaultManager(this.productGroupName);
        this.workerInstanceName = defaultInstance.getDefaultWorker(productGroupName);
        System.setProperty(FrameworkConstants.DEFAULT_PRODUCT_GROUP, productGroupName);
    }

    private void tryLoadXML() {
        try {
            File fXmlFile = new File(FrameworkPathUtil.
                    getSystemResourceLocation() + FrameworkConstants.CONFIGURATION_FILE_NAME);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

            //remove all comments from the content of the automation.xml
            dbFactory.setIgnoringComments(true);
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document xmlDocument = dBuilder.parse(fXmlFile);

            //remove all text(empty) elements
            removeText(xmlDocument);
            xmlDocument.normalizeDocument();

            //check for semantics errors in configuration file
            ConfigurationErrorChecker.checkPlatformErrors(xmlDocument);

            String expression = "//productGroup[@default='true']/@name";

            System.out.println("FFFF expression : " + expression);

            DOMSource domSource = new DOMSource(xmlDocument);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter sw = new StringWriter();
            StreamResult sr = new StreamResult(sw);
            transformer.transform(domSource, sr);

            log.warn("JJJJ xmlDocument : " + sw.toString());

            XPath xPath = XPathFactory.newInstance().newXPath();
//        xPath.setNamespaceContext(new MyNamespaceContext());
            String value = xPath.compile(expression).evaluate(xmlDocument);

            log.warn("JJJJ Match : " + value);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            log.error("JJJJ",e);
        } catch (ParserConfigurationException e) {
            log.error("JJJJ",e);
        } catch (ConfigurationMismatchException e) {
            log.error("JJJJ",e);
        } catch (SAXException e) {
            log.error("JJJJ",e);
        } catch (TransformerException e) {
            log.error("JJJJ",e);
        } catch (XPathExpressionException e) {
            log.error("JJJJ",e);
        }
    }

    private static void removeText(Node doc) throws XPathExpressionException {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        // XPath to find empty text nodes.
        XPathExpression xpathExp = xpathFactory.newXPath().compile(
                "//text()[normalize-space(.) = '']");
        NodeList emptyTextNodes = (NodeList)
                xpathExp.evaluate(doc, XPathConstants.NODESET);

        // Remove each empty text node from document.
        for (int i = 0; i < emptyTextNodes.getLength(); i++) {
            Node emptyTextNode = emptyTextNodes.item(i);
            emptyTextNode.getParentNode().removeChild(emptyTextNode);
        }
    }

    /**
     * This method redirect the call to the constructor
     *
     * @param productGroupName Product Group name
     * @param instanceName     Instance name
     * @param isSuperTenant    Whether the provided tenant is super tenant or not
     * @param isAdminUser      whether the provided user is admin user is not
     */
    private void assignParameters(String productGroupName, String instanceName,
                                  boolean isSuperTenant,
                                  boolean isAdminUser)
            throws XPathExpressionException {
        DefaultInstance defaultInstance = new DefaultInstance();
        this.isSuperTenant = isSuperTenant;
        this.isAdminUser = isAdminUser;
        this.productGroupName = productGroupName;
        this.isClustered = Boolean.parseBoolean(getConfigurationValue(
                String.format(ContextXpathConstants.PRODUCT_GROUP_CLUSTERING_ENABLED, productGroupName)));
        this.tenantDomain = defaultInstance.getTenantDomain(isSuperTenant, isClustered);
        this.userKey = defaultInstance.getUserKey(tenantDomain, isAdminUser);
        if (instanceName == null) {
            this.managerInstanceName = defaultInstance.getDefaultManager(productGroupName);
            this.workerInstanceName = defaultInstance.getDefaultWorker(productGroupName);
        } else {
            this.workerInstanceName = instanceName;
            this.managerInstanceName = instanceName;
        }
    }

    private boolean getIsClustered() throws XPathExpressionException {
        return Boolean.parseBoolean(getConfigurationValue(
                String.format(ContextXpathConstants.PRODUCT_GROUP_CLUSTERING_ENABLED, productGroupName)));
    }

    /**
     * Provides a Instance applicable for the selected product group instance and tenant
     *
     * @return Instance
     * @throws XPathExpressionException
     */
    public Instance getInstance() throws XPathExpressionException {
        Instance instance = new Instance();
        HashMap<String, String> portMap = new HashMap<String, String>();
        HashMap<String, String> hostMap = new HashMap<String, String>();
        HashMap<String, String> propertyMap = new HashMap<String, String>();
        Node instanceNode = this.getConfigurationNode(String.format(ContextXpathConstants.PRODUCT_GROUP_INSTANCE_NAME,
                                                                    productGroupName, managerInstanceName));
        NodeList ports = this.getConfigurationNodeList(String.
                format(ContextXpathConstants.PRODUCT_GROUP_INSTANCE_PORT, productGroupName, managerInstanceName));
        NodeList hosts = this.getConfigurationNodeList(String.format
                (ContextXpathConstants.PRODUCT_GROUP_INSTANCE_HOST, productGroupName, managerInstanceName));
        NodeList properties = this.getConfigurationNodeList(String.format
                (ContextXpathConstants.PRODUCT_GROUP_INSTANCE_PROPERTY, productGroupName, managerInstanceName));
        for (int portNo = 0; portNo <= ports.getLength() - 1; portNo++) {
            Node portNode = ports.item(portNo);
            String portType = portNode.getAttributes().getNamedItem(ContextXpathConstants.TYPE).getNodeValue();
            portMap.put(portType, portNode.getFirstChild().getNodeValue());
        }
        for (int hostNo = 0; hostNo <= hosts.getLength() - 1; hostNo++) {
            Node hostsNode = hosts.item(hostNo);
            String hostType = hostsNode.getAttributes().getNamedItem(ContextXpathConstants.TYPE).getNodeValue();
            hostMap.put(hostType, hostsNode.getFirstChild().getNodeValue());
        }

        for (int propertyNo = 0; propertyNo <= properties.getLength() - 1; propertyNo++) {
            Node propertyNode = properties.item(propertyNo);
            String propertyType = propertyNode.getAttributes().getNamedItem(ContextXpathConstants.NAME).getNodeValue();
            propertyMap.put(propertyType, propertyNode.getFirstChild().getNodeValue());
        }
        instance.setName(instanceNode.getAttributes().getNamedItem(ContextXpathConstants.NAME).getNodeValue());
        instance.setType(instanceNode.getAttributes().getNamedItem(ContextXpathConstants.TYPE).getNodeValue());
        instance.setNonBlockingTransportEnabled(Boolean.parseBoolean(instanceNode.getAttributes().getNamedItem
                (ContextXpathConstants.NON_BLOCKING_ENABLED).getNodeValue()));
        instance.setPorts(portMap);
        instance.setHosts(hostMap);
        instance.setProperties(propertyMap);
        return instance;
    }

    public Instance getDefaultInstance() throws XPathExpressionException {
        Instance instance = new Instance();
        HashMap<String, String> portMap = new HashMap<String, String>();
        HashMap<String, String> hostMap = new HashMap<String, String>();
        HashMap<String, String> propertyMap = new HashMap<String, String>();
        Node instanceNode = this.getConfigurationNode(ContextXpathConstants.PRODUCT_GROUP_STANDALONE_INSTANCE);
        NodeList ports = this.getConfigurationNodeList(String.
                format(ContextXpathConstants.PRODUCT_GROUP_INSTANCE_PORT, productGroupName, managerInstanceName));
        NodeList hosts = this.getConfigurationNodeList(String.format(ContextXpathConstants.PRODUCT_GROUP_INSTANCE_HOST,
                                                                     productGroupName, managerInstanceName));
        NodeList properties = this.getConfigurationNodeList(String.
                format(ContextXpathConstants.PRODUCT_GROUP_INSTANCE_PROPERTY, productGroupName, managerInstanceName));
        for (int portNo = 0; portNo <= ports.getLength() - 1; portNo++) {
            Node portNode = ports.item(portNo);
            String portType = portNode.getAttributes().getNamedItem(ContextXpathConstants.TYPE).getNodeValue();
            portMap.put(portType, portNode.getFirstChild().getNodeValue());
        }
        for (int hostNo = 0; hostNo <= hosts.getLength() - 1; hostNo++) {
            Node hostsNode = hosts.item(hostNo);
            String hostType = hostsNode.getAttributes().getNamedItem(ContextXpathConstants.TYPE).getNodeValue();
            hostMap.put(hostType, hostsNode.getFirstChild().getNodeValue());
        }
        for (int propertyNo = 0; propertyNo <= properties.getLength() - 1; propertyNo++) {
            Node propertyNode = properties.item(propertyNo);
            String propertyType = propertyNode.getAttributes().getNamedItem(ContextXpathConstants.NAME).getNodeValue();
            propertyMap.put(propertyType, propertyNode.getFirstChild().getNodeValue());
        }
        instance.setName(instanceNode.getAttributes().getNamedItem(ContextXpathConstants.NAME).getNodeValue());
        instance.setType(instanceNode.getAttributes().getNamedItem(ContextXpathConstants.TYPE).getNodeValue());
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
        //if tenant already set , return current value
        if(superTenant != null) {
            return superTenant;
        }
        superTenant = new Tenant();
        String superDomain = this.getConfigurationValue(ContextXpathConstants.USER_MANAGEMENT_SUPER_TENANT_DOMAIN);
        superTenant.setDomain(superDomain);
        Node adminUserNode = this.getConfigurationNode(ContextXpathConstants.USER_MANAGEMENT_SUPER_TENANT_ADMIN);
        NodeList adminUserList = adminUserNode.getChildNodes();
        for (int nodeNo = 0; nodeNo <= adminUserList.getLength() - 1; nodeNo++) {
            Node currentNode = adminUserList.item(nodeNo);
            if (currentNode.getNodeName().equals(ContextXpathConstants.USER)) {
                superTenant.setTenantAdmin(extractUser(currentNode, superDomain, true));
            }
        }
        Node userNode = this.getConfigurationNode(ContextXpathConstants.USER_MANAGEMENT_SUPER_TENANT_USERS);
        NodeList childUserList = userNode.getChildNodes();
        for (int nodeNo = 0; nodeNo <= childUserList.getLength() - 1; nodeNo++) {
            Node currentNode = childUserList.item(nodeNo);
            if (currentNode.getNodeName().equals(ContextXpathConstants.USER)) {
                User tenantUser = extractUser(currentNode, superDomain, true);
                superTenant.addTenantUsers(tenantUser);
            }
        }
        superTenant.setContextUser(this.getUser());
        return superTenant;
    }

    private User extractUser(Node currentNode, String tenantDomain, boolean isTenantSuper) {
        User tenantUser = new User();
        String userName = null;
        NodeList userNodeList = currentNode.getChildNodes();
        tenantUser.setKey(currentNode.getAttributes().getNamedItem(ContextXpathConstants.KEY).getNodeValue());
        for (int userItem = 0; userItem <= userNodeList.getLength() - 1; userItem++) {
            if (userNodeList.item(userItem).getNodeName().equals(ContextXpathConstants.USERNAME)) {
                userName = userNodeList.item(userItem).getTextContent();
            } else if (userNodeList.item(userItem).getNodeName().equals(ContextXpathConstants.PASSWORD)) {
                tenantUser.setPassword(userNodeList.item(userItem).getTextContent());
            } else if (userNodeList.item(userItem).getNodeName().equals(ContextXpathConstants.ROLES)) {
                NodeList roleList = userNodeList.item(userItem).getChildNodes();
                for (int i = 0; i < roleList.getLength(); i++) {
                    tenantUser.addRole(roleList.item(i).getTextContent());
                }
            }
        }
        if (tenantDomain.equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
            tenantUser.setUserName(userName);
        } else {
            tenantUser.setUserName(userName + "@" + tenantDomain);
        }
        return tenantUser;
    }

    /**
     * Return the tenant with the tenant domain
     *
     * @return Tenant
     * @throws XPathExpressionException
     */
    public Tenant getContextTenant() throws XPathExpressionException {
        Tenant tenant;
        if (isSuperTenant) {
            tenant = getSuperTenant();
        } else {
            tenant = getNonSuperTenant();
        }
        return tenant;
    }

    private Tenant getNonSuperTenant() throws XPathExpressionException {
        if(contextTenant != null) {
            return contextTenant;
        }
        contextTenant = new Tenant();
        contextTenant.setDomain(tenantDomain);
        Node adminUserNode = this.getConfigurationNode(String.
                format(ContextXpathConstants.USER_MANAGEMENT_TENANT_ADMIN, tenantDomain));
        NodeList adminUserList = adminUserNode.getChildNodes();
        for (int nodeNo = 0; nodeNo <= adminUserList.getLength() - 1; nodeNo++) {
            Node currentNode = adminUserList.item(nodeNo);
            if (currentNode.getNodeName().equals(ContextXpathConstants.USER)) {
                contextTenant.setTenantAdmin(extractUser(currentNode, tenantDomain, true));
            }
        }
        Node userNode = this.getConfigurationNode(String.
                format(ContextXpathConstants.USER_MANAGEMENT_TENANT_USERS, tenantDomain));
        NodeList childUserList = userNode.getChildNodes();
        for (int nodeNo = 0; nodeNo <= childUserList.getLength() - 1; nodeNo++) {
            Node currentNode = childUserList.item(nodeNo);
            if (currentNode.getNodeName().equals(ContextXpathConstants.USER)) {
                User tenantUser = extractUser(currentNode, tenantDomain, isSuperTenant);
                contextTenant.addTenantUsers(tenantUser);
            }
        }
        contextTenant.setContextUser(this.getUser());
        return contextTenant;
    }

    /**
     * Returns applicable Product group
     *
     * @return ProductGroup
     * @throws XPathExpressionException
     */
    public ProductGroup getProductGroup() throws XPathExpressionException {
        ProductGroup productGroup = new ProductGroup();
        Node productGroupNode = this.getConfigurationNode(String.
                format(ContextXpathConstants.PRODUCT_GROUP_NAME, productGroupName));
        productGroup.setGroupName(productGroupNode.getAttributes().getNamedItem(ContextXpathConstants.NAME).getNodeValue());
        productGroup.setClusterEnabled(Boolean.valueOf(productGroupNode.getAttributes()
                                                               .getNamedItem(ContextXpathConstants.CLUSTERING_ENABLED).getNodeValue()));
        NodeList childProductGroupList = productGroupNode.getChildNodes();
        for (int nodeNo = 0; nodeNo <= childProductGroupList.getLength() - 1; nodeNo++) {
            if (childProductGroupList.item(nodeNo).getNodeName().equals(ContextXpathConstants.INSTANCE)) {
                String instanceName = childProductGroupList.item(nodeNo).getAttributes().
                        getNamedItem(ContextXpathConstants.NAME)
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
     * @return User
     * @throws XPathExpressionException
     */
    private User getUser() throws XPathExpressionException {
        User tenantUser;
        if (isAdminUser) {
            tenantUser = getAdminUser();
        } else {
            tenantUser = getOtherUser();
        }
        return tenantUser;
    }

    private User getOtherUser() throws XPathExpressionException {
        String superUserReplacement = ContextXpathConstants.TENANTS;
        if (isSuperTenant) {
            superUserReplacement = ContextXpathConstants.SUPER_TENANT;
        }
        User tenantUser = new User();
        Node tenantUserNode = this.getConfigurationNode(String.format(ContextXpathConstants.USER_MANAGEMENT_TENANT_USER,
                                                                      superUserReplacement, tenantDomain, userKey));
        String userName = this.getConfigurationValue(String.format(ContextXpathConstants.
                                                                           USER_MANAGEMENT_TENANT_USER_NAME, superUserReplacement, tenantDomain, userKey));
        String password = this.getConfigurationValue(String.format(ContextXpathConstants.
                                                                           USER_MANAGEMENT_TENANT_USER_PASSWORD, superUserReplacement, tenantDomain, userKey));
        tenantUser.setUserName(userName + "@" + tenantDomain);
        tenantUser.setPassword(password);
        tenantUser.setKey(tenantUserNode.getAttributes().getNamedItem(ContextXpathConstants.KEY).getNodeValue());

        NodeList roleList = this.getConfigurationNodeList(
                String.format(ContextXpathConstants.USER_MANAGEMENT_TENANT_USERS_ROLES,
                              superUserReplacement, tenantDomain, userKey));
        for (int i = 0; i < roleList.getLength(); i++) {
            tenantUser.addRole(roleList.item(i).getTextContent());
        }

        return tenantUser;
    }

    /**
     * Applicable tenant admin User
     *
     * @return User
     * @throws XPathExpressionException
     */
    private User getAdminUser() throws XPathExpressionException {

        String superUserReplacement = ContextXpathConstants.TENANTS;
        if (isSuperTenant) {
            superUserReplacement = ContextXpathConstants.SUPER_TENANT;
        }
        User tenantUser = new User();
        String userName = this.getConfigurationValue(String.format(ContextXpathConstants.
                                                                           USER_MANAGEMENT_TENANT_ADMIN_USERNAME, superUserReplacement, tenantDomain, userKey));
        String password = this.getConfigurationValue(String.format(ContextXpathConstants.
                                                                           USER_MANAGEMENT_TENANT_ADMIN_PASSWORD, superUserReplacement, tenantDomain, userKey));
        if (isSuperTenant) {
            tenantUser.setUserName(userName);
        } else {
            tenantUser.setUserName(userName + "@" + tenantDomain);
        }
        tenantUser.setPassword(password);
        tenantUser.setKey(userKey);
        return tenantUser;
    }

    /**
     * Returns all URLS needed for the test built upon the configuration
     *
     * @return ContextUrls
     * @throws XPathExpressionException
     */
    public ContextUrls getContextUrls() throws XPathExpressionException {
        ContextUrls contextUrls = new ContextUrls();
        try {
            contextUrls.setBackEndUrl(UrlGenerationUtil.getBackendURL(this.getInstance()));
            contextUrls.setServiceUrl(UrlGenerationUtil.getServiceURL(this.getContextTenant(),
                    this.getInstance(), false));
            contextUrls.setSecureServiceUrl(UrlGenerationUtil.getServiceURL(this.getContextTenant(),
                    this.getInstance(), true));
            contextUrls.setWebAppURL(UrlGenerationUtil.getWebAppURL(this.getContextTenant(),
                    this.getInstance()));
            contextUrls.setWebAppURLHttps(UrlGenerationUtil.getWebAppURLHttps(this.getContextTenant(),
                                                                    this.getInstance()));
        } catch (XPathExpressionException e) {
            throw new XPathExpressionException("configuration retrieve failed");
        }

        return contextUrls;
    }


    private Map<String, ArrayList<Instance>> setInstanceMapByType(NodeList childProductGroupList)
            throws XPathExpressionException {
        Map<String, ArrayList<Instance>> instanceMapByType = new HashMap<String, ArrayList<Instance>>();
        for (InstanceType dir : InstanceType.values()) {
            ArrayList<Instance> instanceList = new ArrayList<Instance>();
            for (int nodeNo = 0; nodeNo <= childProductGroupList.getLength() - 1; nodeNo++) {
                if (childProductGroupList.item(nodeNo).getNodeName().
                        equals(ContextXpathConstants.INSTANCE)) {
                    String instanceName = childProductGroupList.item(nodeNo).getAttributes().
                            getNamedItem(ContextXpathConstants.NAME)
                            .getNodeValue();
                    String type = childProductGroupList.item(nodeNo).getAttributes().
                            getNamedItem(ContextXpathConstants.TYPE)
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

    private Instance getInstance(String groupName, String instName)
            throws XPathExpressionException {
        Instance instance = new Instance();
        HashMap<String, String> portMap = new HashMap<String, String>();
        HashMap<String, String> hostMap = new HashMap<String, String>();
        HashMap<String, String> propertyMap = new HashMap<String, String>();
        Node instanceNode = this.getConfigurationNode
                (String.format(ContextXpathConstants.PRODUCT_GROUP_INSTANCE,
                               groupName, instName));
        NodeList ports = this.getConfigurationNodeList
                (String.format(ContextXpathConstants.PRODUCT_GROUP_INSTANCE_PORTS,
                               groupName, instName));
        NodeList hosts = this.getConfigurationNodeList
                (String.format(ContextXpathConstants.PRODUCT_GROUP_INSTANCE_HOSTS,
                               groupName, instName));
        NodeList properties = this.getConfigurationNodeList
                (String.format(ContextXpathConstants.PRODUCT_GROUP_INSTANCE_PROPERTIES,
                               groupName, instName));
        for (int portNo = 0; portNo <= ports.getLength() - 1; portNo++) {
            Node portNode = ports.item(portNo);
            String portType = portNode.getAttributes()
                    .getNamedItem(ContextXpathConstants.TYPE).getNodeValue();
            portMap.put(portType, portNode.getFirstChild().getNodeValue());
        }
        for (int hostNo = 0; hostNo <= hosts.getLength() - 1; hostNo++) {
            Node hostsNode = hosts.item(hostNo);
            String hostType = hostsNode.getAttributes()
                    .getNamedItem(ContextXpathConstants.TYPE).getNodeValue();
            hostMap.put(hostType, hostsNode.getFirstChild().getNodeValue());
        }
        for (int propertyNo = 0; propertyNo <= properties.getLength() - 1; propertyNo++) {
            Node propertyNode = properties.item(propertyNo);
            String propertyType = propertyNode.getAttributes()
                    .getNamedItem(ContextXpathConstants.NAME).getNodeValue();
            propertyMap.put(propertyType, propertyNode.getFirstChild().getNodeValue());
        }
        instance.setName(instanceNode.getAttributes()
                                 .getNamedItem(ContextXpathConstants.NAME).getNodeValue());
        instance.setType(instanceNode.getAttributes()
                                 .getNamedItem(ContextXpathConstants.TYPE).getNodeValue());
        instance.setPorts(portMap);
        instance.setHosts(hostMap);
        instance.setProperties(propertyMap);
        return instance;
    }

    /**
     * Provides configuration value
     *
     * @param expression xpath for expected element
     * @return String
     * @throws XPathExpressionException
     */
    public String getConfigurationValue(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();


        final String rootNamespace = AutomationConfiguration.getConfigurationDocument().getDocumentElement()
                .getNamespaceURI();

        NamespaceContext ctx = new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                String uri = null;
                if (prefix.equals("ns")) {
                    uri = rootNamespace;
                }
                return uri;
            }

            @Override
            public Iterator getPrefixes(String val) {
                throw new IllegalAccessError("Not implemented!");
            }

            @Override
            public String getPrefix(String uri) {
                throw new IllegalAccessError("Not implemented!");
            }
        };

        log.warn("AAAAA expression : " + expression);

        try {
            DOMSource domSource = new DOMSource(xmlDocument);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter sw = new StringWriter();
            StreamResult sr = new StreamResult(sw);
            transformer.transform(domSource, sr);

            log.warn("AAAAA xmlDocument : " + sw.toString());
        } catch (TransformerConfigurationException e) {
            log.error("TransformerConfigurationException" , e);
        } catch (TransformerException e) {
            log.error("TransformerException" , e);
        }

        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(ctx);
        return xPath.compile(expression).evaluate(xmlDocument);
    }

    /**
     * Provides DOM Node
     *
     * @param expression xpath for expected element
     * @return Node
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
     * @return Node List
     * @throws XPathExpressionException
     */
    public NodeList getConfigurationNodeList(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();
        XPath xPath = XPathFactory.newInstance().newXPath();
        return (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
    }

    /**
     * Replace value in document object
     *
     * @param expression xpath to locate the value
     * @param replaceBy  value to replace
     * @throws XPathExpressionException
     */
    public void replaceDocumentValue(String expression, String replaceBy)
            throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node node = (Node) xpath.compile(expression).evaluate(xmlDocument, XPathConstants.NODE);
        node.setTextContent(replaceBy);
    }

    public String getWorkerInstanceName() {
        return workerInstanceName;
    }

    /**
     * This is implemented to get tenant list described in automation.xml
     * @return - list of tenant names
     * @throws XPathExpressionException
     */
    public List<String> getTenantList() throws XPathExpressionException {
        List<String> tenantList = new ArrayList<String>();
        // add carbon.super
        tenantList.add(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME);

        // add other tenants
        NodeList tenantNodeList =
                this.getConfigurationNodeList(ContextXpathConstants.TENANTS_NODE)
                        .item(0)
                        .getChildNodes();
        for (int i = 0; i < tenantNodeList.getLength(); i++) {
            tenantList.add(
                    tenantNodeList.item(i).getAttributes()
                            .getNamedItem(ContextXpathConstants.DOMAIN).getNodeValue()
            );
        }
        return tenantList;
    }

    /**
     * This is to get user list of given tenant in automation.xml
     * @param tenantDomain - tenant name
     * @return - user name list
     * @throws XPathExpressionException
     */
    public List<String> getUserList(String tenantDomain) throws XPathExpressionException {
        List<String> userList = new ArrayList<String>();

        // set tenant type
        String tenantType = ContextXpathConstants.TENANTS;
        if (tenantDomain.equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
            tenantType = ContextXpathConstants.SUPER_TENANT;
        }

        NodeList userNodeList = this
                .getConfigurationNodeList(
                        String.format(ContextXpathConstants.USER_NODE, tenantType,
                                tenantDomain));

        for (int i = 0; i < userNodeList.getLength(); i++) {
            userList.add(userNodeList.item(i).getAttributes().getNamedItem("key").getNodeValue());
        }
        return userList;
    }
}
