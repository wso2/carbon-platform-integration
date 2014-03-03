/*
 *  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.automation.test.utils.generic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class GenericJSONClient {

    public static final Log log = LogFactory.getLog(GenericJSONClient.class);

    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";

    public JSONObject doGet(String endpoint, String query, String contentType) throws Exception {
        String charset = "UTF-8";
        OutputStream os = null;
        InputStream is = null;
        try {
            if (contentType == null || "".equals(contentType)) {
                contentType = "application/json";
            }
            URLConnection conn = new URL(endpoint).openConnection();
            conn.setRequestProperty(GenericJSONClient.HEADER_CONTENT_TYPE, contentType);
            conn.setRequestProperty(GenericJSONClient.HEADER_ACCEPT_CHARSET, charset);
            conn.setRequestProperty("Content-Length", "1000");
            conn.setReadTimeout(30000);
            System.setProperty("java.net.preferIPv4Stack" , "true");
            conn.setRequestProperty("Connection", "close");

            conn.setDoOutput(true);
            os = conn.getOutputStream();
            os.write(query.getBytes(charset));

            is = conn.getInputStream();
            String out = null;
            if (is != null) {
                StringBuilder source = new StringBuilder();
                byte[] data = new byte[1024];
                int len;
                while ((len = is.read(data)) != -1) {
                    source.append(new String(data, 0, len));
                }
                out = source.toString();
            }
            return new JSONObject(out);
        } catch (IOException e) {
                throw new Exception("Error occurred while executing the GET operation", e);
        } catch (JSONException e) {
            throw new Exception("Error occurred while parsing the response to a JSONObject", e);
        } finally {
            assert os != null;
            os.flush();
            os.close();
            assert is != null;
            is.close();

        }
    }

    public void doPost(String endpoint, String queryString, String contentType) throws Exception {
        String charset = "UTF-8";
        try {
            if (contentType == null || "".equals(contentType)) {
                contentType = "application/json";
            }
            HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
            conn.setRequestProperty(GenericJSONClient.HEADER_CONTENT_TYPE, contentType);
            conn.setRequestProperty(GenericJSONClient.HEADER_ACCEPT_CHARSET, charset);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(queryString.getBytes(charset));
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            conn.getInputStream().close();

            if (responseCode != 202) {
                throw new Exception("Server responded with an inappropriate response code : '" + 
                        responseCode + "'");
            }
        } catch (IOException e) {
            throw new Exception("Error occurred while executing the GET operation", e);
        }
    }

}
