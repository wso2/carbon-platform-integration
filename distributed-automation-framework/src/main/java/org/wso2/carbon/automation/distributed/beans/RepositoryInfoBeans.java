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

/**
 * RThis bean class can be for initialize repositories related info.
 */

public class RepositoryInfoBeans {

    private static String dockerRepoLocation;
    private static String puppetRepoLocation;
    private static String dockerRegistryLocation;

    public static String getDockerRepoLocation() {
        return dockerRepoLocation;
    }

    public static synchronized void setDockerRepoLocation(String dockerRepoLocation) {
        RepositoryInfoBeans.dockerRepoLocation = dockerRepoLocation;
    }

    public static synchronized String getPuppetRepoLocation() {
        return puppetRepoLocation;
    }

    public static synchronized  void setPuppetRepoLocation(String puppetRepoLocation) {
        RepositoryInfoBeans.puppetRepoLocation = puppetRepoLocation;
    }

    public static synchronized  String getDockerRegistryLocation() {
        return dockerRegistryLocation;
    }

    public static synchronized  void setDockerRegistryLocation(String dockerRegistryLocation) {
        RepositoryInfoBeans.dockerRegistryLocation = dockerRegistryLocation;
    }

}
