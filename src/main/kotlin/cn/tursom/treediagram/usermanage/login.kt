package cn.tursom.treediagram.usermanage

import cn.tursom.tools.sha256

fun login(username: String?, password: String?): String {
	return if (username == null) {
		//如果用户名为空
		"{\"state\":false,\"code\":\"user name is null\"}"
	} else if (password == null) {
		//如果密码为空
		"{\"state\":false,\"code\":\"password is null\"}"
	} else {
		//查询用户数据
		Class.forName("cn.tursom.treediagram.usermanage.UserData")
		val userData = findUser(username)
		if ("$password$username$password$username".sha256() == userData?.password) {
			//验证成功，签发token
			"{\"state\":true,\"code\":\"${TokenData.getToken(username)}\"}"
		} else {
			//验证失败
			"{\"state\":false,\"code\":\"wrong password\"}"
		}
	}
}