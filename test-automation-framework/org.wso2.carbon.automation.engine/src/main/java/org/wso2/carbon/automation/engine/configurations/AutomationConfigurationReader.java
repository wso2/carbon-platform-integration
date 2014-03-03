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
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.xml.sax.SAXException;
import sun.net.www.content.audio.x_aiff;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;

public class AutomationConfigurationReader {
    private static final Log log = LogFactory.getLog(AutomationConfigurationReader.class);
    private static AutomationConfigurationReader configurationReaderInstance;
    static Document document;

    public AutomationConfigurationReader readAutomationConfigurations()
            throws Exception {
        synchronized (AutomationConfigurationReader.class) {
            if (configurationReaderInstance == null) {
                configurationReaderInstance = new AutomationConfigurationReader();
                document = readConfigurationXmlDocument();
            }
        }
        return configurationReaderInstance;
    }

    /**
     * This method remove the given node type recursively
     *
     * @param docObj   Automation node from the automation,xml
     * @param nodeType comment node type
     */
    private static void removeNodes(Node docObj, short nodeType) {
        if (docObj.getNodeType() == nodeType)
            docObj.getParentNode().removeChild(docObj);

        // check the children recursively
        NodeList list = docObj.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            removeNodes(list.item(i), nodeType);
        }
    }

    //this method is used to print the processed xml doc
    public static void printBuiltXML(Document xml) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(xml), new StreamResult(out));

        System.out.println(out.toString().trim());
    }

    private Document readConfigurationXmlDocument() throws Exception {
        File fXmlFile = new File(FrameworkPathUtil.
                getSystemResourceLocation() + FrameworkConstants.CONFIGURATION_FILE_NAME);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        //remove all comments from the content of the automation.xml
        dbFactory.setIgnoringComments(true);
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

    private void removeOtherNodes(String nodeName) throws Exception {

        for (int i = 0; i < document.getChildNodes().getLength(); i++) {
            if (document.getChildNodes().item(i).getNodeName().equals(nodeName)) {
                document.removeChild(document.getChildNodes().item(i));

            }
        }
    }

    public Document getConfigurationXmlDocument() throws ConfigurationMismatchException, XPathExpressionException {
        //check for semantics errors in configuration file
        ConfigurationErrorChecker.checkPlatformErrors(document);
        return document;
    }
}
