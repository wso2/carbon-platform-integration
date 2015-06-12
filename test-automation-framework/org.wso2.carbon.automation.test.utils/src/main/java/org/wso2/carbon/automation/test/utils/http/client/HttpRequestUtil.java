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

import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A utility for handling HTTP requests
 */
@SuppressWarnings("unused")
public class HttpRequestUtil {
    /**
     * Sends an HTTP GET request to a url
     *
     * @param endpoint          - The URL of the server. (Example: " http://www.yahoo.com/search")
     * @param requestParameters - all the request parameters (Example: "param1=val1&param2=val2").
     *                          Note: This method will add the question mark (?) to the request - DO NOT add it yourself
     * @return - The response from the end point
     * @throws java.io.IOException If an error occurs while sending the GET request
     */
    public static HttpResponse sendGetRequest(String endpoint,
                                              String requestParameters) throws IOException {
            String urlStr = endpoint;
            if (requestParameters != null && requestParameters.length() > 0) {
                urlStr += "?" + requestParameters;
            }
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
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
            }
            return new HttpResponse(sb.toString(), conn.getResponseCode());
    }

    /**
     * Reads data from the data reader and posts it to a server via POST request.
     * data - The data you want to send
     * endpoint - The server's address
     * output - writes the server's response to output
     *
     * @param data     Data to be sent
     * @param endpoint The endpoint to which the data has to be POSTed
     * @param output   Output
     * @throws AutomationFrameworkException If an error occurs while POSTing
     */
    public static void sendPostRequest(Reader data, URL endpoint, Writer output) throws AutomationFrameworkException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            try {
                urlConnection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new AutomationFrameworkException("Shouldn't happen: HttpURLConnection doesn't " +
                                                       "support POST?? " + e.getMessage(), e);
            }
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");
            OutputStream out = urlConnection.getOutputStream();
            try {
                Writer writer = new OutputStreamWriter(out, "UTF-8");
                pipe(data, writer);
                writer.close();
            } catch (IOException e) {
                throw new AutomationFrameworkException("IOException while posting data " + e.getMessage(), e);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            InputStream in = urlConnection.getInputStream();
            try {
                Reader reader = new InputStreamReader(in, "UTF-8");
                pipe(reader, output);
                reader.close();
            } catch (IOException e) {
                throw new AutomationFrameworkException("IOException while reading response " + e.getMessage(), e);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException e) {
            throw new AutomationFrameworkException("Connection error (is server running at "
                                                   + endpoint + " ?): " + e.getMessage(), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static HttpResponse doPost(URL endpoint, String body) throws AutomationFrameworkException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            try {
                urlConnection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new AutomationFrameworkException("Shouldn't happen: HttpURLConnection doesn't support POST?? "
                                                       + e.getMessage(), e);
            }
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            OutputStream out = urlConnection.getOutputStream();
            try {
                Writer writer = new OutputStreamWriter(out, "UTF-8");
                writer.write(body);
                writer.close();
            } catch (IOException e) {
                throw new AutomationFrameworkException("IOException while posting data " + e.getMessage(), e);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), Charset.defaultCharset()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
            }
            Iterator<String> itr = urlConnection.getHeaderFields().keySet().iterator();
            Map<String, String> headers = new HashMap();
            while (itr.hasNext()) {
                String key = itr.next();
                if (key != null) {
                    headers.put(key, urlConnection.getHeaderField(key));
                }
            }
            return new HttpResponse(sb.toString(), urlConnection.getResponseCode(), headers);
        } catch (IOException e) {
            throw new AutomationFrameworkException("Connection error (is server running at "
                                                   + endpoint + " ?): " + e.getMessage(), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static HttpResponse doPost(URL endpoint, String postBody, Map<String, String> headers)
            throws AutomationFrameworkException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            try {
                urlConnection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new AutomationFrameworkException("Shouldn't happen: HttpURLConnection doesn't" +
                                                       " support POST?? " + e.getMessage(), e);
            }
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            //setting headers

            for (Map.Entry<String, String> e : headers.entrySet()) {
                urlConnection.setRequestProperty(e.getKey(), e.getValue());
            }

            OutputStream out = urlConnection.getOutputStream();
            try {
                Writer writer = new OutputStreamWriter(out, "UTF-8");
                writer.write(postBody);
                writer.close();
            } catch (IOException e) {
                throw new AutomationFrameworkException("IOException while posting data " + e.getMessage(), e);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), Charset.defaultCharset()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
            }
            Iterator<String> itr = urlConnection.getHeaderFields().keySet().iterator();
            Map<String, String> responseHeaders = new HashMap();
            while (itr.hasNext()) {
                String key = itr.next();
                if (key != null) {
                    responseHeaders.put(key, urlConnection.getHeaderField(key));
                }
            }
            return new HttpResponse(sb.toString(), urlConnection.getResponseCode(), responseHeaders);
        } catch (IOException e) {
            throw new AutomationFrameworkException("Connection error (is server running at "
                                                   + endpoint + " ?): " + e.getMessage() , e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static HttpResponse doGet(String endpoint, Map<String, String> headers) throws IOException {
        HttpResponse httpResponse;
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setReadTimeout(30000);
            //setting headers
            for (Map.Entry<String,String> e : headers.entrySet()) {
                conn.setRequestProperty(e.getKey(), e.getValue());
            }

            conn.connect();

            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                httpResponse = new HttpResponse(sb.toString(), conn.getResponseCode());
                httpResponse.setResponseMessage(conn.getResponseMessage());
            } catch (IOException ignored) {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), Charset.defaultCharset()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                httpResponse = new HttpResponse(sb.toString(), conn.getResponseCode());
                httpResponse.setResponseMessage(conn.getResponseMessage());
            } finally {
                if (rd != null) {
                    rd.close();
                }
            }
            return httpResponse;
    }

    /**
     * Reads data from the data reader and posts it to a server via POST request.
     * data - The data you want to send
     * endpoint - The server's address
     * output - writes the server's response to output
     * contentType   content type of the message
     *
     * @param data        Data to be sent
     * @param endpoint    The endpoint to which the data has to be POSTed
     * @param output      Output
     * @param contentType content type of the message
     * @throws AutomationFrameworkException If an error occurs while POSTing
     */
    public static void sendPostRequest(Reader data, URL endpoint, Writer output, String contentType)
            throws AutomationFrameworkException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            try {
                urlConnection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new AutomationFrameworkException("Shouldn't happen: HttpURLConnection doesn't support POST?? "
                                                       + e.getMessage(), e);
            }
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setRequestProperty("Content-type", contentType);
            OutputStream out = urlConnection.getOutputStream();
            try {
                Writer writer = new OutputStreamWriter(out, "UTF-8");
                pipe(data, writer);
                writer.close();
            } catch (IOException e) {
                throw new AutomationFrameworkException("IOException while posting data " + e.getMessage(), e);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            InputStream in = urlConnection.getInputStream();
            try {
                Reader reader = new InputStreamReader(in, Charset.defaultCharset());
                pipe(reader, output);
                reader.close();
            } catch (IOException e) {
                throw new AutomationFrameworkException("IOException while reading response " + e.getMessage(), e);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException e) {
            throw new AutomationFrameworkException("Connection error (is server running at "
                                                   + endpoint + " ?): " + e.getMessage(), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /**
     * Reads data from the data reader and posts it to a server via PUT request.
     * data - The data you want to send
     * endpoint - The server's address
     * output - writes the server's response to output
     * contentType   content type of the message
     *
     * @param data        Data to be sent
     * @param endpoint    The endpoint to which the data has to be POSTed
     * @param output      Output
     * @param contentType content type of the message
     * @throws AutomationFrameworkException If an error occurs while POSTing
     */
    public static void sendPutRequest(Reader data, URL endpoint, Writer output, String contentType)
            throws AutomationFrameworkException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) endpoint.openConnection();
            try {
                urlConnection.setRequestMethod("PUT");
            } catch (ProtocolException e) {
                throw new AutomationFrameworkException("Shouldn't happen: HttpURLConnection doesn't " +
                                                       "support PUT?? " + e.getMessage(), e);
            }
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setRequestProperty("Content-type", contentType);
            OutputStream out = urlConnection.getOutputStream();
            try {
                Writer writer = new OutputStreamWriter(out, "UTF-8");
                pipe(data, writer);
                writer.close();
            } catch (IOException e) {
                throw new AutomationFrameworkException("IOException while posting data " + e.getMessage(), e);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            InputStream in = urlConnection.getInputStream();
            try {
                Reader reader = new InputStreamReader(in, Charset.defaultCharset());
                pipe(reader, output);
                reader.close();
            } catch (IOException e) {
                throw new AutomationFrameworkException("IOException while reading response " + e.getMessage(), e);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException e) {
            throw new AutomationFrameworkException("Connection error (is server running at "
                                                   + endpoint + " ?): " + e.getMessage(), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /**
     * Pipes everything from the reader to the writer via a buffer
     *
     * @param reader Reader
     * @param writer Writer
     * @throws java.io.IOException If piping fails
     */
    private static void pipe(Reader reader, Writer writer) throws IOException {
        char[] buf = new char[1024];
        int read;
        while ((read = reader.read(buf)) >= 0) {
            writer.write(buf, 0, read);
        }
        writer.flush();
    }
}

