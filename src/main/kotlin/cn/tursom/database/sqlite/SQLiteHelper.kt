package cn.tursom.database.sqlite

import cn.tursom.database.SQLAdapter
import cn.tursom.database.SQLHelper
import org.sqlite.SQLiteException
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.logging.Logger


/**
 * MySQLHelper，SQLite辅助使用类
 * 实现创建表格、查询、插入和更新功能
 */

@Suppress("SqlDialectInspection")
class SQLiteHelper
/**
 * 创建名为 base.db 的数据库连接
 */
(base: String) : SQLHelper {
	private val connection: Connection
	private val path = File(base).absolutePath
	
	init {
		synchronized(connectionMap) {
			if (base in connectionMap) {
				connection = connectionMap[path]!!
			} else {
				connection = DriverManager.getConnection("jdbc:sqlite:$base") ?: throw CantConnectDataBase()
				connectionMap[path] = connection
				connection.autoCommit = false
			}
			connectionCount[path] = connectionCount[path] ?: 0 + 1
		}
	}
	
	
	/**
	 * 创建表格
	 * @param table: 表格名
	 * @param keys: 属性列表
	 */
	override fun createTable(table: String, keys: Array<String>) {
		println("CREATE TABLE if not exists $table (${toColumn(keys)})")
		val statement = connection.createStatement()
		statement.executeUpdate("CREATE TABLE if not exists $table (${toColumn(keys)})")
		commit()
	}
	
	/**
	 * 根据提供的class对象自动化创建表格
	 * 但是有诸多缺陷，所以不是很建议使用
	 */
	override fun <T> createTable(table: String, keys: Class<T>) {
		val keysArray = ArrayList<String>()
		keys.declaredFields.forEach {
			keysArray.add("${it.name} ${it.type.toString().split(".").last().toUpperCase()}")
		}
		createTable(table, keysArray.toTypedArray())
	}
	
	/**
	 * 删除表格
	 */
	override fun deleteTable(table: String) {
		val statement = connection.createStatement()
		statement.executeUpdate("DROP TABLE if exists $table")
		commit()
	}
	
	/**
	 * 删除表格
	 */
	override fun dropTable(table: String) {
		deleteTable(table)
	}
	
	/**
	 * 查询
	 * @param adapter 用于保存查询结果的数据类，由SQLAdapter继承而来
	 * @param table 表名
	 * @param name 查询字段
	 * @param where 指定从一个表或多个表中获取数据的条件,Pair左边为字段名，右边为限定的值
	 * @param maxCount 最大查询数量
	 */
	override fun <T : Any> select(
			adapter: SQLAdapter<T>, table: String,
			name: Array<String>, where: Map<String, String>?, maxCount: Int?) {
		if (where != null) {
			select(adapter, table, toColumn(name), toWhere(where), maxCount)
		} else {
			select(adapter, table, toColumn(name), maxCount = maxCount)
		}
	}
	
	/**
	 * 查询
	 * @param adapter 用于保存查询结果的数据类，由SQLAdapter继承而来
	 * @param table 表名
	 * @param name 查询字段
	 * @param where 指定从一个表或多个表中获取数据的条件,Pair左边为字段名，右边为限定的值
	 * @param maxCount 最大查询数量
	 */
	override fun <T : Any> select(
			adapter: SQLAdapter<T>, table: String,
			where: Pair<String, String>, maxCount: Int?, name: Array<String>) {
		select(adapter, table, name, mapOf(where), maxCount)
	}
	
	override fun <T : Any> select(
			adapter: SQLAdapter<T>, table: String, name: String, where: String?, maxCount: Int?
	) {
		val statement = connection.createStatement()
		try {
			@Suppress("SqlResolve", "SqlIdentifier")
			adapter.adapt(
					if (where == null)
						statement.executeQuery("SELECT $name FROM $table limit 0,${maxCount ?: Int.MAX_VALUE};")
					else
						statement.executeQuery("SELECT $name FROM $table WHERE $where limit 0,${maxCount ?: Int.MAX_VALUE};")
			)
		} catch (e: SQLiteException) {
			if (e.message != "[SQLITE_ERROR] SQL error or missing database (no such table: $table)") throw e
		}
		statement.closeOnCompletion()
	}
	
	override fun <T : Any> reverseSelect(
			adapter: SQLAdapter<T>, table: String,
			name: Array<String>, where: Map<String, String>?, index: String, maxCount: Int?) {
		if (where != null) {
			reverseSelect(adapter, table, toColumn(name), toWhere(where), index, maxCount)
		} else {
			reverseSelect(adapter, table, toColumn(name), index = index, maxCount = maxCount)
		}
	}
	
	override fun <T : Any> reverseSelect(
			adapter: SQLAdapter<T>, table: String,
			name: Array<String>, where: Pair<String, String>, index: String, maxCount: Int?) {
		reverseSelect(adapter, table, name, mapOf(where), index, maxCount)
	}
	
	override fun <T : Any> reverseSelect(
			adapter: SQLAdapter<T>, table: String, name: String, where: String?, index: String, maxCount: Int?
	) {
		val statement = connection.createStatement()
		try {
			@Suppress("SqlResolve", "SqlIdentifier")
			adapter.adapt(
					if (where == null)
						statement.executeQuery("SELECT $name FROM $table ORDER BY $index DESC limit 0,${maxCount
								?: Int.MAX_VALUE};")
					else
						statement.executeQuery("SELECT $name FROM $table WHERE $where ORDER BY $index DESC limit 0,${maxCount
								?: Int.MAX_VALUE};")
			)
		} catch (e: SQLiteException) {
			if (e.message != "[SQLITE_ERROR] SQL error or missing database (no such table: $table)") throw e
		}
		statement.closeOnCompletion()
	}
	
	/**
	 * 插入
	 * @param table 表名
	 * @param value 值
	 */
	override fun <T : Any> insert(table: String, value: T) {
		val valueMap = HashMap<String, String>()
		value.javaClass.declaredFields.forEach {
			if (getFieldValueByName(it.name, value) == null) {
				return@forEach
			}
			if (it.type == java.util.Date::class.java) {
				valueMap[it.name] = java.sql.Timestamp((getFieldValueByName(it.name, value) as java.util.Date).time).toString()
			} else {
				valueMap[it.name] = getFieldValueByName(it.name, value).toString()
			}
		}
		try {
			insert(table, valueMap)
		} catch (e: SQLiteException) {
			if (e.message == "[SQLITE_ERROR] SQL error or missing database (no such table: $table)") {
				createTable(table, value.javaClass)
				insert(table, valueMap)
			} else {
				e.printStackTrace()
			}
		}
	}
	
	override fun insert(table: String, column: Map<String, String>) {
		val columns = toKeys(column)
		insert(table, columns.first, columns.second)
	}
	
	override fun insert(table: String, column: String, values: String) {
		val statement = connection.createStatement()
		val sql = "INSERT INTO $table ($column) VALUES ($values)"
		statement.executeUpdate(sql)
		commit()
		statement.closeOnCompletion()
	}
	
	override fun update(
			table: String,
			set: Map<String, String>,
			where: Map<String, String>) {
		val statement = connection.createStatement()
		statement.executeUpdate("UPDATE $table SET ${toValue(set)} WHERE ${toWhere(where)};")
		commit()
		statement.closeOnCompletion()
	}
	
	override fun <T : Any> update(
			table: String, value: T, where: Map<String, String>
	) {
		val set = HashMap<String, String>()
		value.javaClass.declaredFields.forEach {
			if (getFieldValueByName(it.name, value) == null) {
				return@forEach
			}
			if (it.type == java.util.Date::class.java) {
				set[it.name] = java.sql.Timestamp((getFieldValueByName(it.name, value) as java.util.Date).time).toString()
			} else {
				set[it.name] = getFieldValueByName(it.name, value).toString()
			}
		}
		update(table, set, where)
	}
	
	override fun delete(table: String, where: String) {
		val statement = connection.createStatement()
		statement.executeUpdate("DELETE FROM $table WHERE $where;")
		commit()
		statement.closeOnCompletion()
	}
	
	override fun delete(table: String, where: Map<String, String>) {
		delete(table, toWhere(where))
	}
	
	override fun delete(table: String, where: Pair<String, String>) {
		delete(table, mapOf(where))
	}
	
	override fun commit() {
		synchronized(connection) {
			connection.commit()
		}
	}
	
	override fun close() {
		synchronized(connectionMap) {
			connectionCount[path] = connectionCount[path] ?: 1 - 1
			if (connectionCount[path] == 0) {
				connectionCount.remove(path)
				connectionMap.remove(path)
				connection.close()
			}
		}
	}
	
	private fun toKeys(columns: Map<String, String>): Pair<String, String> {
		val column = StringBuilder()
		val value = StringBuilder()
		columns.forEach {
			if (it.key.isNotEmpty() && it.value.isNotEmpty()) {
				column.append("${it.key},")
				value.append("'${it.value.replace("'", "''")}',")
			}
		}
		column.delete(column.length - 1, column.length)
		value.delete(value.length - 1, value.length)
		return Pair(column.toString(), value.toString())
	}
	
	private fun toColumn(column: Array<out String>): String {
		val stringBuilder = StringBuilder()
		column.forEach {
			if (it.isNotEmpty())
				stringBuilder.append("$it,")
		}
		stringBuilder.delete(stringBuilder.length - 1, stringBuilder.length)
		return stringBuilder.toString()
	}
	
	private fun toValue(where: Map<String, String>): String {
		val stringBuilder = StringBuilder()
		where.forEach {
			if (it.key.isNotEmpty() && it.value.isNotEmpty())
				stringBuilder.append("${it.key}='${it.value.replace("'", "''")}',")
		}
		if (stringBuilder.isNotEmpty())
			stringBuilder.delete(stringBuilder.length - 1, stringBuilder.length)
		return stringBuilder.toString()
	}
	
	private fun toWhere(where: Map<String, String>): String {
		val stringBuilder = StringBuilder()
		where.forEach {
			if (it.key.isNotEmpty() && it.value.isNotEmpty())
				stringBuilder.append("${it.key}='${it.value.replace("'", "''")}' AND ")
		}
		if (stringBuilder.isNotEmpty())
			stringBuilder.delete(stringBuilder.length - 5, stringBuilder.length)
		return stringBuilder.toString()
	}
	
	/**
	 * 根据属性名获取属性值
	 */
	private fun getFieldValueByName(fieldName: String, o: Any): Any? {
		return try {
			val firstLetter = fieldName.substring(0, 1).toUpperCase()
			val getter = "get" + firstLetter + fieldName.substring(1)
			val method = o.javaClass.getMethod(getter)
			method.invoke(o)
		} catch (e: Exception) {
			e.printStackTrace()
			logger.warning("${e.message}: $fieldName")
			null
		}
		
	}
	
	
	class CantConnectDataBase(s: String? = null) : SQLException(s)
	
	companion object {
		private val logger = Logger.getLogger("sqlite")!!
		private val connectionMap by lazy {
			Class.forName("org.sqlite.JDBC")
			HashMap<String, Connection>()
		}
		private var connectionCount = HashMap<String, Int>()
	}
}
