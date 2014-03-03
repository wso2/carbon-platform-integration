package org.wso2.carbon.automation.test.utils.dbutils;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface DatabaseManager {

    public void executeUpdate(String sql) throws SQLException;

    public void executeUpdate(File sqlFile) throws SQLException, IOException;

    public Statement getStatement(String sql) throws SQLException;

    public ResultSet executeQuery(String sql) throws SQLException;

    public void execute(String sql) throws SQLException;

    public void disconnect() throws SQLException;

}

