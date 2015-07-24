/*
*Copyright (c) 2015,  WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import java.net.Socket;

import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;

/**
 * This class connects to a coverage agent that run in output mode
 * <code>tcpserver</code> and requests execution data. The collected data is
 * dumped to a local file.
 */
public final class CoverageDataClient {


    public void requestCoverageData(String destinationCoverageDump, int port) throws IOException {
        final FileOutputStream localFile = new FileOutputStream(destinationCoverageDump);
        final ExecutionDataWriter localWriter = new ExecutionDataWriter(localFile);
        final String hostAddress = InetAddress.getLocalHost().getHostAddress();

        // Open a socket to the coverage agent:
        final Socket socket = new Socket(InetAddress.getByName(hostAddress), port);
        final RemoteControlWriter writer = new RemoteControlWriter(socket.getOutputStream());
        final RemoteControlReader reader = new RemoteControlReader(socket.getInputStream());
        reader.setSessionInfoVisitor(localWriter);
        reader.setExecutionDataVisitor(localWriter);

        // Send a dump command and read the response:
        writer.visitDumpCommand(true, false);
        reader.read();

        socket.close();
        localFile.close();

    }

    public static void main(final String[] args) throws IOException {

    }

    private CoverageDataClient() {
    }
}

