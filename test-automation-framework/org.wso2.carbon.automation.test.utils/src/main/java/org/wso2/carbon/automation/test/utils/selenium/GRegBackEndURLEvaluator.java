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
package org.wso2.carbon.automation.test.utils.selenium;

import org.wso2.carbon.automation.engine.context.AutomationContext;

import javax.xml.xpath.XPathExpressionException;

public class GRegBackEndURLEvaluator {
    public String getBackEndURL() throws XPathExpressionException {
        String baseUrl;
        //todo
        AutomationContext gregContext = new AutomationContext("GREG", "", "", "");
        baseUrl = "https://" + gregContext.getInstance().getHosts().get(0);
       /* if (gregContext.getConfigurationValue().getEnvironmentSettings().isEnablePort()
            && gregProperties.getHttpsPort() != null) {
            baseUrl = baseUrl + ":" + gregProperties.getHttpsPort();
        }
        if (properties.getEnvironmentSettings().isEnableCarbonWebContext()
            && gregProperties.getWebContextRoot() != null) {
            baseUrl = baseUrl + "/" + gregProperties.getWebContextRoot();
        }
*/
        return gregContext.getContextUrls().getBackEndUrl().replace("service", "carbon");
//        return baseUrl + "/" + "carbon" + "/";
    }
}
