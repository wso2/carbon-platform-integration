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

package org.wso2.carbon.automation.distributed.commons;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.distributed.beans.RepositoryInfoBeans;
import org.wso2.carbon.automation.distributed.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.distributed.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.distributed.utills.GitRepositoryUtil;
import org.wso2.carbon.automation.distributed.utills.ScriptExecutorUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles initiates fundamental platform required for distributed deployment.
 */
public class BaseManager {

    public BaseManager()
            throws AutomationFrameworkException, IOException, GitAPIException, InterruptedException,
                   LoginAuthenticationExceptionException {

        Log log = LogFactory.getLog(BaseManager.class);

        repoBeansInitializer(new GenericYamlParser().yamlInitializer(FrameworkPathUtil.getSystemResourceLocation()
                                                                     + "deployment.yaml"));

        // step 01
        // git clone - dockerfile repo

        log.info("Performing git clone - dockerfile");

        String resourceLocation = FrameworkPathUtil.getSystemResourceLocation();
        try {

            GitRepositoryUtil.gitCloneRepository(RepositoryInfoBeans.getDockerRepoLocation(), resourceLocation
                                                                                              + File.separator + "Docker-Puppet" + File.separator + "dockerfiles");
        } catch (GitAPIException e) {
            throw new AutomationFrameworkException("Docker files git clone failed.", e);
        }

        // git clone - puppetmodule repo
        log.info("Performing git clone - puppetmodule");

        try {
            GitRepositoryUtil.gitCloneRepository(RepositoryInfoBeans.getPuppetRepoLocation(), resourceLocation
                                                                                              + File.separator + "Docker-Puppet" + File.separator + "puppet-module");
        } catch (GitAPIException e) {
            throw new AutomationFrameworkException("puppet-modules git clone failed.", e);
        }

        log.info("Performing git clone - kubernetes-artifacts");

        try {
            GitRepositoryUtil.gitCloneRepository(RepositoryInfoBeans.getK8sArtifactsRepoLocation(), resourceLocation
                                                                                              + File.separator + "Docker-Puppet" + File.separator + "kubernetes-artifacts");
        } catch (GitAPIException e) {
            throw new AutomationFrameworkException("puppet-modules git clone failed.", e);
        }

//        execution of automation script
        new ScriptExecutorUtil().scriptExecution();

    }


    private void repoBeansInitializer(Map deploymentMap) throws IOException {
        for (Object object : deploymentMap.entrySet()) {
            Map.Entry mEntry = (Map.Entry) object;
            if (mEntry.getKey().equals("repositories")) {
                HashMap hashMap = (HashMap) mEntry.getValue();
                RepositoryInfoBeans.setPuppetRepoLocation(hashMap.get("puppetModuleRepository").toString());
                RepositoryInfoBeans.setDockerRepoLocation(hashMap.get("dockerFileModuleRepository").toString());
                RepositoryInfoBeans.setK8sArtifactsRepoLocation(hashMap.get("k8sArtifactsRepository").toString());
                RepositoryInfoBeans.setDockerRegistryLocation(hashMap.get("dockerRegistry").toString());
            }
        }
    }

}

