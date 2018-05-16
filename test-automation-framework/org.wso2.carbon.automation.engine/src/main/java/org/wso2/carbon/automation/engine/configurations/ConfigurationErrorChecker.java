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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.ContextXpathConstants;
import org.wso2.carbon.automation.engine.exceptions.ConfigurationMismatchException;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;


/**
 * Checks the automation.xml configuration file for possible semantic errors
 */
public class ConfigurationErrorChecker {
    private static final Log log = LogFactory.getLog(ConfigurationErrorChecker.class);

    /**
     * check all kinds of semantic errors
     *
     * @throws org.wso2.carbon.automation.engine.exceptions.ConfigurationMismatchException
     */
    public void checkErrors(Document document)
            throws ConfigurationMismatchException, XPathExpressionException {
        checkUserContextErrors(document);
        checkPlatformErrors(document);
    }

    /**
     * checks errors in platform configurations
     *
     * @throws ConfigurationMismatchException
     * @throws XPathExpressionException
     */
    public static void checkPlatformErrors(Document document) throws ConfigurationMismatchException,
                                                                     XPathExpressionException {
        String executionEnv = getConfigurationValue
                (document, ContextXpathConstants.EXECUTION_ENVIRONMENT);
        int productGroupCount = getConfigurationNodeList
                (document, ContextXpathConstants.PRODUCT_GROUP_PARENT).getLength();
        int lbManagerNodeCount;
        int standaloneNodeCount;
        if (executionEnv.equals(FrameworkConstants.ENVIRONMENT_STANDALONE)) {
            //Standalone execution mode cannot have multiple product groups defined in the configurations
            if (productGroupCount > 1) {
                log.error("Standalone execution mode cannot have multiple product groups");
                throw new ConfigurationMismatchException("PlatformContext",
                                                         "Standalone execution mode cannot" +
                                                         " have multiple product groups");
            }
            standaloneNodeCount = getConfigurationNodeList
                    (document,
                     ContextXpathConstants.PRODUCT_GROUP_ALL_STANDALONE_INSTANCE).getLength();
            if (standaloneNodeCount == 0) {
                log.error("Product execution mode should have at least one standalone instance");
                throw new ConfigurationMismatchException
                        ("PlatformContext", "Product execution mode should have" +
                                            " at least one standalone instance");
            }
        }
    }

    /**
     * checks for errors in user management configurations
     *
     * @throws ConfigurationMismatchException
     */
    public static void checkUserContextErrors(Document document)
            throws ConfigurationMismatchException {
        //add rules form user management configuration here
    }

    /**
     * get value from the configurations
     *
     * @param xmlDocument
     * @param expression
     * @return
     * @throws XPathExpressionException
     */
    private static String getConfigurationValue(Document xmlDocument, String expression)
            throws XPathExpressionException {
        return AutomationContext.createNamespaceAwareXPath(xmlDocument).compile(expression).evaluate(xmlDocument);
    }

    /**
     * get Node from the configurations
     *
     * @param xmlDocument
     * @param expression
     * @return
     * @throws XPathExpressionException
     */
    private static Node getConfigurationNode(Document xmlDocument, String expression)
            throws XPathExpressionException {
        return (Node) AutomationContext.createNamespaceAwareXPath(xmlDocument).compile(expression).evaluate(xmlDocument, XPathConstants.NODE);
    }

    /**
     * get nodeList from the configurations
     *
     * @param xmlDocument
     * @param expression
     * @return
     * @throws XPathExpressionException
     */
    private static NodeList getConfigurationNodeList(Document xmlDocument, String expression)
            throws XPathExpressionException {
        return (NodeList) AutomationContext.createNamespaceAwareXPath(xmlDocument).compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
    }
}
