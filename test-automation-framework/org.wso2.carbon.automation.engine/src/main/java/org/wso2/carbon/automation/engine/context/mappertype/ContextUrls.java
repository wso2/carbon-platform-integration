
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
package org.wso2.carbon.automation.engine.context.mappertype;



public class ContextUrls {
    private String backEndUrl;
    private String serviceUrl;
    private String secureServiceUrl;
    private String webAppURL;

    public String getBackEndUrl() {
        return backEndUrl;
    }

    public void setBackEndUrl(String backendUrl) {
        this.backEndUrl = backendUrl;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getSecureServiceUrl() {
        return secureServiceUrl;
    }

    public void setSecureServiceUrl(String secureServiceUrl) {
        this.secureServiceUrl = secureServiceUrl;
    }

    public String getWebAppURL() {
        return webAppURL;
    }

    public void setWebAppURL(String webAppURL) {
        this.webAppURL = webAppURL;
    }
}
