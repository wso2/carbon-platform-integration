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

package org.wso2.carbon.automation.platform.scenarios.as;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.api.clients.aar.services.AARServiceUploaderClient;
import org.wso2.carbon.automation.api.clients.webapp.mgt.WebAppAdminClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.automation.core.utils.UserListCsvReader;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentBuilder;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentVariables;
import org.wso2.carbon.automation.utils.as.WebApplicationDeploymentUtil;
import org.wso2.carbon.automation.utils.esb.ESBBaseTest;
import org.wso2.carbon.automation.utils.httpclient.HttpClientUtil;
import org.wso2.carbon.automation.utils.services.ServiceDeploymentUtil;

import java.io.File;
import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class WebAppProxyTestCase {

    private static final Log log = LogFactory.getLog(WebAppProxyTestCase.class);
    private EnvironmentVariables environmentAS;
    private WebAppAdminClient webAppAdminClient;
    private ESBBaseTest esbSBaseTest;
    private final String webAppFileName = "appServer-valied-deploymant-1.0.0.war";
    private final String webAppName = "appServer-valied-deploymant-1.0.0";

    @BeforeClass(alwaysRun = true)
    public void testInitialize() throws Exception, RemoteException {
        int userId = 2;
        esbSBaseTest = new ESBBaseTest();
        esbSBaseTest.userInfo = UserListCsvReader.getUserInfo(userId);
        EnvironmentBuilder builder = new EnvironmentBuilder().as(userId).esb(userId);
        environmentAS = builder.build().getAs();

    }

    @Test(groups = {"wso2.as"}, description = "upload webapp file")
    public void testUploadWebAPP() throws Exception {
        webAppAdminClient = new WebAppAdminClient(environmentAS.getBackEndUrl(),
                                                  environmentAS.getSessionCookie());

        webAppAdminClient.warFileUplaoder(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                                          "artifacts" + File.separator + "AS" + File.separator + "war"
                                          + File.separator + webAppFileName);

        assertTrue(WebApplicationDeploymentUtil.isWebApplicationDeployed(
                environmentAS.getBackEndUrl(), environmentAS.getSessionCookie(), webAppName)
                , "Web Application Deployment failed");

    }

    @Test(groups = "wso2.as", description = "Invoke web application",
          dependsOnMethods = "testUploadWebAPP")
    public void testInvokeWebApp() throws Exception {
        String webAppURL = environmentAS.getWebAppURL() + "/appServer-valied-deploymant-1.0.0";
        HttpClientUtil client = new HttpClientUtil();
        OMElement omElement = client.get(webAppURL);
        assertEquals(omElement.toString(), "<status>success</status>", "Web app invocation fail");
    }

    @Test(groups = "wso2.as", description = "UnDeploying web application",
          dependsOnMethods = "testInvokeWebApp")
    public void testDeleteWebApplication() throws Exception {
        webAppAdminClient.deleteWebAppFile(webAppFileName);
        assertTrue(WebApplicationDeploymentUtil.isWebApplicationUnDeployed(
                environmentAS.getBackEndUrl(), environmentAS.getSessionCookie(), webAppName),
                   "Web Application unDeployment failed");

        String webAppURL = environmentAS.getWebAppURL() + "/appServer-valied-deploymant-1.0.0";
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL, null);
        Assert.assertEquals(response.getResponseCode(), 302, "Response code mismatch. Client request " +
                                                             "got a response even after web app is undeployed");

    }

    @Test(groups = "wso2.as", description = "upload aar file",
          dependsOnMethods = "testDeleteWebApplication")
    public void testUploadAAR() throws Exception {
        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(environmentAS.getBackEndUrl(),
                                               environmentAS.getSessionCookie());

        aarServiceUploaderClient.uploadAARFile("SimpleStockQuoteService.aar",
                                               ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                                               File.separator + "AS" + File.separator + "aar" + File.separator +
                                               "SimpleStockQuoteService.aar", "");

        ServiceDeploymentUtil.isServiceDeployed(environmentAS.getBackEndUrl(),
                                                environmentAS.getSessionCookie(), "SimpleStockQuoteService");
    }

    @Test(groups = "wso2.as", description = "deploy proxy configuration",
          dependsOnMethods = "testUploadAAR")
    public void testUploadProxyConfiguration() throws Exception {
        esbSBaseTest.loadESBConfigurationFromClasspath("/artifacts/ESB/proxyconfig/proxy/customProxy/simple_proxy.xml");
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        //Send three malformed requests
        for (int i = 0; i < 3; i++) {
            try {
                //tests a url with a space
                httpClientUtil.get(esbSBaseTest.getProxyServiceURL("simpleProxy").replace("services", " services") + "?WSO2");
            } catch (Exception e) {
            }
        }
        //check whether ESB is still stable by sending a correct request
        OMElement response =
                esbSBaseTest.axis2Client.sendSimpleQuoteRequest(esbSBaseTest.getProxyServiceURL("simpleProxy"), null,
                                                                "WSO2");
        Assert.assertTrue(response.toString().contains("WSO2"));
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        esbSBaseTest.cleanup();
    }

}
