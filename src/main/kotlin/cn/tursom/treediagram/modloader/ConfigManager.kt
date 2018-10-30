package cn.tursom.treediagram.modloader

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.*

/**
 * 用于管理配置的框架
 * 使用JSON格式数据
 * 为了普适性，接收任意来源的BufferedReader与OutputStream
 * 但是getData与writeData必须要相应的流才能正常工作
 */
class ConfigManager(private val bufferedReader: BufferedReader? = null, private val outputStream: OutputStream? = null) {
	constructor(file: File) : this(file.inputStream().bufferedReader(), file.outputStream())
	constructor(config: String) : this(config.byteInputStream().bufferedReader())
	
	val config by lazy {
		val text = bufferedReader?.readText() ?: "{}"
		println(text)
		text
	}
	
	/**
	 * 序列化并写入配置
	 * @param config 用于表示配置的对象
	 */
	fun <T : Any> writeData(config: T) {
		outputStream?.write(Gson().toJson(config).toByteArray())
	}
	
	/**
	 * 读取并逆序列化配置
	 * @param clazz 目标类的对象
	 * @return 加载的配置数据
	 */
	fun <T : Any> getData(clazz: Class<T>): T? {
		//用于储存加载结果的对象
		val configData: T
		return try {
			//加载数据
			configData = Gson().fromJson(config, clazz)
			//返回加载结果
			configData
		} catch (e: JsonSyntaxException) {
			//如果JSON结构出错
			//输出错误信息
			System.err.println("ConfigManager: JSON Syntax Error")
			//返回null
			null
		}
	}
	
	/**
	 * getData的免clazz版本
	 */
	inline fun <reified T : Any> getData(): T? {
		return getData(T::class.java)
	}
}
