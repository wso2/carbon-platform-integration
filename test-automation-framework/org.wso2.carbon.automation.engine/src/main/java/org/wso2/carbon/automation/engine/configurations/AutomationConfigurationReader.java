/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.wso2.carbon.automation.engine.exceptions.ConfigurationMismatchException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;

/**
 * read automation configuration from automation.xml
 */
public class AutomationConfigurationReader {
    private static final Log log = LogFactory.getLog(AutomationConfigurationReader.class);
    private static AutomationConfigurationReader sessionAutomationConfiguration;
    private static Document document;

    /**
     * read automation configuration from xml file
     * @return Automation configuration reader object
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws IOException
     */

    public AutomationConfigurationReader readAutomationConfigurations()
            throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        synchronized (AutomationConfigurationReader.class) {
            if (sessionAutomationConfiguration == null) {
                sessionAutomationConfiguration = new AutomationConfigurationReader();
                document = readConfigurationXmlDocument();
            }
        }
        return sessionAutomationConfiguration;
    }

    /**
     * get configuration as a document
     * @return document object
     */
	public Document getConfigurationDocument() {
		return document;
	}

    private static Document readConfigurationXmlDocument()
            throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
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
        return xmlDocument;
    }

    /*When we remove comment nodes what actually happens is insert empty nodes instead of that
    this method removes all the empty(text) nodes*/
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

    protected static Document getConfigurationXmlDocument() throws ConfigurationMismatchException,
                                                                   XPathExpressionException {
        //check for semantics errors in configuration file
        ConfigurationErrorChecker.checkPlatformErrors(document);
        return document;
    }
}
