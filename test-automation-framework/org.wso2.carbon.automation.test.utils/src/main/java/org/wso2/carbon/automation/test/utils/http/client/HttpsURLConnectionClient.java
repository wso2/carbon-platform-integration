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
package org.wso2.carbon.automation.test.utils.http.client;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class HttpsURLConnectionClient {
    public static HttpsResponse getRequest(String Uri, String requestParameters)
            throws IOException {
        if (Uri.startsWith("https://")) {
            String urlStr = Uri;
            if (requestParameters != null && requestParameters.length() > 0) {
                urlStr += "?" + requestParameters;
            }
            URL url = new URL(urlStr);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            conn.setReadTimeout(30000);
            conn.connect();
            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } catch (IOException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
                conn.disconnect();
            }
            return new HttpsResponse(sb.toString(), conn.getResponseCode());
        }
        return null;
    }

    public static HttpsResponse getWithBasicAuth(String Uri, String requestParameters,
                                                 String userName, String password)
            throws IOException {
        if (Uri.startsWith("https://")) {
            String urlStr = Uri;
            if (requestParameters != null && requestParameters.length() > 0) {
                urlStr += "?" + requestParameters;
            }
            URL url = new URL(urlStr);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String encode = new String(new org.apache.commons.codec.binary.Base64().encode((userName + ":" + password).getBytes())).replaceAll("\n", "");
            conn.setRequestProperty("Authorization", "Basic " + encode);
            conn.setDoOutput(true);
            conn.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            conn.setReadTimeout(30000);
            conn.connect();
            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
                conn.disconnect();
            }
            return new HttpsResponse(sb.toString(), conn.getResponseCode());
        }
        return null;
    }

    public static HttpsResponse postWithBasicAuth(String uri, String requestQuery, String userName,
                                                  String password) throws IOException {
        if (uri.startsWith("https://")) {
            URL url = new URL(uri);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            String encode =
                    new String(new org.apache.commons.codec.binary.Base64().encode((userName + ":" + password).getBytes())).replaceAll("\n", "");
            ;
            conn.setRequestProperty("Authorization", "Basic " + encode);
            conn.setDoOutput(true); // Triggers POST.
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", "" + Integer.toString(requestQuery.getBytes().length));
            conn.setUseCaches(false);
            conn.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(requestQuery);
            conn.setReadTimeout(10000);
            conn.connect();
            System.out.println(conn.getRequestMethod());
            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                return new HttpsResponse(sb.toString(), conn.getResponseCode());
            } catch (FileNotFoundException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
                wr.flush();
                wr.close();
                conn.disconnect();
            }
        }
        return null;
    }

    public static HttpsResponse postWithBasicAuth(String uri, String requestQuery,
                                                  String contentType,
                                                  String userName, String password)
            throws IOException {
        if (uri.startsWith("https://")) {
            URL url = new URL(uri);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            String encode =
                    new String(new org.apache.commons.codec.binary.Base64().encode((userName + ":" + password).getBytes())).replaceAll("\n", "");
            conn.setRequestProperty("Authorization", "Basic " + encode);
            conn.setDoOutput(true); // Triggers POST.
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", "" + Integer.toString(requestQuery.getBytes().length));
            conn.setUseCaches(false);
            conn.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(requestQuery);
            conn.setReadTimeout(10000);
            conn.connect();
            System.out.println(conn.getRequestMethod());
            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
                wr.flush();
                wr.close();
                conn.disconnect();
            }
            return new HttpsResponse(sb.toString(), conn.getResponseCode());
        }
        return null;
    }

    public static HttpsResponse putWithBasicAuth(String uri, String requestQuery,
                                                 String contentType, String userName,
                                                 String password) throws IOException {
        if (uri.startsWith("https://")) {
            URL url = new URL(uri);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            String encode = new String(new org.apache.commons.codec.binary.Base64().encode((userName + ":" + password).getBytes())).replaceAll("\n", "");
            ;
            conn.setRequestProperty("Authorization", "Basic " + encode);
            conn.setDoOutput(true); // Triggers POST.
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", "" + Integer.toString(requestQuery.getBytes().length));
            conn.setUseCaches(false);
            conn.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(requestQuery);
            conn.setReadTimeout(10000);
            conn.connect();
            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
                wr.flush();
                wr.close();
                conn.disconnect();
            }
            return new HttpsResponse(sb.toString(), conn.getResponseCode());
        }
        return null;
    }

    public static HttpsResponse deleteWithBasicAuth(String uri, String contentType, String userName,
                                                    String password) throws IOException {
        if (uri.startsWith("https://")) {
            URL url = new URL(uri);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            String encode = new String(new org.apache.commons.codec.binary.Base64().encode((userName + ":" + password).getBytes())).replaceAll("\n", "");
            ;
            conn.setRequestProperty("Authorization", "Basic " + encode);
            if (contentType != null) {
                conn.setRequestProperty("Content-Type", contentType);
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            conn.connect();
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
                conn.disconnect();
            }
            return new HttpsResponse(sb.toString(), conn.getResponseCode());
        }
        return null;
    }
}