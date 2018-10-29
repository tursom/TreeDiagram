package cn.tursom.treediagram

import cn.tursom.treediagram.modloader.ModLoader
import cn.tursom.treediagram.usermanage.TokenData
import com.google.gson.Gson
import javax.servlet.http.HttpServletRequest

/**
 * 模组调用的返回值
 * @param state 是否成功调用模组
 * @param result 模组的返回值，结构由模组自定义，亦可以没有返回值
 */
data class ReturnData(val state: Boolean, val result: Any?)

/**
 * 用于处理一个模组调用请求
 * @param request Servlet的request对象
 */
fun handle(request: HttpServletRequest?): String {
	try {
		//获取token
		val token = request!!.getParameter("token") ?: return "{\"state\":false,\"result\":\"no token get\"}"
		//解析token
		val tokenParse = TokenData.parseToken(token) ?: return "{\"state\":false,\"result\":\"wrong token\"}"
		//获取需要调用的模组名称
		val modName = request.getParameter("mod") ?: "Echo"
		//获取需要调用的模组
		//首先会去系统模组表中查找
		val mod = ModLoader.systemModMap[modName]
		//没有找到的话就去用户模组表中查找
				?: ModLoader.userModMapMap[tokenParse.usr!!]!![modName]
				//还没有找到的话就返回错误信息
				?: return "{\"state\":false,\"result\":\"mod could not found\"}"
		//获取调用结果
		val result = mod.handle(tokenParse, request.parameterMap)
		//返回调用结果
		return Gson().toJson(ReturnData(true, result))
	} catch (e: Exception) {
		//如果运行中出现任何异常
		//返回异常信息
		return "{\"state\":false,\"result\":\"${e::class.java}: ${e.message}\"}"
	}
}
