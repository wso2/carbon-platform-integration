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

package org.wso2.carbon.automation.platform.scenarios.esb;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.jarservices.JARServiceUploaderClient;
import org.wso2.carbon.automation.api.clients.registry.ResourceAdminServiceClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.endpointutils.EsbEndpointSetter;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentBuilder;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentVariables;
import org.wso2.carbon.automation.utils.as.ASBaseTest;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.utils.esb.ESBBaseTest;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * Implement the scenario described at
 * http://wso2.com/library/articles/2011/01/wso2-esb-by-example-service-chaining
 * Pre-requests : as server should run on port 9445 , esb should run on port 9443
 */
public class CreditPolicyTestCase extends ASBaseTest {

    private static final Log log = LogFactory.getLog(CreditPolicyTestCase.class);
    private ESBBaseTest esbBaseTest;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        esbBaseTest = new ESBBaseTest();
        uploadResourcesToGovernanceRegistry();
    }

    @AfterClass(alwaysRun = true)
    public void jarServiceDelete() throws Exception {

        deleteService("CreditService");
        deleteService("CreditProxy");
        deleteService("CreditInfo");
        deleteService("PersonInfoService");
        deleteService("PersonInfo");
        deleteService("SecureStockQuoteService");
        deleteService("SimpleStockQuoteService");
        log.info("esb-samples-1.0-SNAPSHOT services deleted successfully");
    }

    @Test(groups = "wso2.as", description = "Upload jar service and verify deployment")
    public void asServerJarServiceUpload() throws Exception {

        JARServiceUploaderClient jarServiceUploaderClient =
                new JARServiceUploaderClient(asServer.getBackEndUrl(),
                        asServer.getSessionCookie());
        List<DataHandler> jarList = new ArrayList<DataHandler>();
        URL url = new URL("file://" + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                "artifacts" + File.separator + "AS" + File.separator + "jar" + File.separator +
                "artifact5" + File.separator + "esb-samples-1.0-SNAPSHOT.jar");
        DataHandler dh = new DataHandler(url);
        jarList.add(dh);

        jarServiceUploaderClient.uploadJARServiceFile("", jarList, dh);

        isServiceDeployed("CreditService");
        isServiceDeployed("PersonInfoService");
        log.info("esb-samples-1.0-SNAPSHOT.jar uploaded successfully");
    }

    @Test(groups = "wso2.esb", description = "update synapse config", dependsOnMethods = "asServerJarServiceUpload")
    public void testUpdateSynapseConfig() throws Exception {

        String synapseConfigPath = ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + File.separator +
                "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" +
                File.separator + "servicechaining" + File.separator + "synapse.xml";

        EsbEndpointSetter esbEndpointSetter = new EsbEndpointSetter();
        OMElement synapseConfigOM =
                esbEndpointSetter.setEndpointURL(new DataHandler(new URL("file://" + synapseConfigPath)));
        esbBaseTest.updateESBConfiguration(synapseConfigOM);
    }


    @Test(groups = "wso2.as", description = "invoke credit proxy service", dependsOnMethods = "testUpdateSynapseConfig")
    public void invokeService() throws Exception {

        AxisServiceClient axisServiceClient = new AxisServiceClient();

        String endpoint = esbBaseTest.getBackEndServiceUrl("CreditProxy");
        OMElement response = axisServiceClient.sendReceive(createPayLoad(), endpoint, "credit");
        log.info("Response : " + response);
        assertTrue(response.toString().contains("<ns:return>true</ns:return>"));
    }


    private void uploadResourcesToGovernanceRegistry() throws Exception {  // uploads personToCredit.xslt to esb governance
        EnvironmentBuilder builder = new EnvironmentBuilder().esb(Integer.parseInt(userInfo.getUserId()));
        EnvironmentVariables esbServer = builder.build().getEsb();
        ResourceAdminServiceClient resourceAdminServiceStub =
                new ResourceAdminServiceClient(esbServer.getBackEndUrl(), esbServer.getSessionCookie());

        resourceAdminServiceStub.deleteResource("/_system/governance/xslt");
        resourceAdminServiceStub.addCollection("/_system/governance/", "xslt", "",
                "Needed xslt files for credit policy");

        URL url = new URL("file://" + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" + File.separator + "servicechaining"
                + File.separator + "personToCredit.xslt");
        DataHandler dh = new DataHandler(url);   // creation of data handler .

        assertTrue(resourceAdminServiceStub.addResource(
                "/_system/governance/xslt/personToCredit.xslt", "application/xml", "Needed xslt files for credit policy", dh),
                "PersonToCredit.xslt file upload to /_system/governance/xslt/ failed");
    }

    private static OMElement createPayLoad() {    // creation of payload
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://samples.esb.wso2.org", "ns");
        OMElement getOme = fac.createOMElement("credit", omNs);

        OMElement getID = fac.createOMElement("id", omNs);
        OMElement getAmount = fac.createOMElement("amount", omNs);
        getID.setText("100");
        getAmount.setText("200");

        getOme.addChild(getID);
        getOme.addChild(getAmount);

        return getOme;
    }
}

