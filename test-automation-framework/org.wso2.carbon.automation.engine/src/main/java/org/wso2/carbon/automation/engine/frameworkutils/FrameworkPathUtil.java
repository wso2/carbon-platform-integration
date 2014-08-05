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
package org.wso2.carbon.automation.engine.frameworkutils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.frameworkutils.enums.OperatingSystems;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;

public class FrameworkPathUtil {
    public static final String SYSTEM_ARTIFACT_RESOURCE_LOCATION = "framework.resource.location";
    private static final Log log = LogFactory.getLog(FrameworkPathUtil.class);

    public static String getSystemResourceLocation() {
        String resourceLocation;
        if (System.getProperty(FrameworkConstants.SYSTEM_PROPERTY_OS_NAME)
                .toLowerCase().contains(OperatingSystems.WINDOWS.name().toLowerCase())) {
            resourceLocation = System.getProperty
                    (SYSTEM_ARTIFACT_RESOURCE_LOCATION).replace("/", "\\");
        } else {
            resourceLocation = System.getProperty
                    (SYSTEM_ARTIFACT_RESOURCE_LOCATION).replace("/", "/");
        }
        return resourceLocation;
    }

    public static String getSystemSettingsLocation() {
        String settingsLocation;
        if (System.getProperty
                (FrameworkConstants.SYSTEM_PROPERTY_SETTINGS_LOCATION) != null) {
            if (System.getProperty(FrameworkConstants.SYSTEM_PROPERTY_OS_NAME)
                    .toLowerCase().contains(OperatingSystems.WINDOWS.name().toLowerCase())) {
                settingsLocation = System.getProperty
                        (FrameworkConstants.SYSTEM_PROPERTY_SETTINGS_LOCATION).replace("/", "\\");
            } else {
                settingsLocation = System.getProperty
                        (FrameworkConstants.SYSTEM_PROPERTY_SETTINGS_LOCATION).replace("/", "/");
            }
        } else {
            settingsLocation = getSystemResourceLocation();
        }
        return settingsLocation;
    }

    public static String getReportLocation() {
        String reportLocation;
        reportLocation = (System.getProperty(FrameworkConstants
                .SYSTEM_PROPERTY_BASEDIR_LOCATION, ".")) + File.separator + "target";
        return reportLocation;
    }

    public static String getCarbonZipLocation() {
        return System.getProperty(FrameworkConstants.SYSTEM_PROPERTY_CARBON_ZIP_LOCATION);
    }

    public static String getCarbonTempLocation() {
        String extractDir = "carbontmp" + System.currentTimeMillis();
        String baseDir = (System.getProperty("basedir", ".")) + File.separator + "target";
        return new File(baseDir).getAbsolutePath() + File.separator + extractDir;
    }

    public static String getCarbonServerAxisServiceDirectory() {
        return getCarbonHome() + File.separator + "repository" + File.separator
                + "deployment" + File.separator + "server" + File.separator + "axis2services";
    }

    public static String getCarbonServerLibLocation() {
        return getCarbonHome() + File.separator + "repository" + File.separator + "components" +
                File.separator + "lib";
    }

    public static String getCarbonServerConfLocation() {
        return getCarbonHome() + File.separator + "repository" + File.separator + "conf";
    }

    public static String getCarbonHome() {

        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        if (carbonHome == null) {
               carbonHome = System.getenv(CarbonConstants.CARBON_HOME_ENV);
               System.setProperty(ServerConstants.CARBON_HOME, carbonHome);
        }

        return carbonHome;
    }
}
