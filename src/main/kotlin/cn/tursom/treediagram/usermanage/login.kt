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

private val stackTraceCache = Cache<Array<out StackTraceElement>>(50)

fun getStackTraceCache() = stackTraceCache.copy()

fun tryLogin(username: String, password: String): Boolean {
	stackTraceCache.add(Throwable().stackTrace)
	//查询用户数据
	val userData = findUser(username)
	return "$username$password$username$password".sha256() == userData?.password
}

