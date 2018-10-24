<%@ page import="static cn.tursom.treediagram.usermanage.LoginKt.login" %><%--
  Created by IntelliJ IDEA.
  User: tursom
  Date: 18-10-24
  Time: 下午9:21
  用于用户登录
  返回json数据，拥有state和code两个属性
  state是一个布林值，表示是否成功
  code如果登录成功则是签发的token，失败则是失败原因
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    //获取用户名
    String username = request.getParameter("username");
    //获取密码（MD5过后）
    String password = request.getParameter("password");
    //验证结果
    String result = login(username, password);
    //返回验证结果
    out.print(result);
%>
