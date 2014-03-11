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
package org.wso2.carbon.automation.test.utils.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.frameworkutils.CodeCoverageUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCoverageGenerator {
    private static final Log log = LogFactory.getLog(TestCoverageGenerator.class);

    public static void main(String[] args) {
        CodeCoverageUtils.generateReports(getCarbonHomes());
    }

    public static List<File> getCarbonHomes() {
        String baseDir = System.getProperty("basedir");
        log.info("Maven base dir - " + baseDir);
        List<File> carbonHomeFileList = new ArrayList<File>();
        int count = 0;
        List<File> files = getAllDirectories(new File(baseDir.substring(0, baseDir.lastIndexOf(File.separator))));
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            if (filePath.substring(filePath.lastIndexOf(File.separator), filePath.length()).contains("carbontmp")) {
                log.info(file.getAbsoluteFile());
                File[] carbonInstanceDirectory = file.listFiles();
                if (carbonInstanceDirectory.length >= 2) {
                    log.error("More than one carbon instance directory found inside the directory");
                } else {
                    log.info("Carbon instance directory - " + carbonInstanceDirectory[0].getAbsoluteFile());
                    carbonHomeFileList.add(carbonInstanceDirectory[0]);
                    count++;
                }
            }
        }
        return carbonHomeFileList;
    }

    public static List<File> getAllDirectories(File file) {
        List<File> subDirs = Arrays.asList(file.listFiles(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory();
            }
        }));
        subDirs = new ArrayList<File>(subDirs);
        List<File> deepSubdirs = new ArrayList<File>();
        for (File subdir : subDirs) {
            deepSubdirs.addAll(getAllDirectories(subdir));
        }
        subDirs.addAll(deepSubdirs);
        return subDirs;
    }
}
