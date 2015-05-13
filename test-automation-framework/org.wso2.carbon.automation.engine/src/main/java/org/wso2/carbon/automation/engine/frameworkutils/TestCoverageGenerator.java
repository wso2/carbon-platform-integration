/*
*Copyright (c) 2015​, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.automation.engine.frameworkutils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;

import java.io.File;
import java.io.IOException;

/**
 * Coverage generator class for multi module test project.
 * This class traverse though all Jacoco data dump files in multiple modules and then merge the result
 * into one file. This file will be used to generate aggregated coverage report.
 */
public class TestCoverageGenerator {

    private static final Log log = LogFactory.getLog(TestCoverageGenerator.class);
    private static String carbonZip;
    private static String carbonHome;

    public static void main(String[] args) throws AutomationFrameworkException, IOException {

        if (carbonZip == null) {
            carbonZip = FrameworkPathUtil.getCarbonZipLocation();
            log.info("Using carbon zip file at  " + carbonZip);
        }
        if (carbonZip == null) {
            throw new IllegalArgumentException("carbon zip file cannot find in the given location " +
                                               FrameworkPathUtil.getCarbonZipLocation());
        }
        carbonHome = ArchiveExtractorUtil.setUpCarbonHome(carbonZip);
        File parentDirectory = new File(System.getProperty("basedir")).getParentFile();

        CodeCoverageUtils.executeMerge(parentDirectory.getAbsolutePath());

        File carbonPluginDir =
                new File(carbonHome + File.separator + "repository" +
                         File.separator + "components" + File.separator + "plugins" + File.separator);


        ReportGenerator reportGenerator =
                new ReportGenerator(new File(FrameworkPathUtil.getCoverageMergeFilePath()),
                                    carbonPluginDir,
                                    new File(FrameworkPathUtil.getCoverageDirPath()),
                                    null);
        reportGenerator.create();

        log.info("Jacoco coverage merged file : " + FrameworkPathUtil.getCoverageMergeFilePath());
        log.info("Jacoco class file path : " + carbonPluginDir.getAbsolutePath());
        log.info("Jacoco coverage HTML report path : " + FrameworkPathUtil.getCoverageDirPath() + File.separator + "index.html");
    }
}