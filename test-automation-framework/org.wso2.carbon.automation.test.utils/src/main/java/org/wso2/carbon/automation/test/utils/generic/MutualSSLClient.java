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

package org.wso2.carbon.automation.test.utils.generic;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;

public class MutualSSLClient {

    private KeyStore keyStore;
    private KeyStore trustStore;
    private String keyStorePassword;
    private String KEY_STORE_TYPE = "JKS";
    private String TRUST_STORE_TYPE = "JKS";
    private String KEY_MANAGER_TYPE = "SunX509";
    private String TRUST_MANAGER_TYPE = "SunX509";
    private String PROTOCOL  = "SSLv3";

    private String backendURL;
    private String method;
    private String contentType;
    private String soapAction;
    private HttpsURLConnection httpsURLConnection;
    private SSLSocketFactory sslSocketFactory;

    public MutualSSLClient ( String backendURL, String method, String contentType, String soapAction) {
        this.backendURL = backendURL;
        this.method = method;
        this.contentType = contentType;
        this.soapAction = soapAction;
    }

    public void loadKeyStore ( String keyStorePath, String keyStorePassowrd)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {

        this.keyStorePassword = keyStorePassowrd;
        keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(new FileInputStream(keyStorePath),
                keyStorePassowrd.toCharArray());
    }

    public void loadTrustStore ( String trustStorePath, String trustStorePassowrd)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {

        trustStore = KeyStore.getInstance(TRUST_STORE_TYPE);
        trustStore.load(new FileInputStream(trustStorePath),
                trustStorePassowrd.toCharArray());
    }

    public void initMutualSSLConnection()
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException, IOException {

        KeyManagerFactory kmfKey = KeyManagerFactory.getInstance(KEY_MANAGER_TYPE);
        kmfKey.init(keyStore,keyStorePassword.toCharArray());

        TrustManagerFactory kmfCA = TrustManagerFactory.getInstance(TRUST_MANAGER_TYPE);
        kmfCA.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
        sslContext.init(kmfKey.getKeyManagers(),kmfCA.getTrustManagers(), null);

        sslSocketFactory = (SSLSocketFactory)sslContext.getSocketFactory();

    }

    public String sendReceive(String message) throws IOException {

        URL url = new URL(backendURL);

        httpsURLConnection = (HttpsURLConnection)url.openConnection();
        httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
        httpsURLConnection.setDoOutput(true);
        httpsURLConnection.setDoInput(true);
        httpsURLConnection.setRequestMethod(method);
        httpsURLConnection.setRequestProperty("Content-type", contentType);
        httpsURLConnection.setRequestProperty("SOAPAction", soapAction);

        OutputStream reqStream = getHttpsURLConnection().getOutputStream();
        reqStream.write(message.getBytes());

        InputStream resStream = getHttpsURLConnection().getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(resStream));
        StringBuilder out = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }

        reader.close();
        resStream.close();

        return out.toString();
    }

    public String getKeyStoreType() {
        return KEY_STORE_TYPE;
    }

    public void setKeyStoreType(String KEY_STORE_TYPE) {
        this.KEY_STORE_TYPE = KEY_STORE_TYPE;
    }

    public String getTrustStoreType() {
        return TRUST_STORE_TYPE;
    }

    public void setTrustStoreType(String TRUST_STORE_TYPE) {
        this.TRUST_STORE_TYPE = TRUST_STORE_TYPE;
    }


    public String getKeyManagerType() {
        return KEY_MANAGER_TYPE;
    }

    public void settKeyManagerType(String KEY_MANAGER_TYPE) {
        this.KEY_MANAGER_TYPE = KEY_MANAGER_TYPE;
    }

    public String getTrustManagerType() {
        return TRUST_MANAGER_TYPE;
    }

    public void gsetTrustManagerType(String TRUST_MANAGER_TYPE) {
        this.TRUST_MANAGER_TYPE = TRUST_MANAGER_TYPE;
    }

    public HttpsURLConnection getHttpsURLConnection() {
        return httpsURLConnection;
    }

    public String getProtocol() {
        return PROTOCOL;
    }

    public void setProtocol(String PROTOCOL) {
        this.PROTOCOL = PROTOCOL;
    }
}
