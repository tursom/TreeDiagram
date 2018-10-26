<%--
  Created by IntelliJ IDEA.
  User: tursom
  Date: 18-10-24
  Time: 下午8:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>login test</title>
    <script src="https://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
    <script src="http://cdn.bootcss.com/blueimp-md5/1.1.0/js/md5.min.js"></script>
    <script src="https://cdn.bootcss.com/js-sha256/0.9.0/sha256.min.js"></script>
    <script>
        $(document).ready(function () {
            $("#submit").click(function () {
                var username = document.getElementById("username").value;
                var password = sha256(document.getElementById("password").value);
                document.getElementById("output").innerHTML = sha256(username + password + username + password);
                $.post("login.jsp", {
                    "username": username,
                    "password": password
                }, function (data, status) {
                    document.getElementById("output").innerHTML += data;
                    var ret = JSON.parse(data);
                    if (ret.state) {
                        localStorage.token = ret.code;
                    }
                });
            })
        })
    </script>
</head>
<body>
<input id="username" type="text" height="用户名" title="用户名"/>
<input id="password" type="password" height="密码" title="密码"/>
<input id="submit" type="submit"/>
<p id="output"></p>
</body>
</html>
