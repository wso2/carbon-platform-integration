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
package org.wso2.carbon.automation.distributed.beans;

import java.util.HashMap;

public class Instances {

    private String distributionName;

    private String isCarbonInstance;

    private String id;

    private String dockerFileLocation;

    private String targetDockerImageName;

    public HashMap<String, String> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(HashMap<String, String> parameterMap) {
        this.parameterMap = parameterMap;
    }

    private HashMap<String,String> parameterMap;

    public String getDistributionName() {
        return distributionName;
    }

    public void setDistributionName(String distributionName) {
        this.distributionName = distributionName;
    }

    public String getIsCarbonInstance() {
        return isCarbonInstance;
    }

    public void setIsCarbonInstance(String isCarbonInstance) {
        this.isCarbonInstance = isCarbonInstance;
    }

    public String get_id() {
        return id;
    }

    public void set_id(String _id) {
        this.id = _id;
    }

    public String getDockerFileLocation() {
        return dockerFileLocation;
    }

    public void setDockerFileLocation(String dockerFileLocation) {
        this.dockerFileLocation = dockerFileLocation;
    }

    public String getTargetDockerImageName() {
        return targetDockerImageName;
    }

    public void setTargetDockerImageName(String targetDockerImageName) {
        this.targetDockerImageName = targetDockerImageName;
    }

}
