/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.automation.extensions;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.enums.OperatingSystems;

import java.io.File;

public class FrameworkExtensionUtils {

    private static final Log log = LogFactory.getLog(FrameworkExtensionUtils.class);

    public static String getSystemResourceLocation() throws AutomationFrameworkException {
        return getOSSensitivePath(System.getProperty(ExtensionConstants.SYSTEM_ARTIFACT_RESOURCE_LOCATION));
    }

    /**
     * Gives the absolute resource location.
     *
     * @param relativePath Relative path from system resource location. Relative path should not start with /.
     * @return OS sensitive absolute resource location.
     * @throws AutomationFrameworkException possible from getSystemResourceLocation() and getOSSensitivePath().
     */
    public static String getResourceLocation(String relativePath) throws
            AutomationFrameworkException {
        return getSystemResourceLocation() + getOSSensitivePath(relativePath);
    }

    public static String getSystemSettingsLocation() throws AutomationFrameworkException {
        String settingsLocation;
        if (System.getProperty(ExtensionConstants.SYSTEM_PROPERTY_SETTINGS_LOCATION) != null) {
            return getOSSensitivePath(System.getProperty(ExtensionConstants.SYSTEM_ARTIFACT_RESOURCE_LOCATION));
        } else {
            settingsLocation = getSystemResourceLocation();
        }
        return settingsLocation;
    }

    public static String getReportLocation() {
        return (System.getProperty(ExtensionConstants.SYSTEM_PROPERTY_BASEDIR_LOCATION, ".")) +
               File.separator + "target";
    }

    public static String getCarbonZipLocation() {
        return System.getProperty(ExtensionConstants.SYSTEM_PROPERTY_CARBON_ZIP_LOCATION);
    }

    public static String getCarbonTempLocation() {
        String extractDir = "carbontmp" + System.currentTimeMillis();
        String baseDir = (System.getProperty("basedir", ".")) + File.separator + "target";
        return new File(baseDir).getAbsolutePath() + File.separator + extractDir;
    }

    public static String getCarbonServerAxisServiceDirectory() {
        return getCarbonHome() + File.separator + "repository" + File.separator + "deployment"
               + File.separator + "server" + File.separator + "axis2services";
    }

    public static String getCarbonServerLibLocation() {
        return getCarbonHome() + File.separator + "repository" + File.separator + "components" +
               File.separator + "lib";
    }

    public static String getCarbonServerConfLocation() {
        return getCarbonHome() + File.separator + "repository" + File.separator + "conf";
    }


    public static String getCarbonHome() {
        String carbonHome;
        if (System.getProperty("user.dir") != null) {
            carbonHome = System.getProperty("user.dir");
        } else {
            log.error("Cannot read carbon.home property");
            carbonHome = null;
        }
        return carbonHome;
    }

    /**
     * Gives the OS sensitive path.
     *
     * @param path File Path in linux standard. Should not be null.
     * @return OS sensitive file path.
     * @throws AutomationFrameworkException Throws when path is null.
     */
    public static String getOSSensitivePath(String path) throws AutomationFrameworkException {

        String oSName = System.getProperty(ExtensionConstants.SYSTEM_PROPERTY_OS_NAME);
        if (path == null) {
            throw new AutomationFrameworkException("Path cannot be null");
        } else if (StringUtils.isEmpty(oSName)) {
            throw new AutomationFrameworkException("System property: " + ExtensionConstants.SYSTEM_PROPERTY_OS_NAME
                    + " is empty.");
        }

        if (oSName.toLowerCase().contains(OperatingSystems.WINDOWS.name())) {
            return path.replace("/", "\\");
        }
        return path;
    }
}
