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
package org.wso2.carbon.automation.test.api.clients.security;

import org.apache.axiom.om.util.Base64;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.security.mgt.stub.keystore.AddKeyStore;
import org.wso2.carbon.security.mgt.stub.keystore.GetKeyStoresResponse;
import org.wso2.carbon.security.mgt.stub.keystore.KeyStoreAdminServiceSecurityConfigExceptionException;
import org.wso2.carbon.security.mgt.stub.keystore.KeyStoreAdminServiceStub;
import org.wso2.carbon.security.mgt.stub.keystore.xsd.KeyStoreData;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class KeyStoreAdminServiceClient {
    private static final Log log = LogFactory.getLog(KeyStoreAdminServiceClient.class);

    private final String serviceName = "KeyStoreAdminService";
    private KeyStoreAdminServiceStub keyStoreAdminServiceStub;
    private String endPoint;

    public KeyStoreAdminServiceClient(String backEndUrl, String sessionCookie) throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        keyStoreAdminServiceStub = new KeyStoreAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, keyStoreAdminServiceStub);
    }

    public KeyStoreAdminServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        keyStoreAdminServiceStub = new KeyStoreAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, keyStoreAdminServiceStub);
    }

    public void addKeyStore(AddKeyStore keyStore)
            throws KeyStoreAdminServiceSecurityConfigExceptionException, RemoteException {
        keyStoreAdminServiceStub.addKeyStore(keyStore);
    }

    public void addKeyStore(String keyStoreFilePath, String fileName, String keyStorePassword,
                            String privateKeyPass)
            throws KeyStoreAdminServiceSecurityConfigExceptionException, IOException {
        AddKeyStore keyStore = new AddKeyStore();
        byte[] content = readFile(keyStoreFilePath);
        String data = Base64.encode(content);
        keyStore.setType("jks");
        keyStore.setFileData(data);
        keyStore.setFilename(fileName);
        keyStore.setPassword(keyStorePassword);
        keyStore.setPvtkeyPass(privateKeyPass);
        keyStore.setProvider("");
        keyStoreAdminServiceStub.addKeyStore(keyStore);
    }

    public GetKeyStoresResponse getKeyStores()
            throws KeyStoreAdminServiceSecurityConfigExceptionException, RemoteException {
        return keyStoreAdminServiceStub.getKeyStores();
    }

    public ArrayList<String> getKeyStoresList()
            throws KeyStoreAdminServiceSecurityConfigExceptionException, RemoteException {
        GetKeyStoresResponse response = keyStoreAdminServiceStub.getKeyStores();
        if (response == null) {
            return null;
        }
        KeyStoreData[] keyStoreData = response.get_return();
        if (keyStoreData == null || keyStoreData.length == 0) {
            return null;
        }
        ArrayList<String> keyStores = new ArrayList<String>();
        for (KeyStoreData key : keyStoreData) {
            if (key != null) {
                keyStores.add(key.getKeyStoreName());
            }
        }
        return keyStores;
    }

    public byte[] readFile(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        return bytes;
    }
}
