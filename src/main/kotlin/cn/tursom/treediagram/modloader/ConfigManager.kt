package cn.tursom.treediagram.modloader

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.*

class ConfigManager(private val file: String?) {
	val config by lazy {
		File(file).readText()
	}
	
	fun <T : Any> write(config: T) {
		val outputStream = FileOutputStream(file)
		outputStream.write(Gson().toJson(config).toByteArray())
	}
	
	fun <T : Any> toClass(clazz: Class<T>): T? {
		var configDataJson: T? = null
		val configFile = File(file)
		if (configFile.exists()) {
			try {
				configDataJson = Gson().fromJson(config, clazz)
			} catch (e: JsonSyntaxException) {
				System.err.println("JSON Syntax Error")
				return null
			}
		}
		return configDataJson
	}
	
	inline fun <reified T : Any> toClass(): T? {
		return toClass(T::class.java)
	}
}
