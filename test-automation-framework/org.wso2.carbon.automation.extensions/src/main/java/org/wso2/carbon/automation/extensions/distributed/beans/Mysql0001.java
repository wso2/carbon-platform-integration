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

public class Mysql0001 {

    private String dockerFileDir;
    private String repositoryName;
    private String version;
    private String dockerFileLocation;
    private String createContainerReqFile;
    private String startContainerReqFile;
    private String connectorLocation;
    private boolean isThirdParty;

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

    public String getCreateContainerReqFile() {
        return createContainerReqFile;
    }

    public void setCreateContainerReqFile(String createContainerReqFile) {
        this.createContainerReqFile = createContainerReqFile;
    }

    public String getStartContainerReqFile() {
        return startContainerReqFile;
    }

    public void setStartContainerReqFile(String startContainerReqFile) {
        this.startContainerReqFile = startContainerReqFile;
    }

    public String getConnectorLocation() {
        return connectorLocation;
    }

    public void setConnectorLocation(String connectorLocation) {
        this.connectorLocation = connectorLocation;
    }

    public boolean isThirdParty() {
        return isThirdParty;
    }

    public void setThirdParty(boolean isThirdParty) {
        this.isThirdParty = isThirdParty;
    }


}
