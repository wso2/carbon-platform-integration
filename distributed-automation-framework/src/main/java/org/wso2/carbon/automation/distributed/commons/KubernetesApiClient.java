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

package org.wso2.carbon.automation.distributed.commons;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.LocalObjectReference;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.ReplicationControllerSpec;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.distributed.beans.DockerImageInstance;
import org.wso2.carbon.automation.distributed.beans.Port;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Utility Client for calling Kubernetes api
 */
public class KubernetesApiClient {

    private static final Log log = LogFactory.getLog(KubernetesApiClient.class);

    private KubernetesClient kubernetesClient;


    public KubernetesApiClient() {
        kubernetesClient = new DefaultKubernetesClient(KubernetesConstants.KUBERNETES_MASTER_URL);
    }

    /**
     * Create kubernetes service
     *
     * @throws KubernetesClientException
     */
    public Service createService(DockerImageInstance instances)
            throws KubernetesClientException {
        Service service = new Service();
        ObjectMeta objectMeta = new ObjectMeta();
        objectMeta.setName(instances.getLabel());
        Map<String, String> labelMap = new HashMap<>();
        labelMap.put(DeploymentYamlConstants.YAML_DEPLOYMENT_INSTANCES_LABEL_NAME, instances.getLabel());
        objectMeta.setLabels(labelMap);
        service.setMetadata(objectMeta);

        ServiceSpec serviceSpec = new ServiceSpec();
        serviceSpec.setType("NodePort");
        serviceSpec.setSessionAffinity("ClientIP");

        List<ServicePort> servicePortList = new ArrayList<>();

        for (Port port : instances.getPortList()) {
            ServicePort servicePort = new ServicePort();
            servicePort.setName(port.getName());
            servicePort.setPort(port.getPort());
            if (port.getTargetPort() > 0) {
                servicePort.setTargetPort(new IntOrString(port.getTargetPort()));
            }
            if (port.getNodePort() > 0) {
                servicePort.setNodePort(port.getNodePort());
            }
            servicePortList.add(servicePort);
        }
        serviceSpec.setPorts(servicePortList);
        Map<String, String> selectorMap = new HashMap<>();
        selectorMap.put(DeploymentYamlConstants.YAML_DEPLOYMENT_INSTANCES_LABEL_NAME, instances.getServiceSelector());
        serviceSpec.setSelector(selectorMap);
        service.setSpec(serviceSpec);
        return kubernetesClient.services().inNamespace(instances.getNamespace()).create(service);
    }


    public ServiceSpec getServiceSpec(Map<String, String> map)
            throws KubernetesClientException {
        try {
            return kubernetesClient.services().withLabels(map).list().getItems().get(0).getSpec();
        } catch (Exception e) {
            String msg = String.format("Could not retrieve kubernetes service: lable with map => %s", map);
            log.error(msg, e);
            throw new KubernetesClientException(msg, e);
        }
    }


    public List<Service> getServices() throws KubernetesClientException {
        try {
            return kubernetesClient.services().list().getItems();
        } catch (Exception e) {
            String msg = "Could not retrieve kubernetes services";
            log.error(msg, e);
            throw new KubernetesClientException(msg, e);
        }
    }


    public boolean deleteService(Map<String, String> map)
            throws KubernetesClientException {

        try {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Deleting kubernetes service: lable with map %s", map));
            }

            return kubernetesClient.services().withLabels(map).delete();

        } catch (Exception e) {
            String msg = String.format("Could not delete kubernetes service: [service-id] %s", map);
            log.error(msg, e);
            throw new KubernetesClientException(msg, e);
        }
    }

    //String id, String namespace, int replicas, Map<String, String> controllerLabels,
    //Map<String, String> selectorLabels, String podId, String image, List<String> containerPorts,
    // Map<String, String> podLabels, String manifestId
    public void createReplicationController(DockerImageInstance instances) {

        ReplicationController rc = new ReplicationController();

        ObjectMeta objectMeta = new ObjectMeta();
        objectMeta.setName(instances.getLabel());
        Map<String, String> labelMap = new HashMap<>();
        labelMap.put(DeploymentYamlConstants.YAML_DEPLOYMENT_INSTANCES_LABEL_NAME, instances.getLabel());
        objectMeta.setLabels(labelMap);
        rc.setMetadata(objectMeta);

        ReplicationControllerSpec replicationControllerSpec = new ReplicationControllerSpec();

        replicationControllerSpec.setReplicas(instances.getReplicas());

        Map<String, String> selectorMap = new HashMap<>();
        selectorMap.put(DeploymentYamlConstants.YAML_DEPLOYMENT_INSTANCES_LABEL_NAME, instances.getLabel());

        replicationControllerSpec.setSelector(selectorMap);

        PodTemplateSpec podTemplateSpec = new PodTemplateSpec();
        podTemplateSpec.setMetadata(objectMeta);

        PodSpec podSpec = new PodSpec();
        List<Container> containerList = new ArrayList<>();
        podSpec.setContainers(containerList);

        List<LocalObjectReference> localObjectReferenceList = new ArrayList<>();
        LocalObjectReference localObjectReference = new LocalObjectReference();

        localObjectReference.setName(instances.getImagePullSecrets());
        localObjectReferenceList.add(localObjectReference);
        podSpec.setImagePullSecrets(localObjectReferenceList);

        Container container = new Container();
        container.setName(instances.getLabel());
        container.setImage(instances.getTargetDockerImageName() + ":" + instances.getTag());

        List<EnvVar> envVarList = new ArrayList<>();
        Iterator it = instances.getEnvVariableMap().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            EnvVar envVar = new EnvVar();
            envVar.setName(pair.getKey().toString());
            envVar.setValue(pair.getValue().toString());
            envVarList.add(envVar);
            it.remove(); // avoids a ConcurrentModificationException
        }
        container.setEnv(envVarList);
        containerList.add(container);
        podTemplateSpec.setSpec(podSpec);
        List<ContainerPort> containerPortList = new ArrayList<>();
        for (Port port : instances.getPortList()) {
            ContainerPort containerPort = new ContainerPort();
            containerPort.setContainerPort(port.getPort());
            containerPort.setProtocol(port.getProtocol());
            containerPortList.add(containerPort);
        }
        container.setPorts(containerPortList);
        replicationControllerSpec.setTemplate(podTemplateSpec);
        replicationControllerSpec.setReplicas(instances.getReplicas());
        rc.setSpec(replicationControllerSpec);
        kubernetesClient.replicationControllers().inNamespace(instances.getNamespace()).create(rc);
    }


    public void deleteReplicationController(Map<String, String> map)
            throws KubernetesClientException {
        try {
            kubernetesClient.replicationControllers().withLabels(map).delete();
        } catch (Exception e) {
            String msg = String.format("Could not delete kubernetes rc: map %s", map);
            log.error(msg, e);
            throw new KubernetesClientException(msg, e);
        }
    }

    public PodStatus getPodStatus(String namespace, Map<String, String> map)
            throws KubernetesClientException {
        try {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Deleting kubernetes replication controller: map %s namespace %s", map, namespace));
            }
            Pod pod = kubernetesClient.pods().inNamespace(namespace).withLabels(map).list().getItems().get(0);
            return pod.getStatus();
        } catch (Exception e) {
            String msg = String.format("Could not get kubernetes pod status : map %s namespace %s", map, namespace);
            log.error(msg, e);
            throw new KubernetesClientException(msg, e);
        }
    }

}
