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
    String testClass = request.getParameter("testClass");
    String status = request.getParameter("status");
%>

<div class="container-fluid">
    <div class="row-fluid">
        <jsp:include page="includes/left-menu.jsp"/>
        <div class="span10">
            <div class="hero-unit">
                <%--Test build content goes here--%>
                <%
                    JSONObject testClassInfo = dp.getTestClass(Integer.parseInt(testClass));
                %>
                <h2>Test Class :<%=testClassInfo.get("className")%>
                </h2>


                <%--end of the content--%>

            </div>
            <div class="hero-unit" style="overflow-x: auto">
                <h2>Test Results</h2>

                <%-- <p>Test result content goes here</p>--%>

                <table class="table">
                <tr>
                    <th><b>Build</b></th>
                    <th><b>Test Class</b></th>
                    <th><b>Test Case</b></th>
                    <th><b>Status</b></th>
                    <%
                        if (status.equalsIgnoreCase("fail")) {
                    %>
                    <th><b>Error Type</b></th>
                    <th><b>Message</b></th>
                    <th><b>Stack Trace</b></th>
                    <%
                        }
                    %>
                    <th><b>Duration (mills)</b></th>
                </tr>
                <%

                    JSONArray ja = dp.getTestCasesByClass(testClass, status);
                    Iterator iterator = ja.iterator();
                    while (iterator.hasNext()) {
                        JSONObject testCase = (JSONObject) iterator.next();
                %>
                <tr <%if(status.equalsIgnoreCase("FAIL")) {%>class="fail" <%}
                else if((status.equalsIgnoreCase("PASS"))){%> class="pass" <%} else {%> class="skip" <%}%>>
                    <td>
                        <%=testCase.get("build")%>

                    </td>
                    <td>
                        <%=testCase.get("testClass")%>

                    </td>
                    <td><%=testCase.get("testCase")%>

                    </td>
                    <td><%=testCase.get("status")%>

                    </td>
                    <%
                        if (status.equalsIgnoreCase("fail")) {
                    %>
                    <td><%=testCase.get("errorType")%>
                    <td><%=testCase.get("message")%>
                    <td><a href="viewStackTrace.jsp?id=<%=testCase.get("stackTrace")%>">View</a>
                        <%
                            }
                        %>
                    </td>
                    <td><%=testCase.get("duration")%>

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