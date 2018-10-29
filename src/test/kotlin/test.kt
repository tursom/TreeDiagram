import com.google.gson.Gson

fun main(args: Array<String>) {
	println(Gson().toJson(arrayOf("123", "312213", "213213")))
	Gson().fromJson(Gson().toJson(arrayOf("123", "312213", "213213")), Array<String>::class.java).forEach(::println)
}