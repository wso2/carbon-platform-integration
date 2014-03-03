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
package org.wso2.dashboard;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;


public class DataProvider {

    private static Connection connection = null;

    private static String jdbcUrl = getProperty("jdbc.url");
    private static final String userName = getProperty("db.user");
    private static final String password = getProperty("db.password");
    private static final String driver = getProperty("jdbc.driver");
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static BASE64Decoder dec = new BASE64Decoder();

    public DataProvider() throws ClassNotFoundException, SQLException {
        if (connection == null || connection.isClosed()) {
            synchronized (DataProvider.class) {
                if (connection == null || connection.isClosed()) {
                    Class.forName(driver);
                    if (jdbcUrl.contains("?")) {
                        if (!jdbcUrl.contains("autoReconnect=")) {
                            jdbcUrl = jdbcUrl.concat("&autoReconnect=true");
                        }
                    } else {
                        jdbcUrl = jdbcUrl.concat("?autoReconnect=true");
                    }
                    connection = DriverManager.getConnection(jdbcUrl, userName, password);
                }
            }
        }
    }

    public JSONArray getTestCases(String build, String state) throws SQLException, IOException {
        JSONArray objArray = new JSONArray();
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT WA_TESTCASE_STAT.*, WA_TEST_CLASS_STAT.WA_TEST_CLASS_NAME " +
                                  "FROM WA_TESTCASE_STAT,WA_TEST_CLASS_STAT  " +
                                  "WHERE WA_TESTCASE_STAT.WA_BUILD_NUMBER=" + build
                                  + " AND WA_TESTCASE_STAT.WA_TESTCASE_STATUS='" + state +
                                  "' AND WA_TESTCASE_STAT.WA_TEST_CLASS_ID=WA_TEST_CLASS_STAT.WA_TEST_CLASS_ID");
            while (rst.next()) {
                JSONObject object = new JSONObject();
                object.put("build", rst.getString("WA_BUILD_NUMBER"));
                object.put("testClass", rst.getString("WA_TEST_CLASS_NAME"));
                object.put("testCase", rst.getString("WA_TESTCASE_NAME"));
                object.put("status", rst.getString("WA_TESTCASE_STATUS"));
                object.put("duration", rst.getString("WA_TEST_DURATION"));
                if (rst.getString("WA_TESTCASE_STATUS").equalsIgnoreCase("FAIL")) {
                    object.putAll(getFailReason(rst.getInt("WA_TESTCASE_ID")));
                }

                objArray.add(object);

            }
        } finally {

            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }

        }

        return objArray;
    }

    public JSONArray getTestCasesBySuite(String suite, String state)
            throws SQLException, IOException {
        JSONArray objArray = new JSONArray();
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT WA_TESTCASE_STAT.*, WA_TEST_CLASS_STAT.WA_TEST_CLASS_NAME " +
                                  "FROM WA_TESTCASE_STAT,WA_TEST_CLASS_STAT  " +
                                  "WHERE WA_TESTCASE_STAT.WA_TEST_SUITE_ID=" + suite
                                  + " AND WA_TESTCASE_STAT.WA_TESTCASE_STATUS='" + state +
                                  "' AND WA_TESTCASE_STAT.WA_TEST_CLASS_ID=WA_TEST_CLASS_STAT.WA_TEST_CLASS_ID");
            while (rst.next()) {
                JSONObject object = new JSONObject();
                object.put("build", rst.getString("WA_BUILD_NUMBER"));
                object.put("testClass", rst.getString("WA_TEST_CLASS_NAME"));
                object.put("testCase", rst.getString("WA_TESTCASE_NAME"));
                object.put("status", rst.getString("WA_TESTCASE_STATUS"));
                object.put("duration", rst.getString("WA_TEST_DURATION"));
                if (rst.getString("WA_TESTCASE_STATUS").equalsIgnoreCase("FAIL")) {
                    object.putAll(getFailReason(rst.getInt("WA_TESTCASE_ID")));
                }

                objArray.add(object);

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }

            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }

        }
        return objArray;
    }

    public JSONArray getTestCasesByClass(String testClass, String state)
            throws SQLException, IOException {
        JSONArray objArray = new JSONArray();
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT WA_TESTCASE_STAT.*, WA_TEST_CLASS_STAT.WA_TEST_CLASS_NAME " +
                                  "FROM WA_TESTCASE_STAT,WA_TEST_CLASS_STAT  " +
                                  "WHERE WA_TESTCASE_STAT.WA_TEST_CLASS_ID=" + testClass
                                  + " AND WA_TESTCASE_STAT.WA_TESTCASE_STATUS='" + state +
                                  "' AND WA_TESTCASE_STAT.WA_TEST_CLASS_ID=WA_TEST_CLASS_STAT.WA_TEST_CLASS_ID");
            while (rst.next()) {
                JSONObject object = new JSONObject();
                object.put("build", rst.getString("WA_BUILD_NUMBER"));
                object.put("testClass", rst.getString("WA_TEST_CLASS_NAME"));
                object.put("testCase", rst.getString("WA_TESTCASE_NAME"));
                object.put("status", rst.getString("WA_TESTCASE_STATUS"));
                object.put("duration", rst.getString("WA_TEST_DURATION"));
                if (rst.getString("WA_TESTCASE_STATUS").equalsIgnoreCase("FAIL")) {
                    object.putAll(getFailReason(rst.getInt("WA_TESTCASE_ID")));
                }

                objArray.add(object);

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return objArray;
    }

    public JSONArray getTestCases(int testClass) throws SQLException, IOException {
        JSONArray objArray = new JSONArray();
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT WA_TESTCASE_STAT.*, WA_TEST_CLASS_STAT.WA_TEST_CLASS_NAME FROM " +
                                  "WA_TESTCASE_STAT, WA_TEST_CLASS_STAT" +
                                  " WHERE WA_TESTCASE_STAT.WA_TEST_CLASS_ID=" + testClass
                                  + " AND WA_TESTCASE_STAT.WA_TEST_CLASS_ID=WA_TEST_CLASS_STAT.WA_TEST_CLASS_ID");
            while (rst.next()) {
                JSONObject object = new JSONObject();
                object.put("build", rst.getString("WA_BUILD_NUMBER"));
                object.put("testClass", rst.getString("WA_TEST_CLASS_NAME"));
                object.put("testCase", rst.getString("WA_TESTCASE_NAME"));
                object.put("status", rst.getString("WA_TESTCASE_STATUS"));
                object.put("duration", rst.getString("WA_TEST_DURATION"));
                object.putAll(getFailReason(rst.getInt("WA_TESTCASE_ID")));
                objArray.add(object);

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return objArray;
    }

    public JSONObject getTestSuite(int suiteId) throws SQLException {
        JSONObject object = null;
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT * FROM WA_TEST_SUITE_DETAIL WHERE WA_TEST_SUITE_ID = " + suiteId);
            while (rst.next()) {
                object = new JSONObject();
                object.put("build", rst.getString("WA_BUILD_NUMBER"));
                object.put("suite", rst.getString("WA_TEST_SUITE_ID"));
                object.put("suiteName", rst.getString("WA_SUITE_NAME"));
                object.put("duration", rst.getString("WA_TEST_DURATION"));


            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return object;
    }

    public JSONArray getTestSuites(int build) throws SQLException {
        JSONArray buildInfo = new JSONArray();
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT * FROM WA_TEST_SUITE_DETAIL WHERE WA_BUILD_NUMBER = " + build);
            while (rst.next()) {
                JSONObject customer = new JSONObject();
                customer.put("build", rst.getString("WA_BUILD_NUMBER"));
                customer.put("suite", rst.getString("WA_TEST_SUITE_ID"));
                customer.put("suiteName", rst.getString("WA_SUITE_NAME"));
                customer.put("pass", getCount(rst.getInt("WA_BUILD_NUMBER"),
                                              rst.getInt("WA_TEST_SUITE_ID"), "PASS"));
                customer.put("fail", getCount(rst.getInt("WA_BUILD_NUMBER"),
                                              rst.getInt("WA_TEST_SUITE_ID"), "FAIL"));
                customer.put("skip", getCount(rst.getInt("WA_BUILD_NUMBER"),
                                              rst.getInt("WA_TEST_SUITE_ID"), "SKIP"));
                customer.put("duration", rst.getString("WA_TEST_DURATION"));

                buildInfo.add(customer);

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return buildInfo;
    }

    public JSONArray getTestClasses(int build, int suite) throws SQLException {
        JSONArray buildInfo = new JSONArray();
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT * FROM WA_TEST_CLASS_STAT WHERE WA_BUILD_NUMBER = "
                                  + build + " AND WA_TEST_SUITE_ID =" + suite);
            while (rst.next()) {
                JSONObject object = new JSONObject();
                object.put("build", rst.getString("WA_BUILD_NUMBER"));
                object.put("suite", rst.getString("WA_TEST_SUITE_ID"));
                object.put("className", rst.getString("WA_TEST_CLASS_NAME"));
                object.put("classId", rst.getString("WA_TEST_CLASS_ID"));
                object.put("pass", getCount(rst.getInt("WA_BUILD_NUMBER"), rst.getInt("WA_TEST_SUITE_ID"),
                                            rst.getInt("WA_TEST_CLASS_ID"), "PASS"));
                object.put("fail", getCount(rst.getInt("WA_BUILD_NUMBER"), rst.getInt("WA_TEST_SUITE_ID"),
                                            rst.getInt("WA_TEST_CLASS_ID"), "FAIL"));
                object.put("skip", getCount(rst.getInt("WA_BUILD_NUMBER"), rst.getInt("WA_TEST_SUITE_ID"),
                                            rst.getInt("WA_TEST_CLASS_ID"), "SKIP"));
                object.put("duration", rst.getString("WA_TEST_DURATION"));

                buildInfo.add(object);

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return buildInfo;
    }

    public JSONObject getTestClass(int classId) throws SQLException {
        JSONObject object = null;
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT * FROM WA_TEST_CLASS_STAT WHERE WA_TEST_CLASS_ID = " + classId);
            while (rst.next()) {
                object = new JSONObject();
                object.put("build", rst.getString("WA_BUILD_NUMBER"));
                object.put("suite", rst.getString("WA_TEST_SUITE_ID"));
                object.put("className", rst.getString("WA_TEST_CLASS_NAME"));
                object.put("classId", rst.getString("WA_TEST_CLASS_ID"));
                object.put("pass", getCount(rst.getInt("WA_BUILD_NUMBER"), rst.getInt("WA_TEST_SUITE_ID"),
                                            rst.getInt("WA_TEST_CLASS_ID"), "PASS"));
                object.put("fail", getCount(rst.getInt("WA_BUILD_NUMBER"), rst.getInt("WA_TEST_SUITE_ID"),
                                            rst.getInt("WA_TEST_CLASS_ID"), "FAIL"));
                object.put("skip", getCount(rst.getInt("WA_BUILD_NUMBER"), rst.getInt("WA_TEST_SUITE_ID"),
                                            rst.getInt("WA_TEST_CLASS_ID"), "SKIP"));
                object.put("duration", rst.getString("WA_TEST_DURATION"));


            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return object;
    }

    public JSONArray getBuildHistory() throws SQLException {
        JSONArray data = new JSONArray();
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT * FROM WA_BUILD_HISTORY ORDER BY WA_BUILD_NUMBER DESC");
            while (rst.next()) {
                JSONObject record = new JSONObject();
                record.put("build", rst.getString("WA_BUILD_NUMBER"));
                record.put("startTime", getBuildTime(rst.getInt("WA_BUILD_NUMBER")));
                record.put(Constant.PASS, getCount(rst.getInt("WA_BUILD_NUMBER"), "PASS"));
                record.put(Constant.FAIL, getCount(rst.getInt("WA_BUILD_NUMBER"), "FAIL"));
                record.put(Constant.SKIP, getCount(rst.getInt("WA_BUILD_NUMBER"), "SKIP"));
                record.put("duration", getTimeTaken(rst.getInt("WA_BUILD_NUMBER")));

                data.add(record);

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return data;
    }

    public JSONObject getBuild(String buildNo) throws SQLException {
        JSONObject record = null;
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT * FROM WA_BUILD_HISTORY WHERE WA_BUILD_NUMBER=" + buildNo);
            while (rst.next()) {
                record = new JSONObject();
                record.put("build", rst.getString("WA_BUILD_NUMBER"));
                record.put(Constant.PASS, getCount(rst.getInt("WA_BUILD_NUMBER"), "PASS"));
                record.put(Constant.FAIL, getCount(rst.getInt("WA_BUILD_NUMBER"), "FAIL"));
                record.put(Constant.SKIP, getCount(rst.getInt("WA_BUILD_NUMBER"), "SKIP"));

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return record;
    }

    public JSONObject getLastBuild() throws SQLException {
        JSONObject record = null;
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT * FROM WA_BUILD_HISTORY " +
                                  "WHERE WA_BUILD_NUMBER=(SELECT max(WA_BUILD_NUMBER) FROM WA_BUILD_HISTORY)");
            while (rst.next()) {
                record = new JSONObject();
                record.put("build", rst.getString("WA_BUILD_NUMBER"));
                record.put(Constant.PASS, getCount(rst.getInt("WA_BUILD_NUMBER"), "PASS"));
                record.put(Constant.FAIL, getCount(rst.getInt("WA_BUILD_NUMBER"), "FAIL"));
                record.put(Constant.SKIP, getCount(rst.getInt("WA_BUILD_NUMBER"), "SKIP"));

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return record;
    }

    private static String getProperty(String key) {
        Properties dbProps = new Properties();
        InputStream is = null;

        try {
            is = DataProvider.class.getClassLoader().getResourceAsStream("dbconf.properties");
            dbProps.load(is);//this may throw IOException
        } catch (IOException e) {

            System.err.println("Database configuration not found");
        } finally {
            try {
                if (is != null) {
                    is.close();
                }

            } catch (IOException e) {

            }
        }
        return dbProps.getProperty(key);
    }

    private static int getCount(int build, String status) throws SQLException {
        int record = 0;
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT count(WA_TESTCASE_STATUS) FROM WA_TESTCASE_STAT " +
                                  "WHERE WA_BUILD_NUMBER = " + build
                                  + " AND WA_TESTCASE_STATUS = '" + status + "'");

            while (rst.next()) {
                record = rst.getInt(1);

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }

            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return record;
    }

    private static int getCount(int build, int suite, String status) throws SQLException {

        int record = 0;
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT count(WA_TESTCASE_STATUS) FROM WA_TESTCASE_STAT " +
                                  "WHERE WA_BUILD_NUMBER = " + build
                                  + " AND WA_TEST_SUITE_ID = " + suite
                                  + " AND WA_TESTCASE_STATUS = '" + status + "'");

            while (rst.next()) {
                record = rst.getInt(1);

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return record;
    }

    private static int getCount(int build, int testSuite, int testClass, String status)
            throws SQLException {
        int record = 0;
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT count(WA_TESTCASE_STATUS) FROM WA_TESTCASE_STAT " +
                                  "WHERE WA_BUILD_NUMBER = " + build
                                  + " AND WA_TEST_SUITE_ID = " + testSuite
                                  + " AND WA_TEST_CLASS_ID = " + testClass
                                  + " AND WA_TESTCASE_STATUS = '" + status + "'");
            while (rst.next()) {
                record = rst.getInt(1);

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return record;
    }

    private static double getTimeTaken(int build)
            throws SQLException {

        double record = 0;
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT sum(WA_TEST_DURATION) FROM WA_TEST_SUITE_DETAIL " +
                                  "WHERE WA_BUILD_NUMBER = " + build);

            while (rst.next()) {
                record = rst.getDouble(1);

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return record;
    }

    public Map<String, String> getFailReason(int testCaseId)
            throws SQLException, IOException {
        Map<String, String> record = new Hashtable<String, String>();
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT * FROM WA_ERROR_DETAIL " +
                                  "WHERE WA_TESTCASE_ID = " + testCaseId);

            while (rst.next()) {
                record.put("errorType", rst.getString("WA_ERROR_TYPE"));
                record.put("message", new String(dec.decodeBuffer(rst.getString("WA_ERROR_MESSAGE")),
                                                 DEFAULT_ENCODING));
                record.put("stackTrace", rst.getString("WA_EXCEPTION_ID"));

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return record;

    }

    public String getStackTrace(int exceptionId) throws SQLException, IOException {
        String record = "";
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT WA_ERROR_DESCRIPTION FROM WA_ERROR_DETAIL " +
                                  "WHERE WA_EXCEPTION_ID = " + exceptionId);


            while (rst.next()) {
                record = new String(dec.decodeBuffer(rst.getString("WA_ERROR_DESCRIPTION")),
                                    DEFAULT_ENCODING);

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return record;

    }

    private static String getBuildTime(int buildNo) throws SQLException {

        Statement st = null;
        ResultSet rst = null;
        String time = "";
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT min(WA_START_TIME) AS START_TIME FROM WA_TEST_SUITE_DETAIL " +
                                  "WHERE WA_BUILD_NUMBER=" + buildNo);


            while (rst.next()) {
                time = rst.getString("START_TIME");

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        if (time == null) {
            time = "N/A";
        }
        return time;
    }

    public JSONObject getTimeHistory(int suite, String testClassName) throws SQLException {

        JSONObject record = new JSONObject();
        Statement st = null;
        ResultSet rst = null;
        try {
            st = connection.createStatement();
            rst = st.executeQuery("SELECT WA_BUILD_NUMBER,WA_TEST_DURATION FROM WA_TEST_CLASS_STAT " +
                                  "WHERE WA_TEST_SUITE_ID = " + suite +
                                  " AND WA_TEST_CLASS_NAME='" + testClassName + "'");

            while (rst.next()) {
                record.put(rst.getString("WA_BUILD_NUMBER"), rst.getString("WA_TEST_DURATION"));

            }
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {

                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                }
            }
        }
        return record;

    }
}
