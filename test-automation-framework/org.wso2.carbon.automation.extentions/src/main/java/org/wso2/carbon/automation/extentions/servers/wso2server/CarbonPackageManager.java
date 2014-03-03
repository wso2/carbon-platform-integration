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
package org.wso2.carbon.automation.extentions.servers.wso2server;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.extentions.ExtentionConstants;
import org.wso2.carbon.automation.extentions.ExtentionUtills;
import org.wso2.carbon.utils.FileManipulator;

import java.io.File;
import java.io.IOException;

public class CarbonPackageManager {
    private static final Log log = LogFactory.getLog(CarbonPackageManager.class);

    public String unzipCarbonPackage() throws ZipException {
        String carbonTempHome = ExtentionUtills.getCarbonTempLocation();
        String carbonHome;
        String carbonZipFileLocation = ExtentionUtills.getCarbonZipLocation();
        if (new File(carbonTempHome).exists()) {
            FileManipulator.deleteDir(new File(carbonTempHome));
        }
        try {
            ZipFile zipFile = new ZipFile(ExtentionUtills.getCarbonZipLocation());
            carbonHome = carbonTempHome + File.separator + carbonZipFileLocation.
                    substring(carbonZipFileLocation.lastIndexOf(File.separator) + 1,
                            carbonZipFileLocation.indexOf(".zip"));
            log.info("Extracting carbon distribution into : " + carbonTempHome);
            zipFile.extractAll(carbonTempHome);
        } catch (ZipException e) {
            log.error("Unable to extract distribution at " +
                    ExtentionUtills.getCarbonZipLocation());
            throw new ZipException("Unable to extract distribution at " +
                    ExtentionUtills.getCarbonZipLocation(), e);
        }
        return carbonHome;
    }

    public void copySecurityVerificationService() throws IOException {
        String secVerifierDir = System.getProperty(ExtentionConstants.
                SYSTEM_PROPERTY_SEC_VERIFIER_DIRECTORY);
        File srcFile = new File(secVerifierDir + ExtentionConstants.SERVICE_FILE_SEC_VERIFIER);
        String deploymentPath = ExtentionUtills.getCarbonServerAxisServiceDirectory();
        File depFile = new File(deploymentPath);
        if (! depFile.exists() && ! depFile.mkdir()) {
            throw new IOException("Error while creating the deployment folder : " + deploymentPath);
        }
        File dstFile = new File(depFile.getAbsolutePath() + File.separator
                + ExtentionConstants.SERVICE_FILE_SEC_VERIFIER);
        log.info("Copying " + srcFile.getAbsolutePath() + " => " + dstFile.getAbsolutePath());
        FileManipulator.copyFile(srcFile, dstFile);
    }
}
