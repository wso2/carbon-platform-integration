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
package org.wso2.carbon.automation.extensions.jmeter.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;

public class Utils {
    public static void copyFromClassPath(String fileName, File destination) throws IOException {

        BufferedWriter out = null;
        try {

             out = new BufferedWriter
                    (new OutputStreamWriter(new FileOutputStream(destination), Charset.defaultCharset()));

            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName), out);


        } catch (IOException e) {
            throw new IOException("Could not create temporary saveservice.properties", e);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}
