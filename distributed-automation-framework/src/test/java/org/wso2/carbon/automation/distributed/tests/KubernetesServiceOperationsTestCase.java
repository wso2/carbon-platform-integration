/*
 *
 * Copyright (c) 2005-2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * /
 */

package org.wso2.carbon.automation.distributed.tests;

import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.distributed.beans.Deployment;
import org.wso2.carbon.automation.distributed.beans.DockerImageInstance;
import org.wso2.carbon.automation.distributed.commons.DeploymentConfigurationReader;
import org.wso2.carbon.automation.distributed.commons.KubernetesApiClient;
import org.wso2.carbon.automation.distributed.utills.AuthenticatorClient;
import org.wso2.carbon.automation.distributed.utills.KubernetesApiUtils;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KubernetesServiceOperationsTestCase {

    Map<String, Deployment> deploymentHashMap = new HashMap<>();
    KubernetesApiClient kubernetesApiClient;
    List<DockerImageInstance> wso2InstancesList;
    List<DockerImageInstance> dbInstancesList;

    @BeforeClass
    public void init() throws IOException {
        kubernetesApiClient = new KubernetesApiClient();
        System.setProperty(FrameworkPathUtil.SYSTEM_ARTIFACT_RESOURCE_LOCATION,
                           new File("src/test/resources/").getAbsolutePath() + File.separator);
        deploymentHashMap = new DeploymentConfigurationReader().getDeploymentHashMap();
        wso2InstancesList = new ArrayList<DockerImageInstance>(deploymentHashMap.get("APIM001")
                                                     .getInstancesMap().values());
        dbInstancesList = new ArrayList<DockerImageInstance>(deploymentHashMap.get("db")
                                                     .getInstancesMap().values());
    }

    @Test
    public void testCreateService()
            throws InterruptedException, RemoteException, LoginAuthenticationExceptionException,
                   AutomationFrameworkException {

        KubernetesApiUtils.deployDBImages(dbInstancesList);
        KubernetesApiUtils.deployWSO2Images(wso2InstancesList);
        for (DockerImageInstance instances : wso2InstancesList) {

            Map<String, String> labelMap = new HashMap<>();
            labelMap.put("name", instances.getLabel());
            ServiceSpec serviceSpec = kubernetesApiClient.getServiceSpec(labelMap);
            PodStatus podStatus = KubernetesApiUtils.waitForPod(instances.getNamespace(), labelMap);
            int httpsPort = 0;
            for (ServicePort servicePort : serviceSpec.getPorts()) {
                if (servicePort.getName().equals("servlet-https")) {
                    httpsPort = servicePort.getNodePort();
                }
            }
            AuthenticatorClient authenticatorClient =
                    new AuthenticatorClient("https://" + podStatus.getHostIP() + ":" + httpsPort + "/services/");

            Assert.assertTrue(authenticatorClient.login("admin", "admin", podStatus.getHostIP()));
        }
    }

    @AfterClass
    public void clean() throws IOException {
        KubernetesApiUtils.unDeployImages(wso2InstancesList);
        KubernetesApiUtils.unDeployImages(dbInstancesList);
    }

}
