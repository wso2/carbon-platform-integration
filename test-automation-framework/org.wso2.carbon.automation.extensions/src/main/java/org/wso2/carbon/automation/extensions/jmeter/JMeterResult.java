/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.automation.extensions.jmeter;

import java.util.List;

public class JMeterResult {
    private String fileName;
    private boolean executionState;
    private List<String> assertList;
    private List<String> errorList;
    private long errorCount = 0;
    private long failureCount = 0;

    public String getFileName() {
        return fileName;
    }

    public boolean getExecuteState() {
        return executionState;
    }

    public List<String> getAssertList() {
        return assertList;
    }

    public void setAssertList(List<String> assertList) {
        this.assertList = assertList;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public long getFailureCount() {
        return failureCount;
    }

    public void increaseFailureCount() {
        failureCount++;
    }

    public void increaseErrorCount() {
        errorCount++;
    }

    public void setExecutionState(boolean executionState) {
        this.executionState = executionState;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
