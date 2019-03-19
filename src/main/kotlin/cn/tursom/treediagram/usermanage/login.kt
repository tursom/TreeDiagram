package cn.tursom.treediagram.usermanage

/**
 * 验证用户名和密码并签发token
 * 返回的固定是一个json数据
 * state表示登录是否成功
 * code在成功状态下是token
 *     在失败状态下是失败原因
 */
fun login(username: String?, password: String?): String {
	return if (username == null) {
		//如果用户名为空
		"{\"state\":false,\"code\":\"user name is null\"}"
	} else if (password == null) {
		//如果密码为空
		"{\"state\":false,\"code\":\"password is null\"}"
	} else {
		//试签发token
		val token = TokenData.getToken(username, password)
		if (token != null) {
			//成功通过验证，获得token
			"{\"state\":true,\"code\":\"$token\"}"
		} else {
			//验证失败
			"{\"state\":false,\"code\":\"wrong username or password\"}"
		}
	}
}

