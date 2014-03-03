<%--/*
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
--%>
<%@ page import="org.wso2.dashboard.DataProvider" %>
<%@ page import="net.sf.json.JSONArray" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="net.sf.json.JSONObject" %>
<%@ page import="org.wso2.dashboard.Utils" %>
<%@ page import="org.wso2.dashboard.Constant" %>
<%@ page errorPage="error.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Automation Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="css/bootstrap.css" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css" type="text/css" media="screen"/>


    <style type="text/css">
        body {
            padding-top: 60px;
            padding-bottom: 40px;
        }

        .sidebar-nav {
            padding: 9px 0;
        }
    </style>


</head>

<body>

<jsp:include page="includes/header.jsp"/>
<%
    DataProvider dp = new DataProvider();
                        String build = request.getParameter("build");
                        String suite = request.getParameter("suite");
%>

<div class="container-fluid">
    <div class="row-fluid">
        <jsp:include page="includes/left-menu.jsp"/>
        <div class="span10">
            <div class="hero-unit">
                <%--Test build content goes here--%>
                <%
                    JSONObject suiteInfo = dp.getTestSuite(Integer.parseInt(suite));
                %>
                <h2>Test Build# :<%=suiteInfo.get("build")%>
                </h2><h2>Test Suite :<%=suiteInfo.get("suiteName")%>
                </h2>


                <%--end of the content--%>

            </div>
            <div class="hero-unit">
                <h2>Test Results</h2>

                <%-- <p>Test result content goes here</p>--%>

                <table class="table">
                    <tr>
                        <th><b>Test Class</b></th>
                        <th><b>Passed</b></th>
                        <th><b>Failed</b></th>
                        <th><b>Skipped</b></th>
                        <th><b>Duration(sec)</b></th>
                    </tr>
                    <%

                        JSONArray ja = dp.getTestClasses(Integer.parseInt(build), Integer.parseInt(suite));
                        Iterator iterator = ja.iterator();
                        while (iterator.hasNext()) {
                            JSONObject obj = (JSONObject) iterator.next();
                    %>
                    <tr <%if(obj.getInt(Constant.FAIL) > 0)
                        {%>class=fail <%
                    } else {
                    %> class=pass<%
                        }
                    %>>

                        <td>
                            <a href="testClass.jsp?testClass=<%=obj.get("classId")%>"><%=obj.get("className")%>
                            </a>

                        </td>
                        <td>
                            <a href="testCasesByClass.jsp?testClass=<%=obj.get("classId")%>&status=PASS"><%=obj.get("pass")%>
                            </a>

                        </td>
                        <td>
                            <a href="testCasesByClass.jsp?testClass=<%=obj.get("classId")%>&status=FAIL"><%=obj.get("fail")%>
                            </a>

                        </td>
                        <td>
                            <a href="testCasesByClass.jsp?testClass=<%=obj.get("classId")%>&status=SKIP"><%=obj.get("skip")%>
                            </a>

                        </td>
                        <td><%=Utils.round(obj.getDouble("duration") / 1000)%>

                        </td>
                    </tr>
                    <%
                        }

                    %>
                </table>

                <%-- End of the test result--%>
            </div>
            <!--/row-->
        </div>
        <!--/span-->
    </div>
    <!--/row-->

    <hr>

    <footer>
        <p>&copy; Company 2012</p>
    </footer>

</div>
<!--/.fluid-container-->


</body>
</html>