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
import org.wso2.carbon.automation.test.utils.common.FileManager;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class MySqlDatabaseManager implements DatabaseManager {
    private static final Log log = LogFactory.getLog(MySqlDatabaseManager.class);
    private Connection connection;

    public MySqlDatabaseManager(String jdbcUrl, String userName, String passWord)
            throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        log.debug("JDBC Url: " + jdbcUrl);
        connection = DriverManager.getConnection(jdbcUrl, userName, passWord);
        log.debug("Connected to database");
    }

    /**
     * @param sql
     * @throws java.sql.SQLException
     */
    public void executeUpdate(String sql) throws SQLException {
        Statement st = null;
        try {
            st = connection.createStatement();
            log.debug(sql);
            st.executeUpdate(sql.trim());
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    //can do nothing
                }
            }
        }
        log.debug("Sql update Success");
    }

    /**
     * @param sqlFile
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    public void executeUpdate(File sqlFile) throws SQLException, IOException {
        Statement st = null;
        String sql = FileManager.readFile(sqlFile).trim();
        log.debug("Query List:" + sql);
        String[] sqlQuery = sql.split(";");
        try {
            st = connection.createStatement();
            for (String query : sqlQuery) {
                log.debug(query);
                st.executeUpdate(query.trim());
            }
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    //can do nothing
                }
            }
        }
        log.debug("Sql execution Success");
    }

    /**
     * @param sql
     * @return
     * @throws java.sql.SQLException
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        ResultSet rs;
        Statement st = null;
        try {
            st = connection.createStatement();
            log.debug(sql);
            rs = st.executeQuery(sql);
        } finally {
            if (st != null) {
                st.close();
            }
        }
        return rs;
    }

    /**
     * @param sql
     * @throws java.sql.SQLException
     */
    public void execute(String sql) throws SQLException {
        Statement st = null;
        try {
            st = connection.createStatement();
            st.execute(sql);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    //can do nothing
                }
            }
        }
        log.debug("Sql execution Success");
    }

    /**
     * @param sql
     * @return
     * @throws java.sql.SQLException
     */
    public Statement getStatement(String sql) throws SQLException {
        return connection.createStatement();
    }

    /**
     * @throws java.sql.SQLException
     */
    public void disconnect() throws SQLException {
        connection.close();
        log.debug("Disconnected from database");
    }

    protected void finalize() throws Throwable {
        try {
            if (!connection.isClosed()) {
                disconnect();
            }
        } catch (SQLException e) {
            log.error("Error while disconnecting from database");
            throw new SQLException("Error while disconnecting from database");
        }
        super.finalize();
    }
}

