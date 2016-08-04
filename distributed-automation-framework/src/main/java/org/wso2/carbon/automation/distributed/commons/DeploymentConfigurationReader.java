/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.automation.distributed.commons;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.distributed.beans.Deployment;
import org.wso2.carbon.automation.distributed.beans.DockerImageInstance;
import org.wso2.carbon.automation.distributed.beans.Port;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Reader for deployment.yaml forl for deployment information
 */
public class DeploymentConfigurationReader {

    private static final Log log = LogFactory.getLog(DeploymentConfigurationReader.class);
    private DeploymentConfigurationReader deploymentConfigurationReader = null;
    private HashMap<String, Deployment> deploymentHashMap;

    //Get the only object available
    public DeploymentConfigurationReader() {
    }

    public DeploymentConfigurationReader readConfiguration() throws IOException {
        synchronized (DeploymentConfigurationReader.class) {
            if (deploymentConfigurationReader == null) {
                deploymentConfigurationReader = new DeploymentConfigurationReader();
                deploymentHashMap = readConfigurationYaml();
            }
        }
        return deploymentConfigurationReader;
    }

    private static HashMap<String, Deployment> readConfigurationYaml() throws
                                                                       IOException {
        Map<String, Object> map = getDeploymentObjectMap();
        HashMap<String, Deployment> deploymentHashMap = new HashMap<>();
        ArrayList<Object> deploymentList = (ArrayList<Object>) map.get(DeploymentYamlConstants.YAML_DEPLOYMENTS);
        for (Object deployment : deploymentList) {
            Deployment deploymentObj = new Deployment();
            HashMap<String, DockerImageInstance> instanceInDeployment = new HashMap<>();

            deploymentObj.setId(((Map<String, Object>) ((Map<String, Object>) deployment)
                    .get(DeploymentYamlConstants.YAML_DEPLOYMENT))
                                        .get(DeploymentYamlConstants.YAML_DEPLOYMENT_ID)
                                        .toString());

            ArrayList<Object> instancesArrayList =
                    ((ArrayList<Object>) ((Map<String, Object>) ((Map<String, Object>) deployment)
                            .get(DeploymentYamlConstants.YAML_DEPLOYMENT))
                            .get(DeploymentYamlConstants.YAML_DEPLOYMENT_INSTANCES));

            for (Object instance : instancesArrayList) {
                DockerImageInstance instanceToMap = new DockerImageInstance();

                instanceToMap
                        .setTargetDockerImageName(((LinkedHashMap) instance)
                                                          .get(DeploymentYamlConstants
                                                                       .YAML_DEPLOYMENT_INSTANCES_DOCKER_IMAGE_NAME)
                                                          .toString());
                instanceToMap.setNamespace(((LinkedHashMap) instance)
                                                   .get(DeploymentYamlConstants
                                                                .YAML_DEPLOYMENT_INSTANCES_NAMESPACE)
                                                   .toString());
                instanceToMap.setLabel(((LinkedHashMap) instance)
                                               .get(DeploymentYamlConstants.YAML_DEPLOYMENT_INSTANCES_LABEL)
                                               .toString());
                instanceToMap.setServiceSelector(((LinkedHashMap) instance)
                                                         .get(DeploymentYamlConstants
                                                                      .YAML_DEPLOYMENT_NODE_SELECTOR)
                                                         .toString());
                instanceToMap.setReplicas(Integer.parseInt(((LinkedHashMap) instance)
                                                                   .get(DeploymentYamlConstants
                                                                                .YAML_DEPLOYMENT_NODE_REPLICAS)
                                                                   .toString()));
                instanceToMap.setPriority(Integer.parseInt(((LinkedHashMap) instance)
                                                                   .get(DeploymentYamlConstants
                                                                                .YAML_DEPLOYMENT_INSTANCE_PRIORITY)
                                                                   .toString()));

                instanceToMap.setTag(((LinkedHashMap) instance)
                                             .get(DeploymentYamlConstants
                                                          .YAML_DEPLOYMENT_INSTANCES_DOCKER_TAG_NAME)
                                             .toString());
                instanceToMap.setImagePullSecrets(((LinkedHashMap) instance)
                                                          .get(DeploymentYamlConstants
                                                                       .YAML_DEPLOYMENT_INSTANCES_DOCKER_HUB_SECRETS)
                                                          .toString());

                instanceToMap.setPortList(portList((LinkedHashMap) instance));
                instanceToMap.setEnvVariableMap(envVariableMap((LinkedHashMap) instance));

                instanceInDeployment.put(((LinkedHashMap) instance)
                                                 .get(DeploymentYamlConstants
                                                              .YAML_DEPLOYMENT_INSTANCES_LABEL).toString(),
                                         instanceToMap);
            }
            deploymentObj.setInstancesMap(instanceInDeployment);
            deploymentHashMap.put(deploymentObj.getId(), deploymentObj);
        }

        return deploymentHashMap;
    }


    private static Map<String, String> envVariableMap(Map<String, ArrayList<String>> instanceMap) {
        Map<String, String> envVariableMap = null;
        Iterator<Entry<String, ArrayList<String>>> itr = instanceMap.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<String, ArrayList<String>> instanceEntry = itr.next();
            if (instanceEntry.getKey().equals(DeploymentYamlConstants.YAML_DEPLOYMENT_INSTANCES_ENV_MAP)) {
                try {
                    Object valueObject = instanceEntry.getValue().get(0);
                    envVariableMap = (LinkedHashMap) valueObject;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return envVariableMap;
    }

    private static List<Port> portList(Map<String, ArrayList<String>> instanceMap) {
        Map<String, String> portValuedMap;
        Iterator<Entry<String, ArrayList<String>>> instanceIterator = instanceMap.entrySet().iterator();
        List<Port> portList = new ArrayList<>();
        while (instanceIterator.hasNext()) {
            Entry<String, ArrayList<String>> instanceEntry = instanceIterator.next();
            if (instanceEntry.getKey().equals(DeploymentYamlConstants.YAML_DEPLOYMENT_PORTS)) {
                for (Object valueObject : instanceEntry.getValue()) {
                    portValuedMap = (LinkedHashMap) valueObject;
                    Iterator<Entry<String, String>> portIterator = portValuedMap.entrySet().iterator();
                    Port port = new Port();
                    while (portIterator.hasNext()) {
                        Entry<String, String> portEntry = portIterator.next();
                        if (portEntry.getKey().equals(DeploymentYamlConstants.YAML_DEPLOYMENT_PORT_NAME)) {
                            port.setName(portEntry.getValue());
                        } else if (portEntry.getKey().equals(DeploymentYamlConstants.YAML_DEPLOYMENT_PORT_PORT)) {
                            port.setPort(Integer.parseInt(String.valueOf(portEntry.getValue())));
                        } else if (portEntry.getKey().equals(DeploymentYamlConstants.YAML_DEPLOYMENT_PORT_NODE_PORT)) {
                            port.setNodePort(Integer.parseInt(String.valueOf(portEntry.getValue())));
                        } else if (portEntry.getKey()
                                .equals(DeploymentYamlConstants.YAML_DEPLOYMENT_PORT_TARGET_PORT)) {
                            port.setTargetPort(Integer.parseInt(String.valueOf(portEntry.getValue())));
                        } else if (portEntry.getKey().equals(DeploymentYamlConstants.YAML_DEPLOYMENT_PORT_PROTOCOL)) {
                            port.setProtocol(portEntry.getValue());
                        }
                    }
                    portList.add(port);
                }
            }
        }

        return portList;
    }

    public HashMap<String, Deployment> getDeploymentHashMap() throws IOException {
        if (deploymentConfigurationReader == null) {
            readConfiguration();
        }
        return deploymentHashMap;
    }

    private static Map<String, Object> getDeploymentObjectMap() throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FrameworkPathUtil.
                    getSystemResourceLocation() + DeploymentYamlConstants.DEPLYMENT_YAML_FILE_NAME);
            Yaml yaml = new Yaml();
            return (Map<String, Object>) yaml.load(fis);
        } finally {

            if (fis != null) {
                fis.close();
            }
        }
    }

}


