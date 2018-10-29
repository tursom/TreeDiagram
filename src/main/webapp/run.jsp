<%@ page import="static cn.tursom.treediagram.RequestHandlerKt.handle" %><%@ page session="false"%><%@ page contentType="application/json;charset=UTF-8" %><%
    out.println(handle(request));
%>