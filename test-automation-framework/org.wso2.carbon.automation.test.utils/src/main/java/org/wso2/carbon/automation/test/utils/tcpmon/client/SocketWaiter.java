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
package org.wso2.carbon.automation.test.utils.tcpmon.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * wait for incoming connections, spawn a connection thread when
 * stuff comes in.
 */
class SocketWaiter extends Thread {

	private static final Log log = LogFactory.getLog(SocketWaiter.class);

	private ServerSocket sSocket = null;

	private TCPMonListener listener;

	private int listenPort;

	private boolean pleaseStop = false;

	public SocketWaiter(TCPMonListener listener, int listenPort) {
		this.listener = listener;
		this.listenPort = listenPort;
		start();
	}

	/**
	 * Method run
	 */
	public void run() {
		try {
			log.info("waiting for connection....");
			sSocket = new ServerSocket(listenPort);
			for (; ; ) {
				Socket inSocket = sSocket.accept();
				if (pleaseStop) {
					break;
				}
				new Connection(listener, inSocket);
				inSocket = null;
			}
		} catch (Exception exp) {
			if (!"socket closed".equals(exp.getMessage())) {
				listener.stop();
			}
		}
	}

	/**
	 * force a halt by connecting to self and then closing the server socket
	 */
	public void halt() {
		try {
			pleaseStop = true;
			new Socket("127.0.0.1", listenPort);
			if (sSocket != null) {
				sSocket.close();
			}
		} catch (Exception e) {
			log.error("Error while closing socket : " + e.getMessage());
		}
	}
}
