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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TCPMon Listener class
 */
public class TCPMonListener {

	private static final Log log = LogFactory.getLog(TCPMonListener.class);

	private int listenPort;

	private String targetHost;

	private int targetPort;

	private boolean isProxy = false;

	private boolean xmlFormatBox = true;

	private Map<Integer, ConnectionData> connectionData = new HashMap<Integer, ConnectionData>(0);

	private SocketWaiter socketWaiter = null;

	private String HTTPProxyHost = null;

	private int HTTPProxyPort = 80;

	private SlowLinkSimulator slowLink;

	private final List<Connection> connections = new ArrayList<Connection>(0);

	public TCPMonListener(int listenPort,
	                      String targetHost, int targetPort) {
		this(listenPort, targetHost, targetPort, false, null);
	}

	public TCPMonListener(int listenPort,
	                      String targetHost, int targetPort, boolean isProxy,
	                      int delayBytes, int delayTime) {
		this(listenPort, targetHost, targetPort, isProxy,
		     new SlowLinkSimulator(delayBytes, delayTime));
	}

	public TCPMonListener(int listenPort,
	                      String targetHost, int targetPort, boolean isProxy,
	                      SlowLinkSimulator slowLink) {

		// set the slow link to the passed down link
		if (slowLink != null) {
			this.slowLink = slowLink;
		} else {
			// or make up a no-op one.
			this.slowLink = new SlowLinkSimulator(0, 0);
		}

		this.listenPort = listenPort;
		this.targetHost = targetHost;
		this.targetPort = targetPort;
		this.isProxy = isProxy;
	}

	/**
	 * Start TCPMon listener
	 */
	public void start() {
		log.info("TCPMon listener starting....");
		socketWaiter = new SocketWaiter(this, listenPort);
	}

	/**
	 * Stop TCPMon listener
	 */
	public void stop() {
		try {
			for (Connection connection : connections) {
				connection.halt();
			}
			socketWaiter.halt();
			log.info("TCPMon listener stopped....");
		} catch (Exception e) {
			log.error("Error while TCPMon listener stopping : " + e.getMessage());
		}
	}

	/**
	 * Clear all Connections and ConnectionDatas
	 */
	public void clear() {
		try {
			for (Connection connection : connections) {
				connection.halt();
			}
			connections.clear();
			connectionData.clear();
			log.info("TCPMon listener stopped....");
		} catch (Exception e) {
			log.error("Error while TCPMon listener stopping : " + e.getMessage());
		}
	}

	public Map<Integer, ConnectionData> getConnectionData() {
		return connectionData;
	}

	List<Connection> getConnections() {
		return connections;
	}

	int getListenPort() {
		return listenPort;
	}

	String getTargetHost() {
		return targetHost;
	}

	int getTargetPort() {
		return targetPort;
	}

	boolean isProxy() {
		return isProxy;
	}

	boolean isXmlFormatBox() {
		return xmlFormatBox;
	}

	SocketWaiter getSocketWaiter() {
		return socketWaiter;
	}

	String getHTTPProxyHost() {
		return HTTPProxyHost;
	}

	int getHTTPProxyPort() {
		return HTTPProxyPort;
	}

	SlowLinkSimulator getSlowLink() {
		return slowLink;
	}
}
