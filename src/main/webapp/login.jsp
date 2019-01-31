<%@ page import="static cn.tursom.treediagram.usermanage.LoginKt.login" %><%@ page session="false"%><%@ page contentType="application/json;charset=UTF-8" %><%
	request.setCharacterEncoding("utf-8");
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String result = login(username, password);
    out.print(result);
%>