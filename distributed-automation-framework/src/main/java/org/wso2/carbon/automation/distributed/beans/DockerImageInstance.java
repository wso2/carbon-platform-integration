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
package org.wso2.carbon.automation.distributed.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Instances distributed configuration.
 */
public class DockerImageInstance {

    private boolean isCarbonInstance;

    private String label;

    private String targetDockerImageName;

    private String tag;

    private int priority;

    private String namespace;

    private Map<String, String> envVariableMap = new HashMap<>();

    private int replicas;

    List<Port> portList = new ArrayList<>();

    private String imagePullSecrets;

    private String serviceSelector;

    public DockerImageInstance() {
    }


    public DockerImageInstance(int priority, String targetDockerImageName, String tag) {
        this.priority = priority;
        this.targetDockerImageName = targetDockerImageName;
        this.tag = tag;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTargetDockerImageName() {
        return targetDockerImageName;
    }

    public void setTargetDockerImageName(String targetDockerImageName) {
        this.targetDockerImageName = targetDockerImageName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public boolean isCarbonInstance() {
        return isCarbonInstance;
    }

    public void setCarbonInstance(boolean isCarbonInstance) {
        this.isCarbonInstance = isCarbonInstance;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    public List<Port> getPortList() {
        return portList;
    }

    public void setPortList(List<Port> portList) {
        this.portList = portList;
    }

    public String getImagePullSecrets() {
        return imagePullSecrets;
    }

    public void setImagePullSecrets(String imagePullSecrets) {
        this.imagePullSecrets = imagePullSecrets;
    }

    public Map<String, String> getEnvVariableMap() {
        return envVariableMap;
    }

    public void setEnvVariableMap(Map<String, String> envVariableMap) {
        this.envVariableMap = envVariableMap;
    }

    public String getServiceSelector() {
        return serviceSelector;
    }

    public void setServiceSelector(String serviceSelector) {
        this.serviceSelector = serviceSelector;
    }
}
