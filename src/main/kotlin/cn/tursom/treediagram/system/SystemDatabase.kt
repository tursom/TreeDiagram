package cn.tursom.treediagram.system

import cn.tursom.database.sqlite.SQLiteHelper

object SystemDatabase {
	private val classPath = SystemDatabase::class.java.getResource("/").path!!
	private val baseName = "${classPath}TreeDiagram"
	val database = SQLiteHelper(baseName)
}
