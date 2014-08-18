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

package org.wso2.carbon.automation.engine.extensions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.automation.engine.context.AutomationContext;

import javax.xml.xpath.XPathExpressionException;
import java.util.HashMap;
import java.util.Map;

public class ListenerExtension {
    private final Log log = LogFactory.getLog(getClass());
    private Map<String, String> parameterMap;
    private AutomationContext automationContext;
    private final static String PARAMETER = "parameter";
    private final static String VALUE = "value";
    private final static String NAME = "name";

    public ListenerExtension() {
        parameterMap = new HashMap<String, String>();
        try {
            automationContext = new AutomationContext();
        } catch (Exception e) {
            log.warn("Failed to initializing the Extension Class");
            log.error("Error initializing the Automation Context", e);
        }
    }

    protected void setParameterMap(String xpathToClass, String className) throws XPathExpressionException {
        Node extensionClass = automationContext.getConfigurationNode(
                xpathToClass + "[.='" + className + "']")
                .getParentNode();
        NodeList extensionClassChildNodes = extensionClass.getChildNodes();
        if (extensionClassChildNodes != null) {
            for (int i = 0; i < extensionClassChildNodes.getLength(); i++) {
                if (PARAMETER.equalsIgnoreCase(extensionClassChildNodes.item(i).getNodeName())) {
                    NamedNodeMap attributes = extensionClassChildNodes.item(i).getAttributes();

                    parameterMap.put(attributes.getNamedItem(NAME).getTextContent()
                            , attributes.getNamedItem(VALUE).getTextContent());
                }
            }
        }
    }

    protected Map<String, String> getParameterMap() {
        return parameterMap;
    }

    protected AutomationContext getAutomationContext() {
        return automationContext;
    }
}
