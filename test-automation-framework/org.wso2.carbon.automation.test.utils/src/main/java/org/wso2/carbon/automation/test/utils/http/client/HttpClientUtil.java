/*
 * Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.automation.test.utils.http.client;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClientUtil {
    private static final Log log = LogFactory.getLog(HttpClientUtil.class);
    private static final int connectionTimeOut = 30000;

    public OMElement get(String endpoint) throws Exception {
        log.info("Endpoint : " + endpoint);
        HttpURLConnection httpCon = null;
        String xmlContent = null;
        int responseCode = -1;
        try {
            URL url = new URL(endpoint);
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setConnectTimeout(connectionTimeOut);
            InputStream in = httpCon.getInputStream();
            xmlContent = getStringFromInputStream(in);
            responseCode = httpCon.getResponseCode();
            in.close();
        } catch (Exception e) {
            log.error("Failed to get the response " + e);
            throw new Exception("Failed to get the response :" + e);
        } finally {
            if (httpCon != null) {
                httpCon.disconnect();
            }
        }
        Assert.assertEquals(responseCode, 200, "Response code not 200");
        if (xmlContent != null) {
            try {
                return AXIOMUtil.stringToOM(xmlContent);
            } catch (XMLStreamException e) {
                log.error("Error while processing response to OMElement" + e);
                throw new XMLStreamException("Error while processing response to OMElement" + e);
            }
        } else {
            return null;
        }
    }

    public OMElement getWithContentType(String endpoint, String params, String contentType)
            throws Exception {
        log.info("Endpoint : " + endpoint);
        HttpURLConnection httpCon = null;
        String xmlContent = null;
        int responseCode = -1;
        try {
            URL url = new URL(endpoint);
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setConnectTimeout(connectionTimeOut);
            httpCon.setRequestProperty("Content-type", contentType);
            httpCon.setRequestMethod("GET");
            httpCon.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(params);
            out.close();
            InputStream in = httpCon.getInputStream();
            xmlContent = getStringFromInputStream(in);
            responseCode = httpCon.getResponseCode();
            in.close();
        } catch (Exception e) {
            log.error("Failed to get the response " + e);
            throw new Exception("Failed to get the response :" + e);
        } finally {
            if (httpCon != null) {
                httpCon.disconnect();
            }
        }
        Assert.assertEquals(responseCode, 200, "Response code not 200");
        if (xmlContent != null) {
            try {
                return AXIOMUtil.stringToOM(xmlContent);
            } catch (XMLStreamException e) {
                log.error("Error while processing response to OMElement" + e);
                throw new XMLStreamException("Error while processing response to OMElement" + e);
            }
        } else {
            return null;
        }
    }

    public void delete(String endpoint, String params) throws Exception {
        log.info("Endpoint : " + endpoint);
        HttpURLConnection httpCon = null;
        int responseCode = -1;
        try {
            URL url = new URL(endpoint + "?" + params);
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setConnectTimeout(connectionTimeOut);
            httpCon.setRequestMethod("DELETE");
            responseCode = httpCon.getResponseCode();
        } catch (Exception e) {
            log.error("Failed to get the response " + e);
            throw new Exception("Failed to get the response :" + e);
        } finally {
            if (httpCon != null) {
                httpCon.disconnect();
            }
        }
        Assert.assertEquals(responseCode, 202, "Response Code not 202");
    }

    public void post(String endpoint, String params) throws Exception {
        log.info("Endpoint : " + endpoint);
        HttpURLConnection httpCon = null;
        int responseCode = -1;
        try {
            URL url = new URL(endpoint);
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setConnectTimeout(connectionTimeOut);
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(params);
            out.close();
            responseCode = httpCon.getResponseCode();
            httpCon.getInputStream().close();
        } catch (Exception e) {
            log.error("Failed to get the response " + e);
            throw new Exception("Failed to get the response :" + e);
        } finally {
            if (httpCon != null) {
                httpCon.disconnect();
            }
        }
        Assert.assertEquals(responseCode, 202, "Response Code not 202");
    }

    public int postWithContentType(String endpoint, String params, String contentType)
            throws Exception {
        log.info("Endpoint : " + endpoint);
        HttpURLConnection httpCon = null;
        int responseCode = -1;
        try {
            URL url = new URL(endpoint);
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setConnectTimeout(connectionTimeOut);
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            httpCon.setRequestProperty("Content-type", contentType);
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(params);
            out.close();
            responseCode = httpCon.getResponseCode();
            httpCon.getInputStream().close();
        } catch (Exception e) {
            log.error("Failed to get the response " + e);
            throw new Exception("Failed to get the response :" + e);
        } finally {
            if (httpCon != null) {
                httpCon.disconnect();
            }
        }
        return responseCode;
    }

    public void put(String endpoint, String params) throws Exception {
        log.info("Endpoint : " + endpoint);
        HttpURLConnection httpCon = null;
        int responseCode = -1;
        try {
            URL url = new URL(endpoint);
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setConnectTimeout(connectionTimeOut);
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            httpCon.setRequestProperty("Content-Length", String.valueOf(params.length()));
            httpCon.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(params);
            out.close();
            responseCode = httpCon.getResponseCode();
            httpCon.getInputStream().close();
        } catch (Exception e) {
            log.error("Failed to get the response " + e);
            throw new Exception("Failed to get the response :" + e);
        } finally {
            if (httpCon != null) {
                httpCon.disconnect();
            }
        }
        Assert.assertEquals(responseCode, 202, "Response Code not 202");
    }

    public void patch(String endpoint, String params) throws Exception {
        log.info("Endpoint : " + endpoint);
        HttpURLConnection httpCon = null;
        int responseCode = -1;
        try {
            URL url = new URL(endpoint);
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setConnectTimeout(connectionTimeOut);
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            httpCon.setRequestProperty("Content-Length", String.valueOf(params.length()));
            httpCon.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            httpCon.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(params);
            out.close();
            responseCode = httpCon.getResponseCode();
            httpCon.getInputStream().close();
        } catch (Exception e) {
            log.error("Failed to get the response " + e);
            throw new Exception("Failed to get the response :" + e);
        } finally {
            if (httpCon != null) {
                httpCon.disconnect();
            }
        }
        Assert.assertEquals(responseCode, 202, "Response Code not 202");
    }

    private static String getStringFromInputStream(InputStream in) throws Exception {
        InputStreamReader reader = new InputStreamReader(in);
        char[] buff = new char[1024];
        int i;
        StringBuffer retValue = new StringBuffer();
        try {
            while ((i = reader.read(buff)) > 0) {
                retValue.append(new String(buff, 0, i));
            }
        } catch (Exception e) {
            log.error("Failed to get the response " + e);
            throw new Exception("Failed to get the response :" + e);
        }
        return retValue.toString();
    }
}

