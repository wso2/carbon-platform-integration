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

package org.wso2.carbon.automation.distributed.utills;


import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.distributed.beans.Instances;
import org.wso2.carbon.automation.distributed.beans.Port;
import org.wso2.carbon.automation.distributed.commons.DeploymentYamlConstants;
import org.wso2.carbon.automation.distributed.commons.KubernetesApiClient;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Utility for calling Kubernetes api
 */
public class KubernetesApiUtils {

//    private String dockerRegistry;
//    private String dockerSecret;

    private static final Log log = LogFactory.getLog(KubernetesApiUtils.class);
    private static KubernetesApiClient kubernetesApiClient = new KubernetesApiClient();
    private static final long TIMEOUT = 120000;


    public static void deployWSO2Images(List<Instances> instancesList)
            throws InterruptedException, AutomationFrameworkException, RemoteException,
                   LoginAuthenticationExceptionException {

        instancesList.sort((Instances instances1, Instances instances2) ->
                                   instances1.getPriority() - instances2.getPriority());
        for (Instances instances : instancesList) {
            kubernetesApiClient.createReplicationController(instances);
            kubernetesApiClient.createService(instances);

            Map<String, String> labelMap = new HashMap<>();
            labelMap.put(DeploymentYamlConstants.YAML_DEPLOYMENT_INSTANCES_LABEL_NAME, instances.getLabel());

            ServiceSpec serviceSpec = kubernetesApiClient.getServiceSpec(labelMap);

            PodStatus podStatus = waitForPod(instances.getNamespace(), labelMap);

            waitForPort(serviceSpec.getPorts().get(0).getNodePort(), 120000L, true, podStatus.getHostIP());

            int httpsPort = 0;
            for (ServicePort servicePort : serviceSpec.getPorts()) {
                if (servicePort.getName().equals("servlet-https")) {
                    httpsPort = servicePort.getNodePort();
                }
            }
            log.info("httpsPort" + httpsPort);
            waitForLogin(podStatus.getHostIP(), httpsPort);
        }
    }


    public static void deployDBImages(List<Instances> instancesList)
            throws InterruptedException, AutomationFrameworkException, RemoteException,
                   LoginAuthenticationExceptionException {

        instancesList.sort((Instances instances1, Instances instances2) ->
                                   instances1.getPriority() - instances2.getPriority());
        for (Instances instances : instancesList) {
            kubernetesApiClient.createReplicationController(instances);
            kubernetesApiClient.createService(instances);

            Map<String, String> labelMap = new HashMap<>();
            labelMap.put(DeploymentYamlConstants.YAML_DEPLOYMENT_INSTANCES_LABEL_NAME, instances.getLabel());

            ServiceSpec serviceSpec = kubernetesApiClient.getServiceSpec(labelMap);

            PodStatus podStatus = waitForPod(instances.getNamespace(), labelMap);

            waitForPort(serviceSpec.getPorts().get(0).getPort(), 120000L, true, podStatus.getHostIP());

        }
    }


    public static void unDeployImages(List<Instances> instancesList) {
        Map<String, String> labelMap = new HashMap<>();
        for (Instances instances : instancesList) {
            labelMap.put(DeploymentYamlConstants.YAML_DEPLOYMENT_INSTANCES_LABEL_NAME, instances.getLabel());
            kubernetesApiClient.deleteService(labelMap);
            kubernetesApiClient.deleteReplicationController(labelMap);
        }
    }

    public static PodStatus waitForPod(String namespace, Map<String, String> labelMap)
            throws InterruptedException {
        long startTime = System.currentTimeMillis();
        boolean isPodsRunning = false;
        PodStatus podStatus = null;
        while (((System.currentTimeMillis() - startTime) < TIMEOUT) && !isPodsRunning) {
            podStatus = kubernetesApiClient.getPodStatus(namespace, labelMap);
            if (podStatus.getPhase().trim().equals("Running")) {
                isPodsRunning = true;
            }
        }
        return podStatus;
    }

    public static void waitForPort(int port, long timeout, boolean verbose, String hostName)
            throws RuntimeException {
        long startTime = System.currentTimeMillis();
        boolean isPortOpen = false;

        while (true) {
            if (!isPortOpen && System.currentTimeMillis() - startTime < timeout) {
                Socket socket = null;

                try {
                    InetAddress e = InetAddress.getByName(hostName);
                    socket = new Socket(e, port);
                    isPortOpen = socket.isConnected();
                    if (!isPortOpen) {
                        continue;
                    }

                    if (verbose) {
                        log.info("Successfully connected to the server on port " + port);
                    }
                } catch (IOException var22) {
                    if (verbose) {
                        log.info("Waiting until server starts on port " + port);
                    }

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException var21) {
                        ;
                    }
                    continue;
                } finally {
                    try {
                        if (socket != null && socket.isConnected()) {
                            socket.close();
                        }
                    } catch (IOException var20) {
                        log.error("Can not close the socket with is used to check the server status ", var20);
                    }

                }

                return;
            }

            throw new RuntimeException("Port " + port + " is not open");
        }
    }


    public static void waitForLogin(String hostIP, int httpsPort)
            throws AutomationFrameworkException, AxisFault {

        long startTime = System.currentTimeMillis();
        boolean loginSuccess = false;

        AuthenticatorClient authenticatorClient =
                new AuthenticatorClient("https://" + hostIP + ":" + httpsPort + "/services/");

        while (((System.currentTimeMillis() - startTime) < TIMEOUT) && !loginSuccess) {
            log.info("Waiting for user login...");
            try {
                loginSuccess = authenticatorClient.login("admin", "admin", hostIP);

                if (!loginSuccess) {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Login failed after server startup ", e);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                    //ignored because of login attempts which could happen before proper carbon
                    //server startup
                }
            }
        }

        if (!loginSuccess) {
            throw new AutomationFrameworkException("Login failed! while verifying server startup" +
                                                   ". Please make sure that server is up and running " +
                                                   "or user is a valid user");
        }
    }

    public static void main(String[] args)
            throws IOException, InterruptedException, AutomationFrameworkException,
                   LoginAuthenticationExceptionException {

        Instances instances = new Instances();
        instances.setCarbonInstance(true);
        instances.setLabel("wso2am-api-publisher");

        Map<String, String> envMap = new HashMap<>();
        envMap.put("KUBERNETES_MASTER_SKIP_SSL_VERIFICATION", "true");
        envMap.put("KUBERNETES_API_SERVER", "http://192.168.19.45:8080");
        envMap.put("KUBERNETES_SERVICES", "wso2am-api-publisher");

        instances.setEnvVariableMap(envMap);

        instances.setImagePullSecrets("registrypullsecret");
        instances.setNamespace("default");

        List<Port> portList = new ArrayList<>();
        portList.add(new Port("servlet-http", 9763, 9763, 32016, "TCP"));
        portList.add(new Port("servlet-https", 9443, 9443, 32017, "TCP"));

        instances.setPortList(portList);
        instances.setPriority(1);
        instances.setReplicas(1);
        instances.setTag("1.10.0");
        instances.setTargetDockerImageName("dimuthud-wso2-am-api-publisher");

        List<Instances> instancesList = new ArrayList<>();
        instancesList.add(instances);
        log.info(instancesList);
        KubernetesApiUtils kubernetesApiUtils = new KubernetesApiUtils();
        kubernetesApiUtils.deployWSO2Images(instancesList);
    }
}
