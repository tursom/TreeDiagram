package cn.tursom.treediagram.usermanage

import cn.tursom.database.SQLAdapter
import cn.tursom.database.sqlite.SQLiteHelper
import cn.tursom.tools.sha256
import cn.tursom.treediagram.SystemDatabase

data class UserData(val username: String?, val password: String?, val level: String?)

private val database = SQLiteHelper("${UserData::class.java.getResource("/").path!!}TreeDiagram.db")

internal val userTable = run {
	SystemDatabase.database.createTable("users", arrayOf("username TEXT not null", "password TEXT not null", "level TEXT not null"))
	"users"
}

internal fun findUser(username: String): UserData? {
	val adapter = SQLAdapter(UserData::class.java)
	SystemDatabase.database.select(adapter, userTable, Pair("username", username), 1)
	return if (adapter.count() == 0) null
	else adapter[0]
}

fun tryLogin(username: String, password: String): Boolean {
	//查询用户数据
	val userData = findUser(username)
	return "$username$password$username$password".sha256() == userData?.password
}