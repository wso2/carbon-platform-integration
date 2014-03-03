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

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.automation.core.utils.endpointutils.EsbEndpointSetter;
import org.wso2.carbon.automation.utils.dss.DataServiceBaseTest;
import org.wso2.carbon.automation.utils.esb.ESBBaseTest;
import org.wso2.carbon.automation.utils.httpclient.HttpURLConnectionClient;

import javax.activation.DataHandler;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * Implement the scenario described at
 * http://wso2.org/library/articles/2012/10/implementing-restful-services-wso2-esb
 */
public class RestFullApiTestCase {
    private static final Log log = LogFactory.getLog(RestFullApiTestCase.class);
    private DataServiceBaseTest dataServiceBaseTest;
    private ESBBaseTest esbBaseTest;
    private URL url;

    /**
     * Initialize test environment by creating instances of DataServiceBaseTest and ESBBaseTest classes, these classes
     * provide required utility functions such as upload data service, update and revert synapse configuration etc..
     * <p/>
     * And resource URI is constructed by getting service URL of esb instance in esbBaseTest.
     *
     * @throws Exception - if initialization fails
     */
    @BeforeClass(groups = "wso2.esb", alwaysRun = true, description = "initialize test environment")
    public void testInitialize() throws Exception {
        esbBaseTest = new ESBBaseTest();
        dataServiceBaseTest = new DataServiceBaseTest();
        url = new URL(esbBaseTest.esbServer.getServiceUrl().
                substring(0, esbBaseTest.esbServer.getServiceUrl().lastIndexOf("/")) + "/students/003");
    }

    /**
     * Execute student.sql on mysql instance which specified in automation.properties file. Then deploy the data service
     * located at DSS artifact repository.
     *
     * @throws Exception - if data service deployment fails
     */
    @Test(groups = "wso2.esb", description = "deploy student data service")
    public void testDeployStudentService() throws Exception {
        String serviceName = "StudentService";
        List<File> sqlFileLis = new ArrayList<File>();
        sqlFileLis.add(dataServiceBaseTest.selectSqlFile("student.sql"));
        dataServiceBaseTest.deployService(
                serviceName, dataServiceBaseTest.
                createArtifact(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts"
                               + File.separator + "DSS" + File.separator + "dbs" + File.separator
                               + "rdbms" + File.separator + "MySql" + File.separator
                               + "StudentService.dbs", sqlFileLis));
        log.info(serviceName + " uploaded");
    }

    /**
     * updateESBConfiguration(OMElement synapseConfigOM)
     * will deployed the artifact defined in the synapse configuration (proxy, endpoints, sequence, message store, etc..)
     * using admin services API. Framework will go through the configuration and deployed each synapse artifact separately.
     * If synapse artifact with same name exists in the system, it is deleted delete existing one and redeploy new one.
     *
     * @throws Exception - if update of synapse configuration fails.
     */
    @Test(groups = "wso2.esb", description = "update synapse config", dependsOnMethods = "testDeployStudentService")
    public void testUpdateSynapseConfig() throws Exception {
        String synapseConfigPath = ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + File.separator +
                                   "artifacts" + File.separator + "ESB" + File.separator + "synapseconfig" +
                                   File.separator + "config67" + File.separator + "synapse.xml";

        EsbEndpointSetter esbEndpointSetter = new EsbEndpointSetter();
        OMElement synapseConfigOM =
                esbEndpointSetter.setEndpointURL(new DataHandler(new URL("file://" + synapseConfigPath)));
        esbBaseTest.updateESBConfiguration(synapseConfigOM);
    }

    /**
     * Add new student by sending POST request to student resource.
     *
     * @throws Exception - if POST request fails.
     */
    @Test(groups = "wso2.esb", dependsOnMethods = "testUpdateSynapseConfig", description = "Add new student")
    public void testAddNewStudent() throws Exception {
        String addPayload = "<p:Student xmlns:p=\"http://ws.wso2.org/dataservice\">\n" +
                            "      <p:name>tharindu</p:name>\n" +
                            "      <p:email>tharindu@gmail.com</p:email>\n" +
                            "      <p:age>16</p:age>\n" +
                            "      <p:class>8B</p:class>\n" +
                            "      <p:average>83.45</p:average>\n" +
                            "</p:Student>";

        Reader data = new StringReader(addPayload);
        Writer writer = new StringWriter();
        HttpURLConnectionClient.sendPostRequest(data, url, writer, "application/xml");
    }

    /**
     * Check whether the newly added student is exits by sending GET request.
     *
     * @throws Exception - if GET request fails.
     */
    @Test(groups = "wso2.esb", dependsOnMethods = "testAddNewStudent", description = "get newly added student")
    public void testGetStudent() throws Exception {
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(url.toString(), null);
        assertTrue(response.getData().contains("<Student xmlns=\"http://ws.wso2.org/dataservice\">" +
                                               "<RegistrationNumber>003</RegistrationNumber>" +
                                               "<Name>tharindu</Name><Email>tharindu@gmail.com</Email>" +
                                               "<Age>16</Age><Class>8B</Class><Average>83.45</Average>" +
                                               "</Student>"), "new student has not been added");
    }

    /**
     * Update the student by sending PUT request to student resource. Then verify the whether the student is updatd by
     * sending GET request to student resource.
     *
     * @throws Exception - if PUT request fails.
     */
    @Test(groups = "wso2.esb", dependsOnMethods = "testGetStudent", description = "update student")
    public void testUpdateStudent() throws Exception {
        String updatePayload = "<p:Student xmlns:p=\"http://ws.wso2.org/dataservice\">\n" +
                               "      <p:name>amila</p:name>\n" +
                               "      <p:email>amila@wso2.com</p:email>\n" +
                               "      <p:age>16</p:age>\n" +
                               "      <p:class>8A</p:class>\n" +
                               "      <p:average>67.89</p:average>\n" +
                               "</p:Student>";

        Reader data = new StringReader(updatePayload);

        Writer writer = new StringWriter();
        HttpURLConnectionClient.sendPutRequest(data, url, writer, "application/xml");
        System.out.println(writer.toString());

        HttpResponse response = HttpURLConnectionClient.sendGetRequest(url.toString(), null);
        assertTrue(response.getData().contains("<Student xmlns=\"http://ws.wso2.org/dataservice\">" +
                                               "<RegistrationNumber>003</RegistrationNumber>" +
                                               "<Name>amila</Name><Email>amila@wso2.com</Email>" +
                                               "<Age>16</Age><Class>8A</Class><Average>67.89</Average>" +
                                               "</Student>"), "new student has not been updated");
    }

    /**
     * Delete the student by sending DELETE request to student resource. Then verify the whether the student is deleted by
     * sending GET request to student resource.
     *
     * @throws Exception - if DELETE request fails.
     */
    @Test(groups = "wso2.esb", dependsOnMethods = "testUpdateStudent",
          description = "delete student and try to get the student again")
    public void testDeleteStudent() throws Exception {
        HttpURLConnectionClient.sendDeleteRequest(url, null);
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(url.toString(), null);
        assertTrue(response.getData().contains("<Students xmlns=\"http://ws.wso2.org/dataservice\"/>"),
                   "new student has not been deleted");
    }

    /**
     * Undeploy data service after the executing all test methods.
     * cleanup() will remove all deployed synapse artifacts.
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void deleteService() throws Exception {
        dataServiceBaseTest.deleteService("StudentService");
        esbBaseTest.cleanup();
    }
}
