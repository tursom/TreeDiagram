<%--
  Created by IntelliJ IDEA.
  User: tursom
  Date: 18-10-24
  Time: 下午9:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    //获取用户名
    String username = request.getParameter("username");
    //获取密码（MD5过后）
    String password = request.getParameter("password");
%>