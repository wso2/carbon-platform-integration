/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.automation.extensions.distributed.util;

import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class serves as a property file reader
 */
public class PropertyFileReader {

    public static String resourceLocation = FrameworkPathUtil.getSystemResourceLocation();

    public static String dockerHostIP;
    public static String dockerHostPort;
    public static String dockerUrl;
    public static String dockerFilesGitRepo;
    public static String puppetModuleGitRepo;
    public static String databaseUserName;
    public static String databaseUserPassword;

    /**
     * Reading property file
     *
     * @throws java.io.IOException
     */
    public void readPropertyFile() throws IOException {

        String propFileName = resourceLocation + "distributed.properties";

        Properties prop = new Properties();

        InputStream input = new FileInputStream(propFileName);

        prop.load(input);

        dockerHostIP = prop.getProperty("docker.host.name");
        dockerHostPort = prop.getProperty("docker.host.port");
        dockerFilesGitRepo = prop.getProperty("docker.files.git.repo");
        puppetModuleGitRepo = prop.getProperty("puppet.module.git.repo");
        databaseUserName = prop.getProperty("database.user.name");
        databaseUserPassword = prop.getProperty("database.user.password");

        dockerUrl = "http://" + dockerHostIP + ":" + dockerHostPort;
    }
}
