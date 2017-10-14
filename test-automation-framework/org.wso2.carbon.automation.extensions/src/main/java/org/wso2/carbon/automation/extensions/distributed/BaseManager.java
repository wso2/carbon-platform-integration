/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.automation.extensions.distributed;

import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wink.client.ClientResponse;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.distributed.beans.As0001;
import org.wso2.carbon.automation.extensions.distributed.beans.Esb0001;
import org.wso2.carbon.automation.extensions.distributed.beans.Mysql0001;
import org.wso2.carbon.automation.extensions.distributed.beans.YamlBean;
import org.wso2.carbon.automation.extensions.distributed.util.DataBaseManager;
import org.wso2.carbon.automation.extensions.distributed.util.GitOperations;
import org.wso2.carbon.automation.extensions.distributed.util.PropertyFileReader;
import org.wso2.carbon.automation.test.utils.wink.client.GenericRestClient;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.testng.Assert.assertNotNull;

/**
 * Before Running This Code Please be Ensure The Following
 * <p/>
 * 1. Run & check whether you have already running docker process
 * ps -ef|grep docker
 * 2. If so stop it
 * sudo docker stop / service docker stop
 * 3. Start docker daemon on port 2375
 * sudo docker daemon -H tcp://127.0.0.1:2375
 * 4. If you want to verify docker images
 * export DOCKER_HOST="tcp://127.0.0.1:2375"
 * 5. To retrieve docker run product url ( eg: 31cc6d932893 is the containerID (type docker ps to find so)
 * docker inspect --format '{{ .NetworkSettings.IPAddress }}' 31cc6d932893
 * <p/>
 * info :
 * https://docs.docker.com/engine/reference/api/remote_api_client_libraries/
 * <p/>
 * for error
 * Caused by: org.eclipse.jgit.errors.TransportException: : cannot open git-upload-pack
 * <p/>
 * solution
 * sudo git config --global http.sslVerify false
 * <p/>
 * <p/>
 * Following are the step sequence
 * 1. Building mysql-container
 * 2. Running mysql docker container
 * 3. Performing following git cloning ops.
 * 3.1 - DockerFile
 * 3.2 - Puppet module
 * 4. Copy product pack
 * 5. Copy mysql connector jar
 * 6. Copy yaml files (defult.yaml)
 * 7. Perform DB Ops.
 * 7.1 - Connecting to DB
 * 7.2 - Creating Databases
 * 7.3 - Execute create tables DB script
 * 8. Build Wso2 base
 * 9. Building wso2as container
 * 10.Running wso2as container ( access mgt console : https://172.17.0.3:9443/carbon/admin/login.jsp)
 */

public class BaseManager {

    private static final Log log = LogFactory.getLog(BaseManager.class);

    private GenericRestClient genericRestClient;
    private PropertyFileReader propertyFileReader = new PropertyFileReader();
    private HashMap<String, Object> yamlHierarchy;
    private YamlBean yamlBean;
    private As0001 as = null;
    private Esb0001 esb = null;
    private Mysql0001 mysql = null;
    private String resourceLocation = FrameworkPathUtil.getSystemResourceLocation();
    private String yamlFileDestinationDir = resourceLocation +"temp" + File.separator + "puppet-module"
            + File.separator + "hieradata" + File.separator + "dev" + File.separator + "wso2";
    private String mysqlContainerIP;
    private static JSONParser parser = new JSONParser();
    private static Map<String, String> queryParamMap = new HashMap<String, String>();
    private static Map<String, String> headerMap = new HashMap<String, String>();

    public String getAsContainerIP() {
        return asContainerIP;
    }

    public void setAsContainerIP(String asContainerIP) {
        this.asContainerIP = asContainerIP;
    }

    private String asContainerIP;

    public String getAsContainerPort() {
        return asContainerPort;
    }

    public void setAsContainerPort(String asContainerPort) {
        this.asContainerPort = asContainerPort;
    }

    private String asContainerPort;


    public ArrayList<String> getDockerContainerList() {
        return dockerContainerList;
    }

    public void setDockerContainerList(ArrayList<String> dockerContainerList) {
        this.dockerContainerList = dockerContainerList;
    }

    private ArrayList<String> dockerContainerList = new ArrayList<String>();

    public BaseManager() throws AutomationFrameworkException, IOException {

        genericRestClient = new GenericRestClient();
        propertyFileReader.readPropertyFile();

        GitOperations gitOperations = new GitOperations();

        // git clone - dockerfile repo
        log.info("Performing git clone - dockerfile");

        try {

            gitOperations.gitRepoClone(PropertyFileReader.dockerFilesGitRepo, resourceLocation
                    + File.separator + "temp" + File.separator + "dockerfiles");
        } catch (IOException | GitAPIException e) {
            throw new AutomationFrameworkException("Docker files git clone failed.", e);
        }

        // git clone - puppetmodule repo
        log.info("Performing git clone - puppetmodule");

        try {
            gitOperations.gitRepoClone(PropertyFileReader.puppetModuleGitRepo, resourceLocation
                    + File.separator + "temp" + File.separator + "puppet-module");
        } catch (IOException | GitAPIException e) {
            throw new AutomationFrameworkException("puppet-modules git clone failed.", e);
        }

        yamlBean = new YamlBean();
    }

    /**
     *
     */
    public void dockerContainerRunner() throws IOException, ParseException, JSONException {

        // copy relevant wso2 product distributions
        copyProductDistributions();



        // build wso2-as
        buildDockerFile(PropertyFileReader.dockerUrl, resourceLocation + File.separator
                        + "temp" + File.separator + "dockerfiles" + File.separator + "wso2as" + File.separator
                        + "Dockerfile", "wso2/as:1.0.0"
        );

        //run wso2as image
        JSONObject jsonObjectAS = runDockerImage(resourceLocation + File.separator + "json" + File.separator
                        + "wso2ascreatecontainer.json", resourceLocation + File.separator + "json" + File.separator
                        + "wso2asstartcontainer.json"
        );

        ClientResponse responseInspectContainerAS = inspectContainer(jsonObjectAS);
        Object jsonInspectObjAS = new JSONObject(responseInspectContainerAS.getEntity(String.class)).get("NetworkSettings");
        asContainerIP = ((JSONObject) jsonInspectObjAS).get("IPAddress").toString();
        asContainerPort = ((JSONObject) jsonInspectObjAS).get("Ports").toString();

        dockerContainerList.add(asContainerIP);

        setEnvironmentVariables();

    }

    public void setEnvironmentVariables () {

        ProcessBuilder pb = new ProcessBuilder("/bin/sh"); // or any other program you want to run

        Map<String, String> envMap = pb.environment();

        envMap.put("asContainerIP", asContainerIP);
        envMap.put("asContainerPort", asContainerPort);
    }

    /**
     * @param dockerURL          - docker daemon url
     * @param dockerFileLocation - docker image file location
     * @param repositoryName     - repo name
     */
    public void buildDockerFile(String dockerURL, String dockerFileLocation, String repositoryName) {

        // Building docker image
        Config config = new ConfigBuilder()
                .withDockerUrl(dockerURL)
                .build();

        DockerClient clientOne = new DefaultDockerClient(config);

        clientOne.image().build().withRepositoryName(repositoryName).usingListener(new EventListener() {
            @Override
            public void onSuccess(String message) {
                //return
                log.info("Docker File Build Success: " + message);
            }

            @Override
            public void onError(String errMessage) {
                log.info("Docker File Build Failure: " + errMessage);
            }

            @Override
            public void onEvent(String eventMessage) {
                log.info("Docker Event " + eventMessage);
            }
        }).fromFolder(dockerFileLocation);

    }

    /**
     * Running of docker image
     *
     * @param createContainerFilePath - file location contains create container request information
     * @param startContainerFilePath  - file location contains start container request information
     * @return Json object of container info
     * @throws java.io.IOException
     * @throws ParseException
     * @throws org.json.JSONException
     */
    public JSONObject runDockerImage(String createContainerFilePath, String startContainerFilePath) throws IOException,
            ParseException, JSONException {

        // create container
        Object createObject = parser.parse(new FileReader(createContainerFilePath));

        String type = "restservice";
        queryParamMap.put("type", type);

        ClientResponse responseCreateContainer =
                genericRestClient.geneticRestRequestPost("http://localhost:2375/containers/create",
                        MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
                        createObject.toString(), queryParamMap, headerMap, null);

        JSONObject jsonObj = new JSONObject(responseCreateContainer.getEntity(String.class));

        assertNotNull(jsonObj.get("Id"), "ID should not be null");

        // start container
        Object startObject = parser.parse(new FileReader(startContainerFilePath));

        ClientResponse responseStartContainer =
                genericRestClient.geneticRestRequestPost(PropertyFileReader.dockerUrl + "/containers/" +
                                jsonObj.get("Id").toString() + "/start", MediaType.APPLICATION_JSON,
                        MediaType.APPLICATION_JSON, startObject.toString(), queryParamMap, headerMap, null
                );

        assertNotNull(responseStartContainer);

        return jsonObj;
    }

    /**
     * Returns docker container related information
     *
     * @param jsonObj - json object contains params for starting container
     * @return container information
     * @throws org.json.JSONException
     */
    public ClientResponse inspectContainer(JSONObject jsonObj) throws JSONException {
        // inspect container
        ClientResponse responseInspectContainer =
                genericRestClient.geneticRestRequestGet(PropertyFileReader.dockerUrl+ "/containers/" + jsonObj.get("Id").toString() + "/json",
                        queryParamMap, headerMap, null);

        return responseInspectContainer;

    }


    /**
     * File operation handler utility
     *
     * @param sourceLocation      - Source directory location
     * @param destinationLocation - Destination directory location
     * @throws java.io.IOException
     */
    public static void copyFiles(String sourceLocation, String destinationLocation) throws IOException {
        FileUtils.copyFile(new File(sourceLocation), new File(destinationLocation));
    }

    /**
     * File remover handler utility
     *
     * @param sourceLocation -  Source directory location
     * @throws java.io.IOException
     */
    public static void removeFiles(String sourceLocation) throws IOException {
        FileUtils.forceDelete(new File(sourceLocation));
    }

    /**
     * Restarts the container
     *
     * @param jsonObj - object of container info
     * @throws org.json.JSONException
     */
    public void restartContainer(JSONObject jsonObj) throws JSONException {
        ClientResponse responseInspectContainer =
                genericRestClient.geneticRestRequestPost(PropertyFileReader.dockerUrl + "/containers/" +
                                jsonObj.get("Id").toString() + "/start", MediaType.APPLICATION_JSON,
                        MediaType.APPLICATION_JSON, "t=5", queryParamMap, headerMap, null
                );
    }

    /**
     * This method stops the container
     * @param containerIP - Container IP need to stop
     * @throws org.json.JSONException
     */
    public void stopContainer(String containerIP) throws JSONException {
        ClientResponse responseInspectContainer =
                genericRestClient.geneticRestRequestPost(PropertyFileReader.dockerUrl + "/containers/" +
                                containerIP + "/stop", MediaType.APPLICATION_JSON,
                        MediaType.APPLICATION_JSON, "t=5", queryParamMap, headerMap, null
                );
    }

    public void copyProductDistributions() {

        // copy product pack
        log.info("Copying wso2 distributions to relevant puppet-modules");
        try {

            yamlHierarchy = yamlBean.getYamlHierarchy();
            Iterator itYaml = yamlHierarchy.entrySet().iterator();

            String distributionName;

            while (itYaml.hasNext()) {
                Map.Entry entry = (Map.Entry) itYaml.next();
                if (entry.getKey().toString().contains("As0001")) {
                    as = (As0001) entry.getValue();
                    if (!as.isThirdParty()) {
                        distributionName = as.getDistributionName();
                        BaseManager.copyFiles(resourceLocation + "productpack" + File.separator + distributionName,
                                resourceLocation + "temp" + File.separator + "puppet-module"
                                        + File.separator + "modules" + File.separator + distributionName.split("-")[0]
                                        + File.separator + "files" + File.separator + distributionName
                        );

                        // copy yaml files
                        BaseManager.copyFiles(resourceLocation + File.separator + "yaml" + File.separator + "as" + File.separator + "default.yaml",
                                yamlFileDestinationDir + File.separator + distributionName.split("-")[0] + File.separator  + as.getVersion() + "/default.yaml"
                        );


                    }
                } else if (entry.getKey().toString().contains("Esb0001")) {
                    esb = (Esb0001) entry.getValue();
                    if (!esb.isThirdParty()) {
                        distributionName = esb.getDistributionName();
                        BaseManager.copyFiles(resourceLocation + "productpack" + File.separator + distributionName,
                                resourceLocation + "temp" + File.separator + "puppet-module"
                                        + File.separator + "modules" + File.separator + distributionName.split("-")[0]
                                        + File.separator + "files" + File.separator + distributionName
                        );

                        // copy yaml files
                        BaseManager.copyFiles(resourceLocation + File.separator + "yaml" + File.separator + "esb" + File.separator + "default.yaml",
                                yamlFileDestinationDir + File.separator + distributionName.split("-")[0] + "/" + esb.getVersion() + "/default.yaml"
                        );
                    }
                } else if (entry.getKey().toString().contains("Mysql0001")) {
                    mysql = (Mysql0001) entry.getValue();

                    // copy connector jar
                    BaseManager.copyFiles(mysql.getConnectorLocation(),
                            resourceLocation + "temp" + File.separator +
                                    "puppet-module/modules/wso2as/files/configs/repository/components/lib" +
                                    "/mysql-connector-java-5.1.26-bin.jar"
                    );

                    // build mysql docker file
                    buildDockerFile(PropertyFileReader.dockerUrl, resourceLocation + File.separator
                            + "mysql" + File.separator + "5.7" + File.separator + "Dockerfile", "mysql:5.7.11");

                    // Running mysql docker image
                    JSONObject jsonObjectMySql = runDockerImage(resourceLocation + "json" + File.separator + mysql.getCreateContainerReqFile(),
                            resourceLocation + "json" + File.separator + mysql.getStartContainerReqFile());

                    ClientResponse responseInspectContainerMySql = inspectContainer(jsonObjectMySql);
                    Object jsonInspectObjMySql = new JSONObject(responseInspectContainerMySql.getEntity(String.class)).get("NetworkSettings");
                    mysqlContainerIP = ((JSONObject) jsonInspectObjMySql).get("IPAddress").toString();


                    dockerContainerList.add(mysqlContainerIP);
                    // DBOps
                    DataBaseManager dataBaseManager = new DataBaseManager();
                    dataBaseManager.performDBOperations(mysqlContainerIP);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}