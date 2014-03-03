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
package org.wso2.carbon.automation.extensions.servers.webserver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleWebServer extends Thread {
    private volatile boolean running = true;
    private int port;
    private int expectedResponseCode;
    protected Log log = LogFactory.getLog(SimpleWebServer.class);

    public SimpleWebServer(int listenPort, int expectedResponseCode) {
        this.port = listenPort;
        this.expectedResponseCode = expectedResponseCode;
    }

    public void run() {
        ServerSocket serverSocket = null;
        Socket connectionSocket = null;
        try {
            log.info("Trying to bind to localhost on port " + Integer.toString(port) + "...");
            serverSocket = new ServerSocket(port);
            log.info("Running Simple WebServer!\n");
            connectionSocket = serverSocket.accept();
            connectionSocket.setSoTimeout(30000);
            //go in a infinite loop, wait for connections, process request, send response
            while (running) {
                log.info("\nReady, Waiting for requests...\n");
                try {
                    //this call waits/blocks until someone connects to the port we
                    BufferedReader input;
                    if (!connectionSocket.isClosed()) {
                        InetAddress client = connectionSocket.getInetAddress();
                        log.info(client.getHostName() + " connected to server.\n");
                        input = new BufferedReader(new InputStreamReader(connectionSocket.
                                getInputStream()));
                        if (input.ready()) {
                            DataOutputStream output =
                                    new DataOutputStream(connectionSocket.getOutputStream());
                            httpHandler(input, output);
                        }
                    }
                    //Prepare a outputStream. This will be used sending back our response
                    //(header + requested file) to the client.
                } catch (Exception e) { //catch any errors, and print them
                    log.info("\nError:" + e.getMessage());
                    running = false;
                }
            }
        } catch (Exception e) {
            log.error("\nFatal Error:" + e.getMessage());
            running = false;
        } finally {
            try {
                if (connectionSocket != null) {
                    connectionSocket.close();
                    serverSocket.close();
                }
            } catch (IOException e) {
                log.error("Error while shutting down server sockets", e);
            }
        }
    }

    private void httpHandler(BufferedReader input, DataOutputStream output)
            throws IOException, InterruptedException {
        String contentType;
        String tmp;
        try {
            tmp = input.readLine(); //read from the stream
            contentType = input.readLine();
            log.info(tmp);
            assert tmp != null;
            String sampleReturnResponse = "<testResponse>\n" +
                    "   <message>" + tmp.toUpperCase() + "Success</message>\n" +
                    " </testResponse>";
            output.writeBytes(constructHttpHeader(expectedResponseCode, contentType));
            output.write(sampleReturnResponse.getBytes());
        } catch (Exception e) {
            log.error("error" + e.getMessage());
        } finally {
            output.flush();
            Thread.sleep(1000);
            input.close();
            output.close();
        }
    }

    private String constructHttpHeader(int returnCode, String contentType) {
        String header = "HTTP/1.0 ";
        switch (returnCode) {
            case 200:
                header = header + "200 OK";
                break;
            case 400:
                header = header + "400 Bad Request";
                break;
            case 403:
                header = header + "403 Forbidden";
                break;
            case 404:
                header = header + "404 Not Found";
                break;
            case 500:
                header = header + "500 Internal Server Error";
                break;
            case 501:
                header = header + "501 Not Implemented";
                break;
            case 503:
                header = header + "503 Error";
                break;
            default:
                header = header + returnCode + " Testing response code";
        }
        header = header + "\r\n"; //other header fields,
        header = header + "Connection: close\r\n"; //can't handle persistent connections
        header = header + "Server: SimpleWebServer\r\n"; //server name
        header = header + contentType + "\r\n";
        header = header + "\r\n";
        return header;
    }

    public void terminate() throws IOException {
        running = false;
    }
}