/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.automation.test.utils.governance;

import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.carbon.automation.test.utils.common.XmlFileReaderUtil;
import org.wso2.carbon.governance.api.policies.PolicyManager;
import org.wso2.carbon.governance.api.policies.dataobjects.Policy;
import org.wso2.carbon.governance.api.schema.SchemaManager;
import org.wso2.carbon.governance.api.schema.dataobjects.Schema;
import org.wso2.carbon.governance.api.services.ServiceManager;
import org.wso2.carbon.governance.api.services.dataobjects.Service;
import org.wso2.carbon.governance.api.wsdls.WsdlManager;
import org.wso2.carbon.governance.api.wsdls.dataobjects.Wsdl;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class MetaDataUtils {
    public static String addService(String nameSpace, String serviceName, Registry governance)
            throws Exception {
        ServiceManager serviceManager = new ServiceManager(governance);
        Service service;
        service = serviceManager.newService(new QName(nameSpace, serviceName));
        serviceManager.addService(service);
        for (String serviceId : serviceManager.getAllServiceIds()) {
            service = serviceManager.getService(serviceId);
            if (service.getPath().endsWith(serviceName) && service.getPath().contains("trunk")) {

                return service.getPath();
            }

        }
        throw new Exception("Getting Service path failed");
    }

    public static String addSchema(String name, Registry governance, String schemaFilePath)
            throws IOException, RegistryException, XMLStreamException {
        SchemaManager schemaManager = new SchemaManager(governance);
        Schema schema = schemaManager.newSchema(XmlFileReaderUtil.read(schemaFilePath).toString().getBytes(), name);
        schemaManager.addSchema(schema);
        schema = schemaManager.getSchema(schema.getId());
        return schema.getPath();
    }

    public static String addPolicy(String policyName, Registry governance, String policyFilePath)
            throws RegistryException, IOException {
        PolicyManager policyManager = new PolicyManager(governance);
        Policy policy = policyManager.newPolicy(FileManager.readFile(policyFilePath).getBytes(), policyName);
        policyManager.addPolicy(policy);
        policy = policyManager.getPolicy(policy.getId());
        return policy.getPath();

    }

    public static String addWSDL(String name, Registry governance, String wsdlFilePath, Wsdl wsdl)
            throws IOException, RegistryException {
        WsdlManager wsdlManager = new WsdlManager(governance);
        wsdl = wsdlManager.newWsdl(FileManager.readFile(wsdlFilePath).getBytes(), name);
        wsdlManager.addWsdl(wsdl);
        wsdl = wsdlManager.getWsdl(wsdl.getId());

        return wsdl.getPath();
    }
}
