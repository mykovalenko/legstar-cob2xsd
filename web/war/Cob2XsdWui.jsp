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
    <link rel="stylesheet" type="text/css" href="css/gxt-all.css" /> 
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
<%
    if (user == null) {
%>
    <h1>Welcome to LegStar on Google Application Engine</h1>
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
</body>
</html>