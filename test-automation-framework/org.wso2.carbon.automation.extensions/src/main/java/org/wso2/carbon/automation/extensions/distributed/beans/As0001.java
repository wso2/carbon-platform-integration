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

public class As0001 {

    private String dockerFileDir;
    private String repositoryName;
    private String version;
    private String dockerFileLocation;
    private String distributionName;
    private String createContainerReqFile;
    private String startContainerReqFile;
    private String yamlFileLocation;
    private boolean isThirdParty;

    public boolean isThirdParty() {
        return isThirdParty;
    }

    public void setThirdParty(boolean isThirdParty) {
        this.isThirdParty = isThirdParty;
    }

    public String getStartContainerReqFile() {
        return startContainerReqFile;
    }

    public void setStartContainerReqFile(String startContainerReqFile) {
        this.startContainerReqFile = startContainerReqFile;
    }

    public String getDockerFileDir() {
        return dockerFileDir;
    }

    public void setDockerFileDir(String dockerFileDir) {
        this.dockerFileDir = dockerFileDir;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDockerFileLocation() {
        return dockerFileLocation;
    }

    public void setDockerFileLocation(String dockerFileLocation) {
        this.dockerFileLocation = dockerFileLocation;
    }

    public String getDistributionName() {
        return distributionName;
    }

    public void setDistributionName(String distributionName) {
        this.distributionName = distributionName;
    }

    public String getCreateContainerReqFile() {
        return createContainerReqFile;
    }

    public void setCreateContainerReqFile(String createContainerReqFile) {
        this.createContainerReqFile = createContainerReqFile;
    }

    public String getYamlFileLocation() {
        return yamlFileLocation;
    }

    public void setYamlFileLocation(String yamlFileLocation) {
        this.yamlFileLocation = yamlFileLocation;
    }




}
