package cn.tursom.treediagram.modloader

import cn.tursom.treediagram.basemod.BaseMod
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import java.util.logging.Logger

/**
 * 用于加载模组
 * 模组可由网络或者本地加载
 * 亦可将配置写入一个文件中
 * 会优先尝试从本地加载模组
 * 本地文件不存在则会从网络加载模组
 *
 * @param config 配置管理器
 * @param loadInstantly 是否立即加载，默认为真
 */
class ModLoader(config: ConfigManager, private val user: String? = null, loadInstantly: Boolean = true) {
	//配置数据
	private val configData: ClassData = config.getData()!!
	//要加载的类名
	private val className: Array<String> = configData.classname!!
	//类加载器
	private val myClassLoader: ClassLoader? = try {
		val file = File(configData.path!!)
		//如果文件不存在，抛出一个文件不存在异常
		if (!file.exists()) throw FileNotFoundException()
		val url = file.toURI().toURL()
		URLClassLoader(arrayOf(url), Thread.currentThread().contextClassLoader)
	} catch (e: Exception) {
		//从文件加载模组失败，尝试从网络加载模组
		URLClassLoader(arrayOf(URL(configData.url!!)), Thread.currentThread().contextClassLoader)
	}
	
	init {
		//如果需要立即加载模组
		//则会在对象构造时自动加载模组
		if (loadInstantly) {
			//自动加载模组
			load()
		}
	}
	
	/**
	 * 辅助构造函数
	 * config是一个ClassData类的json格式对象
	 */
	constructor(config: String, user: String? = null, loadInstantly: Boolean = true) : this(ConfigManager(config), user, loadInstantly)
	
	/**
	 * 手动加载模组
	 * @return 是否所有的模组都加载成功
	 */
	fun load(): Boolean {
		//是否所有的模组都加载成功
		var allSuccessful = true
		className.forEach { className ->
			try {
				//获取一个指定模组的对象
				val modClass = myClassLoader!!.loadClass(className)
				val modObject = modClass.getConstructor().newInstance() as BaseMod
				//加载模组
				if (user == null)
					loadMod(modObject)
				else {
					loadMod(user, modObject)
				}
			} catch (e: NoSuchMethodException) {
				//如果失败，将标志位置否
				allSuccessful = false
			}
		}
		return allSuccessful
	}
	
	companion object {
		private val logger = Logger.getLogger("ModLoader")!!
		val systemModMap by lazy {
			val modMap = Hashtable<String, BaseMod>()
			//系统模组列表
			val systemModList = arrayOf(
					"cn.tursom.treediagram.basemod.systemmod.Echo",
					"cn.tursom.treediagram.basemod.systemmod.SingleEmail",
					"cn.tursom.treediagram.basemod.systemmod.GroupEmail",
					"cn.tursom.treediagram.basemod.systemmod.MultipleEmail",
					"cn.tursom.treediagram.basemod.systemmod.ModLoader")
			//加载系统模组
			systemModList.forEach { modName ->
				//获取模组对象
				val modClass = Class.forName(modName)
				val modObject = modClass.getConstructor().newInstance() as BaseMod
				//输出日志信息
				logger.info("ModLoader: loading mod: ${modName::class.java.name}")
				//将模组的信息加载到系统中
				modMap[modObject.modName] = modObject
				modMap[modObject.modName.split('.').last()] = modObject
			}
			modMap
		}
		
		val userModMapMap: Hashtable<String, Hashtable<String, BaseMod>> = Hashtable()
		
		/**
		 * 加载模组
		 * 将模组的注册信息加载进系统中
		 */
		fun loadMod(mod: BaseMod) {
			//输出日志信息
			logger.info("ModLoader: loading mod: ${mod::class.java.name}")
			//将模组的信息加载到系统中
			systemModMap[mod.modName] = mod
			systemModMap[mod.modName.split('.').last()] = mod
		}
		
		/**
		 * 加载模组
		 * 将模组的注册信息加载进系统中
		 */
		fun loadMod(user: String, mod: BaseMod) {
			//输出日志信息
			logger.info("ModLoader: loading mod: ${mod::class.java.name}")
			//将模组的信息加载到系统中
			val userModMap = (userModMapMap[user] ?: run {
				val modMap = Hashtable<String, BaseMod>()
				userModMapMap[user] = modMap
				modMap
			})
			userModMap[mod.modName] = mod
			userModMap[mod.modName.split('.').last()] = mod
		}
	}
}

