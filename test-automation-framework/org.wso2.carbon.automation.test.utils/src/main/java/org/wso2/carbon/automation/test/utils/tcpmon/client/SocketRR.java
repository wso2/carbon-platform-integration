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
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * this class handles the pumping of data from the incoming socket to the
 * outgoing socket
 */
class SocketRR extends Thread {

	private static final Log log = LogFactory.getLog(SocketRR.class);

	private Socket inSocket = null;

	private Socket outSocket = null;

	private StringBuffer message;

	private InputStream inputStream = null;

	private OutputStream outputStream = null;

	private boolean xmlFormat;

	private volatile boolean done = false;

	private volatile long elapsed = 0;

	private Map<Integer, ConnectionData> connectionData = null;

	private int index = 0;

	private String type = null;

	private Connection connection = null;

	private SlowLinkSimulator slowLink;

	public SocketRR(Connection connection, Socket inputSocket,
	                InputStream inputStream, Socket outputSocket,
	                OutputStream outputStream, StringBuffer message,
	                boolean format, Map<Integer, ConnectionData> connectionData, int index,
	                final String type, SlowLinkSimulator slowLink) {
		this.inSocket = inputSocket;
		this.inputStream = inputStream;
		this.outSocket = outputSocket;
		this.outputStream = outputStream;
		this.message = message;
		xmlFormat = format;
		this.connectionData = connectionData;
		this.index = index;
		this.type = type;
		this.connection = connection;
		this.slowLink = slowLink;

		start();
	}

	/**
	 * Method isDone
	 *
	 * @return boolean
	 */
	public boolean isDone() {
		return done;
	}

	public String getElapsed() {
		return String.valueOf(elapsed);
	}

	/**
	 * Method run
	 */
	public void run() {
		try {
			byte[] buffer = new byte[4096];
			byte[] tmpbuffer = new byte[8192];
			int saved = 0;
			int len;
			int i1, i2;
			int i;
			int reqSaved = 0;
			int tabWidth = 3;
			boolean atMargin = true;
			int thisIndent = -1, nextIndent = -1, previousIndent = -1;
			if (connectionData != null) {
				String tmpStr = connectionData.get(index).getRequest();
				if (!"".equals(tmpStr)) {
					reqSaved = tmpStr.length();
				}
			}
			long start = System.currentTimeMillis();
			a:
			for (; ; ) {

				elapsed = System.currentTimeMillis() - start;

				if (done) {
					break;
				}

				len = buffer.length;

				// Used to be 1, but if we block it doesn't matter
				// however 1 will break with some servers, including apache
				if (len == 0) {
					len = buffer.length;
				}
				if (saved + len > buffer.length) {
					len = buffer.length - saved;
				}
				int len1 = 0;
				while (len1 == 0) {
					try {
						len1 = inputStream.read(buffer, saved, len);
					} catch (Exception ex) {
						if (done && (saved == 0)) {
							break a;
						}
						len1 = -1;
						break;
					}
				}
				len = len1;
				if ((len == -1) && (saved == 0)) {
					break;
				}
				if (len == -1) {
					done = true;
				}

				// No matter how we may (or may not) format it, send it
				// on unformatted - we don't want to mess with how its
				// sent to the other side, just how its displayed
				if ((outputStream != null) && (len > 0)) {
					slowLink.pump(len);
					outputStream.write(buffer, saved, len);
				}

				if ((connectionData != null) && (reqSaved < 50)) {
					String old = connectionData.get(index).getRequest();

					old = old + new String(buffer, saved, len, Charset.defaultCharset());
					if (old.length() > 50) {
						old = old.substring(0, 50);
					}
					reqSaved = old.length();
					if ((i = old.indexOf('\n')) > 0) {
						old = old.substring(0, i - 1);
						reqSaved = 50;
					}
					connectionData.get(index).setRequest(old);
				}

				if (xmlFormat) {

					// Do XML Formatting
					boolean inXML = false;
					int bufferLen = saved;
					if (len != -1) {
						bufferLen += len;
					}
					i1 = 0;
					i2 = 0;
					saved = 0;
					for (; i1 < bufferLen; i1++) {

						// Except when we're at EOF, saved last char
						if ((len != -1) && (i1 + 1 == bufferLen)) {
							saved = 1;
							break;
						}
						thisIndent = -1;
						if ((buffer[i1] == '<')
						    && (buffer[i1 + 1] != '/')) {
							previousIndent = nextIndent++;
							thisIndent = nextIndent;
							inXML = true;
						}
						if ((buffer[i1] == '<')
						    && (buffer[i1 + 1] == '/')) {
							if (previousIndent > nextIndent) {
								thisIndent = nextIndent;
							}
							previousIndent = nextIndent--;
							inXML = true;
						}
						if ((buffer[i1] == '/')
						    && (buffer[i1 + 1] == '>')) {
							previousIndent = nextIndent--;
							inXML = true;
						}
						if (thisIndent != -1) {
							if (thisIndent > 0) {
								tmpbuffer[i2++] = (byte) '\n';
							}
							for (i = tabWidth * thisIndent; i > 0; i--) {
								tmpbuffer[i2++] = (byte) ' ';
							}
						}
						atMargin = ((buffer[i1] == '\n')
						            || (buffer[i1] == '\r'));
						if (!inXML || !atMargin) {
							tmpbuffer[i2++] = buffer[i1];
						}
					}
					message.append(new String(tmpbuffer, 0, i2, Charset.defaultCharset()));

					// Shift saved bytes to the beginning
					for (i = 0; i < saved; i++) {
						buffer[i] = buffer[bufferLen - saved + i];
					}
				} else {
					message.append(new String(buffer, 0, len, Charset.defaultCharset()));
				}
			}

		} catch (Exception e) {
			log.error("Error while SocketRR.run() : " + e.getMessage());
		} finally {
			done = true;
			try {
				if (outputStream != null) {
					outputStream.flush();
					if (null != outSocket) {
						outSocket.shutdownOutput();
					} else {
						outputStream.close();
					}
					outputStream = null;
				}
			} catch (Exception e) {
				log.error("Error while closing outSocket and outputStream : " + e.getMessage());
			}
			try {
				if (inputStream != null) {
					if (inSocket != null) {
						inSocket.shutdownInput();
					} else {
						inputStream.close();
					}
					inputStream = null;
				}
			} catch (Exception e) {
				log.error("Error while closing inSocket and inputStream : " + e.getMessage());
			}
			connection.wakeUp();
		}
	}

	/**
	 * Method halt
	 */
	public void halt() {
		try {
			if (inSocket != null) {
				inSocket.close();
			}
			if (outSocket != null) {
				outSocket.close();
			}
			inSocket = null;
			outSocket = null;
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			inputStream = null;
			outputStream = null;
			done = true;
		} catch (Exception e) {
			log.error("Error while closing SocketRR : " + e.getMessage());
		}
	}
}
