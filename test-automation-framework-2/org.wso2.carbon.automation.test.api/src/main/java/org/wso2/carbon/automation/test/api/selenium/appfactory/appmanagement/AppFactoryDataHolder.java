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

package org.wso2.carbon.automation.test.api.selenium.appfactory.appmanagement;

public class AppFactoryDataHolder {

    private static AppFactoryDataHolder value = new AppFactoryDataHolder();
    private static String sandBox;
    private static String sandboxAndProductionDetails;
    private static String production;
    private static String dbName;

    private AppFactoryDataHolder() {
    }

    public static AppFactoryDataHolder getInstance() {
        return value;
    }

    public static void setSandBoxDetails(String sandBoxDetails) {
        sandBox = sandBoxDetails;
    }

    public static String getSandBoxDetails() {
        return sandBox;
    }


    public static void setProductionDetails(String productionDetails) {
        production = productionDetails;
    }

    public static String getProductionDetails() {
        return production;
    }

    public static void setSandBoxAndProductionDetails(String details) {
        sandboxAndProductionDetails = details;
    }

    public static String getSandboxAndProductionDetails() {
        return sandboxAndProductionDetails;
    }
}
