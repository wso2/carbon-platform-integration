package org.wso2.carbon.automation.engine.configurations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.automation.engine.context.AutomationContext;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringWriter;
import java.util.Iterator;

public class AutomationConfiguration {
    static final Log log = LogFactory.getLog(AutomationConfiguration.class);
    private static Document configurationDocument;

    static {
        AutomationConfigurationReader configurationReader = new AutomationConfigurationReader();
        try {
            configurationReader.readAutomationConfigurations();
            configurationDocument = configurationReader.getConfigurationXmlDocument();
        } catch (Exception e) {
            log.error("Error While reading configurations ",  e);
            throw new IllegalArgumentException("Error While reading configurations" ,
                                               e);
        }
    }

    public static Document getConfigurationDocument() {
        return configurationDocument;
    }

	public static void setConfigurationDocument(Document configurationDocument) {
		AutomationConfiguration.configurationDocument = configurationDocument;
	}

    public static String getConfigurationValue(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();

        return AutomationContext.createNamespaceAwareXPath(xmlDocument).compile(expression).evaluate(xmlDocument);
    }

    public static Node getConfigurationNode(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();
        return (Node) AutomationContext.createNamespaceAwareXPath(xmlDocument).compile(expression).evaluate(xmlDocument, XPathConstants.NODE);
    }

    public static NodeList getConfigurationNodeList(String expression) throws XPathExpressionException {
        Document xmlDocument = AutomationConfiguration.getConfigurationDocument();

        log.warn("FFFF expression : " + expression);

        try {
            DOMSource domSource = new DOMSource(xmlDocument);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter sw = new StringWriter();
            StreamResult sr = new StreamResult(sw);
            transformer.transform(domSource, sr);

            log.warn("FFFF xmlDocument : " + sw.toString());
        } catch (TransformerConfigurationException e) {
            log.error("TransformerConfigurationException" , e);
        } catch (TransformerException e) {
            log.error("TransformerException" , e);
        }


//        return (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        return (NodeList) AutomationContext.createNamespaceAwareXPath(xmlDocument).evaluate(expression, xmlDocument,
                XPathConstants
                .NODESET);
    }

}
