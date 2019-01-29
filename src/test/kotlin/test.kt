import cn.tursom.database.SQLAdapter
import cn.tursom.database.sqlite.SQLiteHelper
import java.util.*

data class Hello(val hello: String?)

fun main(args: Array<String>) {
	val sqlHelper = SQLiteHelper("test.db")
	sqlHelper.insert("hello", Hello("Hello world in ${Date()}"))
	val sqlAdapter = SQLAdapter<Hello>()
	sqlHelper.select(sqlAdapter, "hello")
	sqlAdapter.forEach {
		println(it.hello ?: return@forEach)
	}
}