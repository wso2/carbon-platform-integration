/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.automation.remotecoverage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;

/**
 * This class starts a socket server to collect coverage from agents that run
 * in output mode <code>tcpclient</code>. The collected data is dumped to a
 * local file.
 */
public class CoverageDataServer {
    private Log log = LogFactory.getLog(CoverageDataServer.class);
    private int port;
    private String fileToDumpCoverage;
    private String hostName;


    public CoverageDataServer(int port, String fileToDumpCoverage, String hostName)
            throws UnknownHostException {
        this.port = port;
        this.fileToDumpCoverage = fileToDumpCoverage;
        if (hostName == null) {
            this.hostName = InetAddress.getLocalHost().getHostAddress();
        } else {
            this.hostName = hostName;
        }
    }

    public void startServer() {
        try {
            final ExecutionDataWriter fileWriter = new ExecutionDataWriter(new FileOutputStream(fileToDumpCoverage));
            final ServerSocket server = new ServerSocket(port, 0, InetAddress.getByName(hostName));

            while (true) {
                log.info("Starting coverage dump server on host " + hostName + " and" + " port " + port);
                log.info("Dumping coverage to : " + fileToDumpCoverage);
                final Handler handler = new Handler(server.accept(), fileWriter);
                new Thread(handler).start();
            }

        } catch (IOException e) {
            log.error("Error while starting coverage dump server", e);
        }
    }

    private static class Handler implements Runnable, ISessionInfoVisitor,
                                            IExecutionDataVisitor {

        private final Socket socket;
        private final RemoteControlReader reader;
        private final ExecutionDataWriter fileWriter;
        private Log log = LogFactory.getLog(Handler.class);

        Handler(final Socket socket, final ExecutionDataWriter fileWriter) throws IOException {
            this.socket = socket;
            this.fileWriter = fileWriter;

            // Just send a valid header:
            new RemoteControlWriter(socket.getOutputStream());

            reader = new RemoteControlReader(socket.getInputStream());
            reader.setSessionInfoVisitor(this);
            reader.setExecutionDataVisitor(this);
        }

        public void run() {
            try {
                while (reader.read()) {
                }
                socket.close();
                synchronized (fileWriter) {
                    fileWriter.flush();
                }
            } catch (final IOException e) {
                log.error("unable to read or close coverage sever socket ", e);
            }
        }

        public void visitSessionInfo(final SessionInfo info) {
            log.info("Retrieving execution Data for session: " + info.getId());
            synchronized (fileWriter) {
                fileWriter.visitSessionInfo(info);
            }
        }

        public void visitClassExecution(final ExecutionData data) {
            synchronized (fileWriter) {
                fileWriter.visitClassExecution(data);
            }
        }
    }
}
