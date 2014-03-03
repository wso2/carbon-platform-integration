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
package org.wso2.carbon.automation.extentions.servers.proxyserver;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

public class ProxyConnection extends Thread {
    Socket fromClient;
    String host;
    int port;
    long timeout;

    ProxyConnection(Socket s, String host, int port, long timeout) {
        fromClient = s;
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public void run() {
        InputStream clientIn;
        OutputStream clientOut;
        InputStream serverIn;
        OutputStream serverOut;
        Socket toServer;
        int r0 = - 1, r1 = - 1, ch = - 1, i = - 1;
        long time0 = new Date().getTime();
        long time1 = new Date().getTime();
        try {
            toServer = new Socket(host, port);
            Proxy.display("open connection to:" + toServer + "(timeout=" + timeout + " ms)");
            clientIn = fromClient.getInputStream();
            clientOut = new BufferedOutputStream(fromClient.getOutputStream());
            serverIn = toServer.getInputStream();
            serverOut = new BufferedOutputStream(toServer.getOutputStream());
            while (r0 != 0 || r1 != 0 || (time1 - time0) <= timeout) {
                while ((r0 = clientIn.available()) > 0) {
                    Proxy.println("");
                    Proxy.println("<<<" + r0 + " bytes from client");
                    Proxy.display("");
                    Proxy.display("<<<" + r0 + " bytes from client");
                    for (i = 0; i < r0; i++) {
                        ch = clientIn.read();
                        if (ch != - 1) {
                            serverOut.write(ch);
                            Proxy.print(ch);
                        } else {
                            Proxy.display("client stream closed");
                        }
                    }
                    time0 = new Date().getTime();
                    serverOut.flush();
                }
                while ((r1 = serverIn.available()) > 0) {
                    Proxy.println("");
                    Proxy.println(">>>" + r1 + " bytes from server");
                    Proxy.display("");
                    Proxy.display(">>>" + r1 + " bytes from server");
                    for (i = 0; i < r1; i++) {
                        ch = serverIn.read();
                        if (ch != - 1) {
                            clientOut.write(ch);
                            Proxy.print(ch);
                        } else {
                            Proxy.display("server stream closed");
                        }
                    }
                    time0 = new Date().getTime();
                    clientOut.flush();
                }
                if (r0 == 0 && r1 == 0) {
                    time1 = new Date().getTime();
                    Thread.sleep(100);
                    //Proxy.display("waiting:"+(time1-time0)+" ms");
                }
            }
            clientIn.close();
            clientOut.close();
            serverIn.close();
            serverOut.close();
            fromClient.close();
            toServer.close();
            Proxy.quit(time1 - time0);
        } catch (Throwable t) {
            Proxy.display("i=" + i + " ch=" + ch);
            t.printStackTrace(System.err);
        }
    }
}

