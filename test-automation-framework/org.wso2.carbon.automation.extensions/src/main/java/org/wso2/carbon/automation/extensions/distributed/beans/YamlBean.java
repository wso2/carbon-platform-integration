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

package org.wso2.carbon.automation.extensions.distributed.beans;

import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.distributed.util.PropertyFileReader;

import java.io.File;
import java.util.HashMap;

public class YamlBean {

    private HashMap<String, Object> yamlHierarchy = new HashMap<String, Object>();

    public HashMap<String, Object> getYamlHierarchy() {
        return yamlHierarchy;
    }

    public void setYamlHierarchy(HashMap<String, Object> yamlHierarchy) {
        this.yamlHierarchy = yamlHierarchy;
    }


    private String dockerFileDir = FrameworkPathUtil.getSystemResourceLocation() + File.separator
            + "temp" + File.separator + "dockerfiles";

    public String getDockerUrl() {
        return dockerUrl;
    }

    public void setDockerUrl(String dockerUrl) {
        this.dockerUrl = dockerUrl;
    }

    private String dockerUrl;

    public YamlBean() {

        dockerUrl = PropertyFileReader.dockerUrl;

        As0001 as0001 = new As0001();

        as0001.setThirdParty(false);
        as0001.setVersion("5.3.0");
        as0001.setDockerFileLocation(dockerFileDir + File.separator + "wso2as");
        as0001.setDistributionName("wso2as-5.3.0.zip");
        as0001.setRepositoryName("wso2/as-5.3.0:1.0.0");
        as0001.setCreateContainerReqFile("wso2ascreatecontainer.json");
        as0001.setStartContainerReqFile("wso2asstartcontainer.json");
        as0001.setYamlFileLocation(FrameworkPathUtil.getSystemResourceLocation()
                + File.separator + "yaml" + File.separator + "as" + File.separator + "default.yaml");


        Esb0001 esb0001 = new Esb0001();

        esb0001.setThirdParty(false);
        esb0001.setVersion("4.9.0");
        esb0001.setDockerFileLocation(dockerFileDir + File.separator + "wso2esb");
        esb0001.setDistributionName("wso2esb-4.9.0.zip");
        esb0001.setRepositoryName("wso2/esb-4.9.0:1.0.0");
        esb0001.setCreateContainerReqFile("wso2esbcreatecontainer.json");
        esb0001.setStartContainerReqFile("wso2esbstartcontainer.json");
        esb0001.setYamlFileLocation(FrameworkPathUtil.getSystemResourceLocation()
                + File.separator + "yaml" + File.separator + "esb" + File.separator + "default.yaml");


        Mysql0001 mysql0001 = new Mysql0001();

        mysql0001.setThirdParty(true);
        mysql0001.setVersion("5.7.11");
        mysql0001.setDockerFileLocation(FrameworkPathUtil.getSystemResourceLocation() + File.separator
                + "mysql" + File.separator + "5.7");
        mysql0001.setRepositoryName("mysql:5.7.11");
        mysql0001.setCreateContainerReqFile("mysqlcreatecontainer.json");
        mysql0001.setStartContainerReqFile("mysqlstartcontainer.json");
        mysql0001.setConnectorLocation(FrameworkPathUtil.getSystemResourceLocation()
                + "connectors/mysql-connector-java-5.1.26-bin.jar");

        yamlHierarchy.put("As0001", as0001);
        yamlHierarchy.put("Esb0001", esb0001);
        yamlHierarchy.put("Mysql0001", mysql0001);

    }

}

