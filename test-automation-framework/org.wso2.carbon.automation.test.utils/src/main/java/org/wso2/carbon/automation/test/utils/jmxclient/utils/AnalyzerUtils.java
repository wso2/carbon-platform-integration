/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.automation.test.utils.jmxclient.utils;

import java.io.File;
import java.lang.management.ThreadInfo;
import java.sql.Timestamp;
import java.util.Date;

/**
 * provide utility methods
 */

public class AnalyzerUtils {
    /**
     * create absolute file path
     *
     * @param path      location of the memory dump file.
     * @param extension file extension
     * @return absolute file path as a string
     */
    public String getAbsoluteFilePath(String path, String extension) {
        Timestamp timeStamp = new Timestamp(new Date().getTime());

        StringBuilder absoluteFilePathBuilder = new StringBuilder();

        absoluteFilePathBuilder.append(path);
        absoluteFilePathBuilder.append(File.separator);
        absoluteFilePathBuilder.append(timeStamp);
        absoluteFilePathBuilder.append(".");
        absoluteFilePathBuilder.append(extension);

        return absoluteFilePathBuilder.toString();
    }

    /**
     * build the dump message of a thread.
     *
     * @param threadInfo ThreadInfo object
     * @return dump message of the thread
     */
    public String buildDumpMessage(ThreadInfo threadInfo) {
        StringBuilder dump = new StringBuilder();
        dump.append("Thread name:");
        dump.append('"');
        dump.append(threadInfo.getThreadName());
        dump.append("\" ");

        final Thread.State state = threadInfo.getThreadState();

        dump.append("\n\tjava.lang.Thread.State: ");
        dump.append(state);

        final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();

        for (final StackTraceElement stackTraceElement : stackTraceElements) {
            dump.append("\n\t\tat ");
            dump.append(stackTraceElement);
        }
        dump.append("\n\n");

        return dump.toString();
    }

}
