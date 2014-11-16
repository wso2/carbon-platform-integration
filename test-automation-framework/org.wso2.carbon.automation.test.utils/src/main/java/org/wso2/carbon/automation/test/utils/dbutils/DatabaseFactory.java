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
package org.wso2.carbon.automation.test.utils.dbutils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.configurations.AutomationConfiguration;
import org.wso2.carbon.automation.test.utils.AutomationContextXPathConstants;

import javax.xml.xpath.XPathExpressionException;
import java.sql.SQLException;

public class DatabaseFactory {
    private static final Log log = LogFactory.getLog(DatabaseFactory.class);
    private static String JDBC_URL;
    private static String JDBC_DRIVER;
    private static String DB_USER;
    private static String DB_PASSWORD;

    private static void init() throws XPathExpressionException {
        JDBC_URL = AutomationConfiguration.getConfigurationValue
                (AutomationContextXPathConstants.DATA_SOURCE_URL);
        JDBC_DRIVER = AutomationConfiguration.getConfigurationValue
                (AutomationContextXPathConstants.DATA_SOURCE_DRIVER_CLASS_NAME);
        DB_USER = AutomationConfiguration.getConfigurationValue
                (AutomationContextXPathConstants.DATA_SOURCE_DB_USER_NAME);
        DB_PASSWORD = AutomationConfiguration.getConfigurationValue
                (AutomationContextXPathConstants.DATA_SOURCE_DB_PASSWORD);
    }

    public static DatabaseManager getDatabaseConnector(String databaseDriver, String jdbcUrl,
                                                       String userName, String passWord)
            throws ClassNotFoundException, SQLException, XPathExpressionException {
        return new SqlDatabaseManager(databaseDriver, jdbcUrl, userName, passWord);
    }

    public static DatabaseManager getDatabaseConnector(String jdbcUrl, String userName,
                                                       String passWord)
            throws ClassNotFoundException, SQLException, XPathExpressionException {
        init();
        return new SqlDatabaseManager(JDBC_DRIVER, jdbcUrl, userName, passWord);
    }

    public static DatabaseManager getDatabaseConnector()
            throws ClassNotFoundException, SQLException, XPathExpressionException {
        init();
        return new SqlDatabaseManager(JDBC_DRIVER, JDBC_URL, DB_USER, DB_PASSWORD);
    }
}
