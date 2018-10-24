package cn.tursom.treediagram.usermanage

import cn.tursom.database.SQLAdapter
import cn.tursom.treediagram.system.SystemDatabase

data class UserData(val username: String?, val password: String?, val level: String?) {
	companion object {
		init {
			SystemDatabase.database.createTable(userTable, arrayOf("username TEXT not null", "password TEXT not null", "level TEXT not null"))
		}
	}
}

private const val userTable = "users"

fun findUser(username: String): UserData? {
	val adapter = SQLAdapter(UserData::class.java)
	SystemDatabase.database.select(adapter, userTable, Pair("username", username), 1)
	return if (adapter.count() == 0) null
	else adapter[0]
}