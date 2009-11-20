<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="description" content="LegStar COBOL Structure to XML Schema Translator online" />
    <meta name="keywords" content="COBOL, XML, Schema, Translator, LegStar" />
    <link type="text/css" rel="stylesheet" href="Cob2XsdWui.css">
    <title>LegStar COBOL Structure to XML Schema Translator</title>
    <!-- Load GWT javascript only if user is signed in  -->
<%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
%>
    <script type="text/javascript" language="javascript" src="cob2xsdwui/cob2xsdwui.nocache.js"></script>
<%
    }
%>
</head>
<body>
    <a href="http://www.legsem.com/legstar" class="logo"><img src="legstar-logo-2008.png" alt="LegStar" /></a>
    <a href="http://code.google.com/p/legstar-cob2xsd/"><h1>COBOL Structure to XML Schema</h1></a>

<%
    if (user == null) {
%>
    <p>You need to <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a> using your Google account to access this service.</p>
<%
    } else {
%>
    <div id="mainPanel"></div>
    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
<%
    }
%>
    <div class="copyright">
        <p>
            Copyright &#169; 2009 LegSem. All rights reserved.
        </p>
        <p>
            This program is made available under the terms of the <a href="http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html">GNU Lesser Public License v2.1</a>.
            This program is made available in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
        </p>
    </div>
</body>
</html>