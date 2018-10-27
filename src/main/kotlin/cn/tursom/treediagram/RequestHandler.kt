package cn.tursom.treediagram

import cn.tursom.treediagram.modloader.ModLoader
import cn.tursom.treediagram.usermanage.TokenData
import com.google.gson.Gson
import javax.servlet.http.HttpServletRequest

data class ReturnData(val state: Boolean?, val result: Any?)

fun handle(request: HttpServletRequest): String {
	val token = request.getParameter("token") ?: return "{\"state\":false,\"result\":\"no token get\"}"
	val tokenParse = TokenData.parseToken(token) ?: return "{\"state\":false,\"result\":\"wrong token\"}"
	val function = request.getParameter("function") ?: "Echo"
	val message = request.getParameter("message")
	return try {
		val result = ModLoader.functionMap[function]?.handle(tokenParse, message)
		Gson().toJson(ReturnData(true, result))
	} catch (e: Exception) {
		"{\"state\":false,\"result\":\"${e::class.java}: ${e.message}\"}"
	}
}
