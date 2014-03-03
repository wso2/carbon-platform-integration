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
<%@ page import="net.sf.json.JSONObject" %>
<%@ page import="org.wso2.dashboard.Utils" %>
<%@ page import="org.wso2.dashboard.Constant" %>
<%@ page import="java.util.Iterator" %>
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

    <script language="javascript" src="js/jquery.min.js"></script>
    <script language="javascript" src="js/jquery.jqplot.min.js"></script>
    <script type="text/javascript" src="js/jqplot.barRenderer.min.js"></script>


    <script type="text/javascript" src="js/jqplot.pieRenderer.min.js"></script>
    <script type="text/javascript" src="js/jqplot.donutRenderer.min.js"></script>

    <link rel="stylesheet" type="text/css" href="css/jquery.jqplot.css"/>

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

    <%
        DataProvider dp = new DataProvider();
        String buildNo = request.getParameter("build");
        JSONObject build = dp.getBuild(buildNo);
    %>

    <script type="text/javascript">
        $(document).ready(function () {
            var data = [
                <%=Utils.getPieChartData(build)%>
            ];
            var plot1 = jQuery.jqplot('pieChart', [data],
                                      {
                                          seriesColors:[ "#4ea12e", "#d9261b", "#FACB1F"],
                                          title:{
                                              text:'Build #<%=build.getString("build")%>', // title for the plot,
                                              show:true
                                          },

                                          seriesDefaults:{
                                              // Make this a pie chart.
                                              renderer:jQuery.jqplot.PieRenderer,
                                              rendererOptions:{
                                                  // Put data labels on the pie slices.
                                                  // By default, labels show the percentage of the slice.
                                                  showDataLabels:true
                                              }
                                          },
                                          legend:{ show:true, location:'e' }
                                      }
            );
        });
    </script>

</head>

<body>

<jsp:include page="includes/header.jsp"/>

<div class="container-fluid">
    <div class="row-fluid">
        <jsp:include page="includes/left-menu.jsp"/>
        <div class="span10">
            <div class="hero-unit">
                <%--Test build content goes here--%>
                <h2>Test Build#<%=buildNo%>
                </h2>

                <h3>Passed :<%=build.get("pass")%>
                </h3>

                <h3>Failed :<%=build.get("fail")%>
                </h3>

                <h3>Skipped :<%=build.get("skip")%>
                </h3>

                <%--end of the content--%>

            </div>
            <div class="hero-unit">
                <h2>Test Results</h2>

                <%-- <p>Test result content goes here</p>--%>

                <table width="100%">
                    <tr valign="top">
                        <td>

                            <table class="table">
                                <tr>
                                    <th><b>Test Suite</b></th>
                                    <th><b>Total</b></th>
                                    <th><b>Passed</b></th>
                                    <th><b>Failed</b></th>
                                    <th><b>Skipped</b></th>
                                    <th><b>Success Rate(%)</b></th>
                                    <th><b>Duration(min)</b></th>
                                    <%--<td><b>Skip</b></td>--%>
                                </tr>
                                <%

                                    JSONArray ja = dp.getTestSuites(Integer.parseInt(buildNo));
                                    Iterator iterator = ja.iterator();
                                    while (iterator.hasNext()) {
                                        JSONObject buildData = (JSONObject) iterator.next();
                                %>
                                <tr <%if(buildData.getInt(Constant.FAIL) > 0)
                        {%>class=fail <%
                                } else {
                                %> class=pass<%
                                    }
                                %>>
                                    <td>
                                        <a href="testClasses.jsp?build=<%=buildData.get("build")%>&suite=<%=buildData.get("suite")%>"><%=buildData.get("suiteName")%>
                                        </a>
                                    </td>
                                    <td>
                                        <%
                                            int total = (buildData.getInt(Constant.PASS) + buildData.getInt(Constant.FAIL) + buildData.getInt(Constant.SKIP));
                                        %>
                                        <%=total%>

                                    </td>

                                    <td>
                                        <a href="testCaseBySuite.jsp?suite=<%=buildData.get("suite")%>&status=PASS"><%=buildData.get("pass")%>
                                        </a>

                                    </td>


                                    <td>
                                        <a href="testCaseBySuite.jsp?suite=<%=buildData.get("suite")%>&status=FAIL"><%=buildData.get("fail")%>
                                        </a>
                                    </td>


                                    <td>
                                        <a href="testCaseBySuite.jsp?suite=<%=buildData.get("suite")%>&status=SKIP"><%=buildData.get("skip")%>
                                        </a>
                                    </td>

                                    <td>
                                        <%
                                            double sr = 0;
                                            if (total > 0) {
                                                sr = (buildData.getDouble(Constant.PASS) / total) * 100;
                                            }
                                        %>
                                        <%=Utils.round(sr) %>

                                    </td>

                                    <td><%=Utils.round(buildData.getDouble("duration") / (1000 * 60))%>
                                    </td>


                                </tr>
                                <%

                                    }

                                %>
                            </table>
                        </td>
                        <td align="left" width="30">
                            <div id="pieChart" align="center"
                                 style="width: 400px; height: 300px;"></div>
                        </td>
                    </tr>

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