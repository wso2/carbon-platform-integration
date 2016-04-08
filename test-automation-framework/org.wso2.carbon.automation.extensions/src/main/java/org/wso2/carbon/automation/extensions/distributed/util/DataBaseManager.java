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
import org.wso2.carbon.automation.test.utils.dbutils.MySqlDatabaseManager;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This class handles all DB related operations
 */
public class DataBaseManager {

    private String resourceLocation = FrameworkPathUtil.getSystemResourceLocation();

    public void performDBOperations(String mysqlContainerIP) throws SQLException, ClassNotFoundException, IOException, InterruptedException {

        Thread.sleep(40000);

        MySqlDatabaseManager dbs = new MySqlDatabaseManager("jdbc:mysql://" + mysqlContainerIP + ":3306/?zeroDateTimeBehavior=convertToNull",
                PropertyFileReader.databaseUserName, PropertyFileReader.databaseUserPassword);

        // create DBs
        dbs.executeDBScript(resourceLocation + "/scripts/createdatabases.sql");

        //create Tables - wso2_config_DB
        dbs.execute("USE WSO2_CONFIG_DB");
        dbs.executeDBScript(resourceLocation + "/scripts/mysql.sql");

        //create Tables - wso2_gov_DB
        dbs.execute("USE WSO2_GOV_DB");
        dbs.executeDBScript(resourceLocation + "/scripts/mysql.sql");

        //create Tables - wso2_user_DB
        dbs.execute("USE WSO2_USER_DB");
        dbs.executeDBScript(resourceLocation + "/scripts/mysql.sql");

    }
}
