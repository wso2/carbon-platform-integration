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
package org.wso2.carbon.automation.extensions.servers.proxyserver;

import java.net.ServerSocket;
import java.net.Socket;

public class Proxy {
    static int clientCount;

    public static synchronized void print(int i) {
        System.out.print((char) i);
    }

    public static synchronized void println(String s) {
        System.out.println(s);
    }

    public static synchronized void display(String s) {
        System.err.println(s);
    }

    public static synchronized void quit(long t) {
        display("...quit after waiting " + t + " ms");
        clientCount--;
    }

    public void run(int localport, String host, int port, long timeout) {
        try {
            ServerSocket sSocket = new ServerSocket(localport);
            while (true) {
                Socket cSocket = null;
                try {
                    display("listening...");
                    cSocket = sSocket.accept();
                    if (cSocket != null) {
                        incrementCount();
                        display("accepted as #" + clientCount + ":" + cSocket);
                        ProxyConnection c = new ProxyConnection(cSocket, host, port, timeout);
                        c.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
                try {
                    cSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    private static void incrementCount () {
        Proxy.clientCount++;
    }

    private static void decrementCount() {
        Proxy.clientCount--;
    }
}
