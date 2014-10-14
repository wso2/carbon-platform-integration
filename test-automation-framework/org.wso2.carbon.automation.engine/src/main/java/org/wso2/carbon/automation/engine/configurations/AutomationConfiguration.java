package org.wso2.carbon.automation.engine.configurations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class AutomationConfiguration {
    static final Log log = LogFactory.getLog(AutomationConfiguration.class);
    private static Document configurationDocument;

    public static Document getConfigurationDocument() {
        return configurationDocument;
    }

	public static void setConfigurationDocument(Document configurationDocument) {
		AutomationConfiguration.configurationDocument = configurationDocument;
	}

    public static String getConfigurationValue(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();
        XPath xPath = XPathFactory.newInstance().newXPath();
        return xPath.compile(expression).evaluate(xmlDocument);
    }

    public static Node getConfigurationNode(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();
        XPath xPath = XPathFactory.newInstance().newXPath();
        return (Node) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODE);
    }

    public static NodeList getConfigurationNodeList(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();
        XPath xPath = XPathFactory.newInstance().newXPath();
        return (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
    }

}
