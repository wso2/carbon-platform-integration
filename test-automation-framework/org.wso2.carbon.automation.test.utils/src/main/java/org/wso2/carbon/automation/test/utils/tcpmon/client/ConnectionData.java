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

/**
 * This class holds ConnectionData
 */
public class ConnectionData {

	String state;
	String time;
	String requestHost;
	String targetHost;
	String request;
	String elapsedTime;
	StringBuffer inputText;
	StringBuffer outputText;

	ConnectionData(String state, String time, String requestHost, String targetHost,
	                      String request, String elapsedTime) {
		this.state = state;
		this.time = time;
		this.requestHost = requestHost;
		this.targetHost = targetHost;
		this.request = request;
		this.elapsedTime = elapsedTime;
	}

	public String getState() {
		return state;
	}

	void setState(String state) {
		this.state = state;
	}

	public String getTime() {
		return time;
	}

	void setTime(String time) {
		this.time = time;
	}

	public String getRequestHost() {
		return requestHost;
	}

	void setRequestHost(String requestHost) {
		this.requestHost = requestHost;
	}

	public String getTargetHost() {
		return targetHost;
	}

	void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}

	public String getRequest() {
		return request;
	}

	void setRequest(String request) {
		this.request = request;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public StringBuffer getInputText() {
		return inputText;
	}

	void setInputText(StringBuffer inputText) {
		this.inputText = inputText;
	}

	public StringBuffer getOutputText() {
		return outputText;
	}

	void setOutputText(StringBuffer outputText) {
		this.outputText = outputText;
	}
}
