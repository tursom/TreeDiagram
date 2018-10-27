package cn.tursom.treediagram.usermanage

import cn.tursom.tools.sha256

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
		//查询用户数据
		val userData = findUser(username)
		if ("$username$password$username$password".sha256() == userData?.password) {
			//验证成功，签发token
			"{\"state\":true,\"code\":\"${TokenData.getToken(username)}\"}"
		} else {
			//验证失败
			"{\"state\":false,\"code\":\"wrong password\"}"
		}
	}
}