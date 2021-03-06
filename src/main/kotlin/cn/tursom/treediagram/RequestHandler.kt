package cn.tursom.treediagram

import cn.tursom.treediagram.modinterface.ModException
import cn.tursom.treediagram.modloader.ModManager.getSystemMod
import cn.tursom.treediagram.modloader.ModManager.getUserMod
import cn.tursom.treediagram.usermanage.TokenData
import com.google.gson.Gson
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 用于处理一个模组调用请求
 * @param request Servlet的request对象
 */
fun handle(request: HttpServletRequest?, response: HttpServletResponse): String {
	request?.characterEncoding = "utf-8"
	try {
		//获取token
		val token = request!!.getHeader("token")
				?: request.getParameter("token")
				?: return "{\"state\":false,\"result\":\"no token get\"}"
		//解析token
		val tokenParse = TokenData.parseToken(token)
				?: return "{\"state\":false,\"result\":\"wrong token\"}"
		//获取需要调用的模组名称
		val modName = request.getHeader("mod")
				?: request.getParameter("mod")
				?: return "{\"state\":false,\"result\":\"not given mod name\"}"
		//获取需要调用的模组
		//首先会去系统模组表中查找
		val mod = getSystemMod(modName)
		//没有找到的话就去用户模组表中查找
				?: getUserMod(tokenParse.usr
						?: return "{\"state\":false,\"result\":\"user is null\"}", modName)
				//如果没有找到的话就返回错误信息
				?: return "{\"state\":false,\"result\":\"mod could not found\"}"
		//获取调用结果
		val result = mod.handle(tokenParse, request, response)
		//返回调用结果
		return Gson().toJson(ReturnData(true, result))
	} catch (e: ModException) {
		return "{\"state\":false,\"result\":\"${e.message}\"}"
	} catch (e: Exception) {
		//如果运行中出现任何异常
		//返回异常信息
		return "{\"state\":false,\"result\":\"${e::class.java}${if (e.message != null) ": ${e.message}" else ""}\"}"
	}
}
