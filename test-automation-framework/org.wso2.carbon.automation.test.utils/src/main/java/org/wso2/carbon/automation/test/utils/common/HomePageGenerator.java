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
package org.wso2.carbon.automation.test.utils.common;

import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.test.utils.AutomationContextXPathConstants;

import javax.xml.xpath.XPathExpressionException;

public class HomePageGenerator {
    public static String getProductHomeURL(AutomationContext automationContext) throws XPathExpressionException {
        String indexURL;
        String webContextRoot = null;
        String hostName;
        String httpsPort = null;
        boolean webContextEnabled = false;
        boolean portEnabled = false;
        if (automationContext.getConfigurationNodeList
                (String.format(AutomationContextXPathConstants.WEB_CONTEXT_ENABLED)).equals("true")) {
            webContextEnabled = true;
            webContextRoot = automationContext.getConfigurationNodeList
                    (String.format(AutomationContextXPathConstants.WEB_CONTEXT_ROOT)).toString();
        }
        if (automationContext.getInstance().getPorts().size() != 0) {
            portEnabled = true;
            httpsPort = automationContext.getInstance().getPorts().get("https");
        }
        hostName = automationContext.getInstance().getHosts().get("default");
        if (portEnabled && webContextEnabled) {
            if (webContextRoot != null && httpsPort != null) {
                indexURL = "https://" + hostName + ":" + httpsPort + "/" + webContextRoot + "/" +
                        "carbon";
            } else if (webContextRoot == null && httpsPort != null) {
                indexURL = "https://" + hostName + ":" + httpsPort + "/" + "carbon";
            } else if (webContextRoot == null) {
                indexURL = "https://" + hostName + "/" + "carbon";
            } else {
                indexURL = "https://" + hostName + "/" + webContextRoot + "/" + "carbon";
            }
        } else if (!portEnabled && webContextEnabled) {
            indexURL = "https://" + hostName + "/" + webContextRoot + "/" + "carbon";
        } else if (portEnabled && !webContextEnabled) {
            indexURL = "https://" + hostName + ":" + httpsPort + "/" + "carbon";
        } else {
            indexURL = "https://" + hostName + "/" + "carbon";
        }
        return indexURL;
    }
}
