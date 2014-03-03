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
<%@ page isErrorPage = "true"%>
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

<div class="container-fluid">
    <div class="row-fluid">
        <jsp:include page="includes/left-menu.jsp"/>
        <div class="span10">
            <div class="hero-unit">
                <%--Test build content goes here--%>
                <h3>Exception Occurred</h3>


                <%--end of the content--%>

            </div>

             <div class="hero-unit">
                 <%= exception.toString() %>
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