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
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.configurations.exceptions.ConfigurationMismatchException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class AutomationConfigurationReader {
    private static final Log log = LogFactory.getLog(AutomationConfigurationReader.class);
    private static AutomationConfigurationReader configurationReaderInstance;
    static Document document;

    public AutomationConfigurationReader readAutomationConfigurations()
            throws SAXException, URISyntaxException, IOException,
            XMLStreamException, ConfigurationMismatchException, ParserConfigurationException {
        synchronized (AutomationConfigurationReader.class) {
            if (configurationReaderInstance == null) {
                configurationReaderInstance = new AutomationConfigurationReader();
                document = readConfigurationXmlDocument();
            }
        }
        return configurationReaderInstance;
    }


    private Document readConfigurationXmlDocument() throws ParserConfigurationException, IOException, SAXException {
        FileInputStream file = new FileInputStream(new File(FrameworkPathUtil.
                getSystemResourceLocation() + FrameworkConstants.CONFIGURATION_FILE_NAME));
        // FileInputStream file = new FileInputStream(new File("/home/dharshana/framework/test-automation-framework/org.wso2.carbon.automation.engine/resources/automation.xml"));
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(file);
        return xmlDocument;
    }

    public Document getConfigurationXmlDocument() {
        return document;
    }
}