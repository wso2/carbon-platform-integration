/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.automation.coverage.report;


import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.ArchiveExtractorUtil;
import org.wso2.carbon.automation.engine.frameworkutils.CodeCoverageUtils;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.engine.frameworkutils.ReportGenerator;

import java.io.File;
import java.io.IOException;

/**
 * Coverage generator for multiple coverage dump files.
 * This class traverse though all Jacoco data dump files in multiple modules and then merge the result
 * into one file. This file will be used to generate aggregated coverage report.
 */
public class RemoteTestCoverageGenerator {

    private static final Log log = LogFactory.getLog(RemoteTestCoverageGenerator.class);
    private static String carbonZip;

    public static void main(String[] args) throws AutomationFrameworkException, IOException {

        checkSystemProperties("basedir");
        checkSystemProperties("filters.file");
        checkSystemProperties("instr.file");
        checkSystemProperties("carbon.zip");

        System.setProperty("report.dir", System.getProperty("basedir") + File.separator + "reports");

        if (args.length == 0) {
            log.info("Proper Usage is: [Coverage dump file paths separated by comma]");
            System.exit(0);
        }

        String remoteCoverageMergeFile = System.getProperty("basedir") + File.separator +
                                         "remote-dump" + File.separator + "remote-coverage-merge.exec";

        cleanUpFiles(remoteCoverageMergeFile); //delete remote coverage file if exists
        cleanUpFiles(CodeCoverageUtils.getJacocoReportDirectory()); //delete jacoco report directory if exists
        cleanUpFiles(System.getProperty("basedir")); //delete jacoco base dir

        String carbonHome = extractCarbonZip();

        String[] dumpFileDirectories = (args[0].split(","));

        File carbonPluginDir =
                new File(carbonHome + File.separator + "repository" +
                         File.separator + "components" + File.separator + "plugins" + File.separator);

        for (String dumpFile : dumpFileDirectories) {
            CodeCoverageUtils.executeMerge(new File(dumpFile).getAbsolutePath(), remoteCoverageMergeFile);
        }

        ReportGenerator reportGenerator =
                new ReportGenerator(new File(remoteCoverageMergeFile),
                                    carbonPluginDir,
                                    new File(CodeCoverageUtils.getJacocoReportDirectory()),
                                    null);

        reportGenerator.create();
        cleanUpFiles(carbonHome); //clean carbon server instance

        log.info("Jacoco coverage merged file : " + FrameworkPathUtil.getCoverageMergeFilePath());
        log.info("Jacoco class file path : " + carbonPluginDir.getAbsolutePath());
        log.info("Jacoco coverage HTML report path : " + CodeCoverageUtils.getJacocoReportDirectory() +
                 File.separator + "index.html");
    }

    private static String extractCarbonZip() throws IOException {
        if (carbonZip == null) {
            carbonZip = FrameworkPathUtil.getCarbonZipLocation();
            log.info("Using carbon zip file at  " + carbonZip);
        }

        if (carbonZip == null) {
            throw new IllegalArgumentException("carbon zip file cannot find in the given location " +
                                               FrameworkPathUtil.getCarbonZipLocation());
        }

        return ArchiveExtractorUtil.setUpCarbonHome(carbonZip);
    }

    private static void checkSystemProperties(String propertyKey) {
        if (System.getProperty(propertyKey) == null){
            throw new IllegalArgumentException("System property not set : " + propertyKey);
        }
    }

    private static void cleanUpFiles(String path){
        if (new File(path).exists()){
            FileUtils.deleteQuietly(new File(path));
        }
    }
}
