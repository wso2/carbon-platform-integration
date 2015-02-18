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

package org.wso2.carbon.automation.artifacts.JMXclientUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TriggerDeadlock {

    private static final Log log = LogFactory.getLog(TriggerDeadlock.class);

    /**
     *Execute an executable jar file and create new process and return it.
     *
     * @param executableJarPath Path of the executable jar file
     * @return Created process
     * @throws java.io.IOException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public Process runNewProcess(String executableJarPath) throws IOException, NoSuchFieldException, IllegalAccessException {
        File executableJar = new File(executableJarPath);

        int pid = 0;
        Process process = null;
        if(executableJar.exists() && FilenameUtils.getExtension(executableJar.getName()).equalsIgnoreCase("jar")){
            String[] cmdArray = new String[3];
            cmdArray[0] = "java";
            cmdArray[1] = "-jar";
            cmdArray[2] = executableJar.getAbsolutePath();

            process = Runtime.getRuntime().exec(cmdArray,null);

            return process;
        }else{
            log.error("Executable file dose not exist in the given path");
            throw new FileNotFoundException("Executable file dose not exist in the given path : "+executableJarPath);
        }
    }

    /**
     *
     * Execute an executable file and create process and return it.
     *
     * @param path Path of the executable jar.
     * @param cmdArray Commandline arguments to execute the electable file.
     * @return Created process
     * @throws java.io.IOException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public Process runNewProcess(String path, String[] cmdArray) throws IOException, NoSuchFieldException, IllegalAccessException {
        File executableJar = new File(path);

        int pid = 0;
        Process process = null;
        if(executableJar.exists()){

            process = Runtime.getRuntime().exec(cmdArray,null);

            return process;
        }else {
            log.error("Executable file dose not exist in the given path");
            throw new FileNotFoundException("Executable file dose not exist in the given path");
        }
    }

}
