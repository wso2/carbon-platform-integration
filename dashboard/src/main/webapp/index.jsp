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

    <meta http-equiv="refresh" content="60">
    <script language="javascript" src="js/jquery.min.js"></script>
    <script language="javascript" src="js/jquery.jqplot.min.js"></script>
    <script type="text/javascript" src="js/jqplot.barRenderer.min.js"></script>

    <script type="text/javascript" src="js/jqplot.categoryAxisRenderer.min.js"></script>
    <script type="text/javascript" src="js/jqplot.pointLabels.min.js"></script>

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
        JSONArray build = dp.getBuildHistory();
        JSONObject lastBuild = dp.getLastBuild();
        if (lastBuild != null) {
    %>

    <script type="text/javascript">
        $(document).ready(function () {
            var pass = [
                <%=Utils.getDataAsAList(build, Constant.PASS)%>];
            var fail = [
                <%=Utils.getDataAsAList(build, Constant.FAIL)%>];
            var skip = [
                <%=Utils.getDataAsAList(build, Constant.SKIP)%>];
            // Can specify a custom tick Array.
            // Ticks should match up one for each y value (category) in the series.
            var ticks = [
                <%=Utils.getDataAsAList(build, "build")%>];

            var plot1 = $.jqplot('chart1', [pass, fail, skip], {

                seriesColors:[ "#4ea12e", "#d9261b", "#FACB1F"],
                title:{
                    text:'Build Chart', // title for the plot,
                    show:true
                },

                // The "seriesDefaults" option is an options object that will
                // be applied to all series in the chart.
                seriesDefaults:{
                    renderer:$.jqplot.BarRenderer,
                    rendererOptions:{
                        barPadding:3, // number of pixels between adjacent bars in the same
                        // group (same category or bin).
                        barMargin:10, // number of pixels between adjacent groups of bars.
                        barDirection:'vertical', // vertical or horizontal.
                        barWidth:30, // width of the bars.  null to calculate automatically.
                        shadowOffset:0    // offset from the bar edge to stroke the shadow.
                        //shadowDepth: 5,     // nuber of strokes to make for the shadow.
                        //shadowAlpha: 0.8   // transparency of the shadow.
                    },
                    showMarker:false,
                    pointLabels:{ show:true }
                },
                // Custom labels for the series are specified with the "label"
                // option on the series option.  Here a series option object
                // is specified for each series.
                series:[
                    {label:'Passed'},
                    {label:'Failed'},
                    {label:'Skiped'}
                ],
                // Show the legend and put it outside the grid, but inside the
                // plot container, shrinking the grid to accomodate the legend.
                // A value of "outside" would not shrink the grid and allow
                // the legend to overflow the container.
                legend:{
                    show:true,

                    placement:'outsideGrid'

                },
                axes:{
                    // Use a category axis on the x axis and use our custom ticks.
                    xaxis:{
                        label:'Build #',
                        renderer:$.jqplot.CategoryAxisRenderer,
                        ticks:ticks
                    },
                    // Pad the y axis just a little so bars can get close to, but
                    // not touch, the grid boundaries.  1.2 is the default padding.
                    yaxis:{
                        pad:1.00,
                        tickOptions:{formatString:'%d'}
                    }
                }
            });

            var data1 = [
                <%=Utils.getPieChartData(lastBuild)%>
            ];
            var plot2 = jQuery.jqplot('pieChart', [data1],
                                      {
                                          seriesColors:[ "#4ea12e", "#d9261b", "#FACB1F"],
                                          title:{
                                              text:'Last Build #<%=lastBuild.get("build")%>', // title for the plot
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
    <%
        }
    %>

</head>

<body>

<jsp:include page="includes/header.jsp"/>

<div class="container-fluid">
    <div class="row-fluid">
        <jsp:include page="includes/left-menu.jsp"/>
        <div class="span10">
            <%
                if (lastBuild == null) {
            %>
            <div class="hero-unit">
                <h2>No Test Result Found</h2>
            </div>
            <%
            } else {
            %>
            <div class="hero-unit">
                <%
                    if (lastBuild == null) {
                %>
                <h2>No Test Result Found</h2>
                <%
                    }
                %>
                <h2>Test Builds</h2>
                <table width="100%">
                    <tr>
                        <td width="25%">
                            <div id="pieChart"></div>
                        </td>
                        <td width="5%">

                        </td>
                        <td width="70%">
                            <div id="chart1"></div>
                        </td>
                    </tr>
                </table>

                <%--end of the content--%>

            </div>

            <div class="hero-unit">
                <h2>Test Results</h2>

                <%-- <p>Test result content goes here</p>--%>

                <table class="table">
                    <tr>
                        <th><b>Build</b></th>
                        <th><b>Time</b></th>
                        <th><b>Total</b></th>
                        <th><b>Passed</b></th>
                        <th><b>Failed</b></th>
                        <th><b>Skipped</b></th>
                        <th><b>Success Rate(%)</b></th>
                        <th><b>Duration(min)</b></th>
                    </tr>
                    <%
                        Iterator iterator = build.iterator();
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
                            <a href="testSuite.jsp?build=<%=buildData.get("build")%>"><%=buildData.get("build")%>
                            </a>
                        </td>
                        <td>
                            <%=buildData.get("startTime")%>

                        </td>
                        <td>
                            <%
                                int total = (buildData.getInt(Constant.PASS) + buildData.getInt(Constant.FAIL) + buildData.getInt(Constant.SKIP));
                            %>
                            <%=total%>

                        </td>
                        <td>
                            <a href="testCasesByBuild.jsp?build=<%=buildData.get("build")%>&state=PASS"><%=buildData.getString(Constant.PASS)%>
                            </a>
                        </td>
                        <td>
                            <a href="testCasesByBuild.jsp?build=<%=buildData.get("build")%>&state=FAIL"><%=buildData.getString(Constant.FAIL)%>
                            </a>
                        </td>
                        <td>
                            <a href="testCasesByBuild.jsp?build=<%=buildData.get("build")%>&state=SKIP"><%=buildData.getString(Constant.SKIP)%>
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
                        <td>
                            <%
                                double duration = buildData.getDouble("duration") / (1000 * 60);
                            %>
                            <%=Utils.round(duration) %>

                        </td>
                    </tr>
                    <%
                        }

                    %>
                </table>

                <%-- End of the test result--%>
            </div>
            <%
                }
            %>
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