/*
*Copyright (c) 2005-2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.automation.distributed.commons;

import org.wso2.carbon.automation.distributed.beans.Deployment;
import org.wso2.carbon.automation.distributed.beans.Instances;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DeploymentConfigurationReader {
    public static Map<String, Object> map;

    private static DeploymentConfigurationReader instance;

    private DeploymentConfigurationReader() {
    }

    //Get the only object available

    public static synchronized DeploymentConfigurationReader getInstance(String filePath) throws FileNotFoundException {
        if (instance == null) {
            instance = new DeploymentConfigurationReader();
            Reader input = new FileReader(new File(filePath));
            Yaml yaml = new Yaml();
            map = (Map<String, Object>) yaml.load(input);
        }
        return instance;
    }

    public static HashMap<String, Deployment> build() throws FileNotFoundException {


        HashMap<String, Deployment> deploymentHashMap = new HashMap<>();
        ArrayList<Object> deploymentList = (ArrayList<Object>) map.get(DeploymentYamlConstants.YAML_DEPLOYMENTS);
        for (Object deployment : deploymentList) {
            Deployment deploymentObj = new Deployment();
            HashMap<String, Instances> instanceInDeployment = new HashMap<>();
            deploymentObj.setId(((Map<String, Object>) ((Map<String, Object>) deployment).get(DeploymentYamlConstants
                                                                                                      .YAML_DEPLOYMENT))
                                        .get(DeploymentYamlConstants.YAML_DEPLOYMENT_ID)
                                        .toString());
            ArrayList<Object> instancesArrayList = ((ArrayList<Object>) ((Map<String, Object>) ((Map<String, Object>)
                                                                                                        deployment).get
                    (DeploymentYamlConstants
                             .YAML_DEPLOYMENT)).get(DeploymentYamlConstants.YAML_DEPLOYMENT_INSTANCES));
            for (Object instance : instancesArrayList
                    ) {
                Instances instanceToMap = new Instances();
                instanceToMap
                        .setDockerFileLocation(((LinkedHashMap) instance)
                                                       .get(DeploymentYamlConstants
                                                                    .YAML_DEPLOYMENT_INSTANCES_DOCKER_FILE_LOCTION)
                                                       .toString());
                instanceToMap
                        .setDistributionName(((LinkedHashMap) instance)
                                                     .get(DeploymentYamlConstants
                                                                  .YAML_DEPLOYMENT_INSTANCES_DISTRIBUTION_NAME)
                                                     .toString());
                instanceToMap
                        .setIsCarbonInstance(((LinkedHashMap) instance)
                                                     .get(DeploymentYamlConstants
                                                                  .YAML_DEPLOYMENT_INSTANCES_CARBON_INSTANCE)
                                                     .toString());
                instanceToMap
                        .setTargetDockerImageName(((LinkedHashMap) instance)
                                                          .get(DeploymentYamlConstants
                                                                       .YAML_DEPLOYMENT_INSTANCES_DOCKER_IMAGE_NAME)
                                                          .toString());
                instanceToMap.setParameterMap((HashMap) (((LinkedHashMap) instance)
                                                                 .get(DeploymentYamlConstants
                                                                              .YAML_DEPLOYMENT_INSTANCES_PARAMETERS)));
                instanceInDeployment.put(((LinkedHashMap) instance)
                                                 .get(DeploymentYamlConstants.YAML_DEPLOYMENT_INSTANCES_ID).toString(),
                                         instanceToMap);
            }
            deploymentObj.setInstancesMap(instanceInDeployment);
            deploymentHashMap.put(deploymentObj.getId(), deploymentObj);
        }

        return deploymentHashMap;
    }
}