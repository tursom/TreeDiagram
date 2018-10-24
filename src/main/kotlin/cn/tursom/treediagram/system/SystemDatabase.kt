package cn.tursom.treediagram.system

import cn.tursom.database.sqlite.SQLiteHelper

object SystemDatabase {
	private val classPath = SystemDatabase::class.java.getResource("/").path!!
	val baseName = "${classPath}TreeDiagram.db"
	val database = SQLiteHelper(baseName)
}
