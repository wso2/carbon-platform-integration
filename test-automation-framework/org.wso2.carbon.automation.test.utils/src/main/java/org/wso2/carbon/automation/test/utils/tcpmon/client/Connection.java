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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A connection listens to a single current connection
 */
class Connection extends Thread {

	private static final Log log = LogFactory.getLog(Connection.class);

	private TCPMonListener listener;

	private boolean active;

	private String fromHost;

	private String time;

	private StringBuffer inputText = null;

	private StringBuffer outputText = null;

	private Socket inSocket = null;

	private Socket outSocket = null;

	private SocketRR rr1 = null;

	private SocketRR rr2 = null;

	private InputStream inputStream = null;

	private String HTTPProxyHost = null;

	private int HTTPProxyPort = 80;

	private SlowLinkSimulator slowLink;

	public Connection(TCPMonListener listener) {
		this.listener = listener;
		HTTPProxyHost = listener.getHTTPProxyHost();
		HTTPProxyPort = listener.getHTTPProxyPort();
		slowLink = listener.getSlowLink();
	}

	public Connection(TCPMonListener listener, Socket socket) {
		this(listener);
		inSocket = socket;
		start();
	}

	public Connection(TCPMonListener listener, InputStream in) {
		this(listener);
		inputStream = in;
		start();
	}

	/**
	 * Method run
	 */
	public void run() {
		try {
			active = true;
			HTTPProxyHost = System.getProperty("http.proxyHost");
			if ((HTTPProxyHost != null) && HTTPProxyHost.equals("")) {
				HTTPProxyHost = null;
			}
			if (HTTPProxyHost != null) {
				String tmp = System.getProperty("http.proxyPort");
				if ((tmp != null) && tmp.equals("")) {
					tmp = null;
				}
				if (tmp == null) {
					HTTPProxyPort = 80;
				} else {
					HTTPProxyPort = Integer.parseInt(tmp);
				}
			}
			if (inSocket != null) {
				fromHost = (inSocket.getInetAddress()).getHostName();
			} else {
				fromHost = "resend";
			}
			String dateformat = "yyyy-MM-dd HH:mm:ss";
			DateFormat df = new SimpleDateFormat(dateformat);
			time = df.format(new Date());
			int count = listener.getConnections().size();

			listener.getConnectionData().put(count + 1,
			                             new ConnectionData("Active", time, fromHost,
			                                                listener.getTargetHost(), "",
			                                                "")
			);
			listener.getConnections().add(this);

			inputText = new StringBuffer();

			outputText = new StringBuffer();

			String targetHost = listener.getTargetHost();
			int targetPort = listener.getTargetPort();
			int listenPort = listener.getListenPort();
			InputStream tmpIn1 = inputStream;
			OutputStream tmpOut1 = null;
			InputStream tmpIn2 = null;
			OutputStream tmpOut2 = null;
			if (tmpIn1 == null) {
				tmpIn1 = inSocket.getInputStream();
			}
			if (inSocket != null) {
				tmpOut1 = inSocket.getOutputStream();
			}
			String bufferedData = null;
			StringBuffer buf = null;
			int index = listener.getConnections().indexOf(this);
			listener.getConnectionData().get(index + 1).setInputText(inputText);
			listener.getConnectionData().get(index + 1).setOutputText(outputText);
			if (listener.isProxy() || (HTTPProxyHost != null)) {

				// Check if we're a proxy
				byte[] b = new byte[1];
				buf = new StringBuffer();
				String s;
				for (; ; ) {
					int len;
					len = tmpIn1.read(b, 0, 1);
					if (len == -1) {
						break;
					}
					s = new String(b);
					buf.append(s);
					if (b[0] != '\n') {
						continue;
					}
					break;
				}
				bufferedData = buf.toString();
				inputText.append(bufferedData);
				if (bufferedData.startsWith("GET ")
				    || bufferedData.startsWith("POST ")
				    || bufferedData.startsWith("PUT ")
				    || bufferedData.startsWith("DELETE ")) {
					int start, end;
					URL url;
					start = bufferedData.indexOf(' ') + 1;
					while (bufferedData.charAt(start) == ' ') {
						start++;
					}
					end = bufferedData.indexOf(' ', start);
					String urlString = bufferedData.substring(start, end);
					if (urlString.charAt(0) == '/') {
						urlString = urlString.substring(1);
					}
					if (listener.isProxy()) {
						url = new URL(urlString);
						targetHost = url.getHost();
						targetPort = url.getPort();
						if (targetPort == -1) {
							targetPort = 80;
						}

						listener.getConnectionData().get(index + 1).setTargetHost(targetHost);
						bufferedData = bufferedData.substring(0, start)
						               + url.getFile()
						               + bufferedData.substring(end);
					} else {
						url = new URL("http://" + targetHost + ":"
						              + targetPort + "/" + urlString);

						listener.getConnectionData().get(index + 1).setTargetHost(targetHost);
						bufferedData = bufferedData.substring(0, start)
						               + url.toExternalForm()
						               + bufferedData.substring(end);
						targetHost = HTTPProxyHost;
						targetPort = HTTPProxyPort;
					}
				}
			} else {

				//
				// Change Host: header to point to correct host
				//
				byte[] b1 = new byte[1];
				buf = new StringBuffer();
				String s1;
				String lastLine = null;
				for (; ; ) {
					int len;
					len = tmpIn1.read(b1, 0, 1);
					if (len == -1) {
						break;
					}
					s1 = new String(b1);
					buf.append(s1);
					if (b1[0] != '\n') {
						continue;
					}

					// we have a complete line
					String line = buf.toString();
					buf.setLength(0);

					// check to see if we have found Host: header
					if (line.startsWith("Host: ")) {

						// we need to update the hostname to target host
						String newHost = "Host: " + targetHost + ":"
						                 + listenPort + "\r\n";
						bufferedData = bufferedData.concat(newHost);
						break;
					}

					// add it to our headers so far
					if (bufferedData == null) {
						bufferedData = line;
					} else {
						bufferedData = bufferedData.concat(line);
					}

					// failsafe
					if (line.equals("\r\n")) {
						break;
					}
					if ("\n".equals(lastLine) && line.equals("\n")) {
						break;
					}
					lastLine = line;
				}
				if (bufferedData != null) {
					inputText.append(bufferedData);
					int idx = (bufferedData.length() < 50)
					          ? bufferedData.length()
					          : 50;
					s1 = bufferedData.substring(0, idx);
					int i = s1.indexOf('\n');
					if (i > 0) {
						s1 = s1.substring(0, i - 1);
					}
					s1 = s1 + "                           "
					     + "                       ";
					s1 = s1.substring(0, 51);

					listener.getConnectionData().get(index + 1).setRequest(s1);
				}
			}
			if (targetPort == -1) {
				targetPort = 80;
			}
			outSocket = new Socket(targetHost, targetPort);
			tmpIn2 = outSocket.getInputStream();
			tmpOut2 = outSocket.getOutputStream();
			if (bufferedData != null) {
				byte[] b = bufferedData.getBytes();
				tmpOut2.write(b);
				slowLink.pump(b.length);
			}
			boolean format = listener.isXmlFormatBox();

			// this is the channel to the endpoint
			rr1 = new SocketRR(this, inSocket, tmpIn1, outSocket, tmpOut2,
			                   inputText, format, listener.getConnectionData(),
			                   index + 1, "request:", slowLink);

			// create the response slow link from the inbound slow link
			SlowLinkSimulator responseLink =
					new SlowLinkSimulator(slowLink);

			// this is the channel from the endpoint
			rr2 = new SocketRR(this, outSocket, tmpIn2, inSocket, tmpOut1,
			                   outputText, format, null, 0, "response:",
			                   responseLink);

			while ((rr1 != null) || (rr2 != null)) {

				if (rr2 != null) {
					listener.getConnectionData().get(1 + index).setElapsedTime(rr2.getElapsed());
				}

				// Only loop as long as the connection to the target
				// machine is available - once that's gone we can stop.
				// The old way, loop until both are closed, left us
				// looping forever since no one closed the 1st one.

				if ((null != rr1) && rr1.isDone()) {
					if ((index >= 0) && (rr2 != null)) {
						listener.getConnectionData().get(1 + index).setState("Resp");
					}
					rr1 = null;
				}

				if ((null != rr2) && rr2.isDone()) {
					if ((index >= 0) && (rr1 != null)) {
						listener.getConnectionData().get(1 + index).setState("Req");
					}

					rr2 = null;
				}

				synchronized (this) {
					this.wait(100);    // Safety just incase we're not told to wake up.
				}
			}

			active = false;

			if (index >= 0) {
				listener.getConnectionData().get(1 + index).setState("Done");
			}

		} catch (Exception e) {
			StringWriter st = new StringWriter();
			PrintWriter wr = new PrintWriter(st);
			int index = listener.getConnections().indexOf(this);
			if (index >= 0) {
				listener.getConnectionData().get(1 + index).setState("Error");
			}
			e.printStackTrace(wr);
			wr.close();
			if (outputText != null) {
				outputText.append(st.toString());
			} else {
				// something went wrong before we had the output area
				log.error("Something went wring : " + st.toString());
			}
			halt();
		}
	}

	/**
	 * Connection wakeUp
	 */
	synchronized void wakeUp() {
		this.notifyAll();
	}

	/**
	 * Connection halt
	 */
	public void halt() {
		try {
			if (rr1 != null) {
				rr1.halt();
			}
			if (rr2 != null) {
				rr2.halt();
			}
			if (inSocket != null) {
				inSocket.close();
			}
			inSocket = null;
			if (outSocket != null) {
				outSocket.close();
			}
			outSocket = null;
		} catch (Exception e) {
			log.error("Error while closing connection : " + e.getMessage());
		}
	}
}
