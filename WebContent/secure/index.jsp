<%@ page language="java" import="unl.cse.entities.User" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
	response.sendRedirect(request.getContextPath() + "/secure/dispatchUser");
%><html>
<!-- dummy index page to force a login -->
<body>
<p>These are not the droids you are looking for.</p>
</body>
</html>