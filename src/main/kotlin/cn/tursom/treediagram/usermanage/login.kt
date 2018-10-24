package cn.tursom.treediagram.usermanage

import cn.tursom.tools.sha256

fun login(username: String?, password: String?): String {
	if (username == null) return "{\"state\":false,\"code\":\"user name is null\"}"
	UserData.findUser(username)
	return if (password == "$password$username".sha256()) {
		"{\"state\":true,\"code\":\"${TokenData.getToken(username)}\"}"
	} else {
		"{\"state\":false,\"code\":\"wrong password\"}"
	}
}