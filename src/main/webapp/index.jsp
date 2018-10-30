<%--
  User: tursom
  Date: 18-10-24
  Time: 下午8:07
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>login test</title>
	<script src="https://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
	<script src="https://cdn.bootcss.com/blueimp-md5/1.1.0/js/md5.min.js"></script>
	<script src="https://cdn.bootcss.com/js-sha256/0.9.0/sha256.min.js"></script>
	<script>function ajax(page) {
		var username = document.getElementById("username").value;
		var password = sha256(document.getElementById("password").value);
		var token = localStorage.token;
		$.post(page, {"username": username, "password": password, "token": token}, function (data) {
			document.getElementById("output").innerHTML = JSON.stringify(data);
			if (data.state) {
				localStorage.token = data.code;
			}
		});
	}</script>
	<script>$(document).ready(function () {
		$("#submit").click(function () {
			ajax("login.jsp");
		})
	})</script>
	<script>$(document).ready(function () {
		$("#register").click(function () {
			ajax("register.jsp");
		})
	})</script>
</head>
<body>
<input id="username" type="text" placeholder="用户名" title="用户名"/>
<input id="password" type="password" placeholder="密码" title="密码"/>
<button id="submit">登录</button>
<button id="register">注册</button>
<p id="output"></p>
</body>
</html>
