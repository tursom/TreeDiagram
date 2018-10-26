package cn.tursom.treediagram.usermanage

import cn.tursom.database.SQLAdapter
import cn.tursom.treediagram.system.SystemDatabase
import javax.servlet.http.HttpServletRequest

private fun firstUser(username: String, password: String, level: String) {
	val firstUser = UserData(username, password, level)
	SystemDatabase.database.insert(userTable, firstUser)
}

fun register(request: HttpServletRequest?): String {
	val adapter = SQLAdapter(UserData::class.java)
	SystemDatabase.database.select(adapter, userTable, maxCount = 1)
	return if (adapter.size == 0) {
		val username = request!!.getParameter("username") ?: return "{\"state\":false,\"code\":\"user name is null\"}"
		val password = request.getParameter("password") ?: return "{\"state\":false,\"code\":\"password is null\"}"
		firstUser(username, password, "admin")
		"{\"state\":true,\"code\":\"${TokenData.getToken(username)}\"}"
	} else {
		val token = TokenData.parseToken(request?.getParameter("token")
				?: return "{\"state\":false,\"code\":\"no token get\"}")
				?: return "{\"state\":false,\"code\":\"cant parse token\"}"
		SystemDatabase.database.select(adapter, userTable, Pair("username", token.usr!!), maxCount = 1)
		when {
			adapter.size == 0 -> "{\"state\":false,\"code\":\"token user not found\"}"
			"admin" != adapter[0].level -> "{\"state\":false,\"code\":\"token user not admin\"}"
			else -> {
				val username = request.getParameter("username") ?: return "{\"state\":false,\"code\":\"user name is null\"}"
				val password = request.getParameter("password") ?: return "{\"state\":false,\"code\":\"password is null\"}"
				val level = request.getParameter("level")
				firstUser(username, password, level ?: "user")
				"{\"state\":true,\"code\":\"${TokenData.getToken(username)}\"}"
			}
		}
	}
}