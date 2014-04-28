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
package org.wso2.carbon.automation.engine.frameworkutils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.context.AutomationContext;

import javax.xml.xpath.XPathExpressionException;

/**
 * This class contain util methods which can be used inside test framework and test cases
 */
public class TestFrameworkUtils {
    private static final Log log = LogFactory.getLog(ArchiveExtractorUtil.class);

    /**
     * Set SSL properties,trust store files should be available at the patch returned by
     * FrameworkPathUtil.getSystemResourceLocation()
     *
     * @param context - Automation Context
     * @throws XPathExpressionException - Throws if xpath cannot be read
     */
    public static void setKeyStoreProperties(AutomationContext context) throws XPathExpressionException {

        System.setProperty("javax.net.ssl.trustStore", FrameworkPathUtil.getSystemResourceLocation()
                + context.getConfigurationValue("//keystore/fileName/text()"));
        System.setProperty("javax.net.ssl.trustStorePassword",
                context.getConfigurationValue("//keystore/keyPassword/text()"));
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");

        if (log.isDebugEnabled()) {
            log.debug("javax.net.ssl.trustStore :" + System.getProperty("javax.net.ssl.trustStore"));
            log.debug("javax.net.ssl.trustStorePassword :" + System.getProperty("javax.net.ssl.trustStorePassword"));
            log.debug("javax.net.ssl.trustStoreType :" + System.getProperty("javax.net.ssl.trustStoreType"));
        }
    }


}
