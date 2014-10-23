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

package org.wso2.carbon.automation.engine.test.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ConfigurationXSDValidatorTest {
    static final Log log = LogFactory.getLog(ConfigurationXSDValidatorTest.class);

    File validateXsdFile;
    File configXmlFile;

    @BeforeClass
    public void init() {
        configXmlFile = new File(FrameworkPathUtil.
                getSystemResourceLocation() + FrameworkConstants.CONFIGURATION_FILE_NAME);
        validateXsdFile = new File(FrameworkPathUtil.
                getSystemResourceLocation() + "automationXMLSchema.xsd");
    }

    @Test(groups = "context.unit.test", description = "Upload aar service and verify deployment")
    public void validateAutomationXml() throws IOException, SAXException {
        boolean validated=false;
        URL schemaFile = validateXsdFile.toURI().toURL();
        Source xmlFile = new StreamSource(configXmlFile);
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaFile);
        Validator validator = schema.newValidator();
        try {
            validator.validate(xmlFile);
            validated=true;

        } catch (SAXException e) {
            log.error(e.getStackTrace());
            throw new SAXException(e);
        }
        Assert.assertTrue(validated);

    }
}
