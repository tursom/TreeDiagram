package cn.tursom.treediagram.usermanage

import cn.tursom.treediagram.system.SystemDatabase

data class UserData(val username: String?, val password: String?, val level: String?) {
	companion object {
		private const val userTable = "users"
		
		init {
			SystemDatabase.database.createTable(userTable, UserData::class.java)
		}
	}
}