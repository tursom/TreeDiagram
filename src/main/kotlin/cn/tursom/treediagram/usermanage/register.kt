package cn.tursom.treediagram.usermanage

import cn.tursom.database.SQLAdapter
import cn.tursom.tools.sha256
import cn.tursom.treediagram.SystemDatabase
import javax.servlet.http.HttpServletRequest

/**
 * 本文件用来处理当用户尝试注册一个新用户时的逻辑
 */

/**
 * 添加一个新用户
 * @param username 新用户用户名
 * @param password 新用户密码
 * @param level 新用户权限等级
 */
private fun addUser(username: String, password: String, level: String) {
	val firstUser = UserData(username, "$username$password$username$password".sha256(), level)
	SystemDatabase.database.insert(userTable, firstUser)
}

/**
 * 是否是一个刚建立的，没有任何数据的服务器
 */
private val newServer by lazy {
	//首先尝试从数据库中取一条数据
	val adapter = SQLAdapter(UserData::class.java)
	SystemDatabase.database.select(adapter, userTable, maxCount = 1)
	//判断是否真的有数据
	adapter.size == 0
}

/**
 * 用于处理注册逻辑的函数
 * 第一个注册的用户不需要验证，并且固定是admin权限
 * 后续注册的用户需要有admin权限的token
 * 需要username，password以及level（可选，默认user）来注册一个新用户
 * @param request 请求的request对象
 * @return 处理结果，json数据
 */
fun register(request: HttpServletRequest?): String {
	request?.characterEncoding = "utf-8"
	//如果数据库内无任何用户，则可以直接创建一个admin权限的用户
	return if (newServer) {
		//如果没有数据，说明现在没有任何用户注册
		//可以直接创建一个admin权限的用户
		//获取新用户用户名
		val username = request!!.getParameter("username") ?: return "{\"state\":false,\"code\":\"user name is null\"}"
		//获取新用户密码
		val password = request.getParameter("password") ?: return "{\"state\":false,\"code\":\"password is null\"}"
		//添加一个admin权限的新用户
		addUser(username, password, "admin")
		//返回成功信息
		"{\"state\":true,\"code\":\"${TokenData.getToken(username, password)}\"}"
	} else {
		//解析token
		val token = TokenData.parseToken(request?.getParameter("token")
				?: return "{\"state\":false,\"code\":\"no token get\"}")  //客户端没有发送token
				?: return "{\"state\":false,\"code\":\"cant parse token\"}" //token无法解析
		when {
			//用户权限不是admin
			"admin" != token.lev -> "{\"state\":false,\"code\":\"token user not admin\"}"
			//满足以上两个调解则注册新用户
			else -> {
				//获取新用户用户名
				val username = request.getParameter("username") ?: return "{\"state\":false,\"code\":\"user name is null\"}"
				//获取新用户密码
				val password = request.getParameter("password") ?: return "{\"state\":false,\"code\":\"password is null\"}"
				//获取要注册的用户的权限
				val level = request.getParameter("level")
				//添加新用户
				addUser(username, password, level ?: "user")
				//返回成功信息
				"{\"state\":true,\"code\":\"${TokenData.getToken(username, password)}\"}"
			}
		}
	}
}