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
package org.wso2.carbon.automation.test.utils.axis2client;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

import java.io.File;

public class ConfigurationContextProvider {
    private static final Log log = LogFactory.getLog(ConfigurationContextProvider.class);
    private static ConfigurationContext configurationContext = null;
    private static ConfigurationContextProvider instance = new ConfigurationContextProvider();

    private ConfigurationContextProvider() {
        try {
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
            configurationContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(
                    FrameworkPathUtil.getSystemResourceLocation() + File.separator + "client", null);

            poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();

            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(25);
            poolingHttpClientConnectionManager.setMaxTotal(5);

            CloseableHttpClient client =
                    HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();

            configurationContext.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, client);
            configurationContext.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Constants.VALUE_TRUE);

        } catch (AxisFault axisFault) {
            log.error("Error while creating axis2 configuration context", axisFault);
        }
    }

    public static ConfigurationContextProvider getInstance() {
        return instance;
    }

    public ConfigurationContext getConfigurationContext() {
        return configurationContext;
    }
}
