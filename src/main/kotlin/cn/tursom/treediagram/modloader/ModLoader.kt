package cn.tursom.treediagram.modloader

import cn.tursom.tools.fromJson
import cn.tursom.treediagram.basemod.BaseMod
import com.google.gson.Gson
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.net.URLClassLoader
import java.util.logging.Logger

class ModLoader {
	private val configData: ClassData
	private val className: Array<String>
	private val myClassLoader: ClassLoader?

	constructor(config: ConfigManager, loadInstantly: Boolean = true) {
		configData = config.toClass()!!
		className = configData.classname!!
		myClassLoader = try {
			val file = File(configData.path!!)
			if (!file.exists()) throw FileNotFoundException()

			val url = file.toURI().toURL()
			URLClassLoader(arrayOf(url), Thread.currentThread().contextClassLoader)
		} catch (e: Exception) {
			URLClassLoader(arrayOf(URL(configData.url!!)), Thread.currentThread().contextClassLoader)
		}
		if (loadInstantly) {
			load()
		}
	}

	constructor(string: String, loadInstantly: Boolean = true) {
		configData = Gson().fromJson(string)
		className = configData.classname!!
		myClassLoader = when {
			configData.path != null -> {
				val file = File(configData.path)
				if (!file.exists()) throw FileNotFoundException()

				val url = file.toURI().toURL()
				URLClassLoader(arrayOf(url), Thread.currentThread().contextClassLoader)
			}
			configData.url != null -> URLClassLoader(arrayOf(URL(configData.url)), Thread.currentThread().contextClassLoader)
			else -> Thread.currentThread().contextClassLoader
		}
		if (loadInstantly) {
			load()
		}
	}


	fun load(): Boolean {
		var allSuccessful: Boolean = true
		className.forEach { className ->
			try {
				val loadClass = myClassLoader!!.loadClass(className)
				val thisClass = loadClass.getConstructor().newInstance() as BaseMod
				loadMod(thisClass)
			} catch (e: NoSuchMethodException) {
				allSuccessful = false
			}
		}
		return allSuccessful
	}

	companion object {
		val logger = Logger.getLogger("ModLoader")
		val modMap = HashMap<Int, String>()
		val functionMap = HashMap<String?, BaseMod>()

		fun loadMod(mod: BaseMod) {
			logger.info("ModLoader: loading mod: ${mod::class.java.name}")
			modMap[mod.id] = mod::class.java.name
			functionMap[mod.functionName] = mod
			functionMap[mod.functionName.split('.').last()] = mod
		}

		fun loadMod(mod: String) {
			val loadClass = Class.forName(mod)
			val thisClass = loadClass.getConstructor().newInstance() as BaseMod
			loadMod(thisClass)
		}

		private fun loadBaseMod() {
			val baseModList = arrayOf(
					"cn.tursom.smartdata.basemod.systemmod.Println",
					"cn.tursom.smartdata.basemod.systemmod.Exit",
					"cn.tursom.smartdata.basemod.systemmod.SingleEmail",
					"cn.tursom.smartdata.basemod.systemmod.GroupEmail",
					"cn.tursom.smartdata.basemod.systemmod.MultipleEmail",
					"cn.tursom.smartdata.basemod.systemmod.ModLoader")
			baseModList.forEach {
				ModLoader.loadMod(it)
			}
		}

		init {
			loadBaseMod()
		}
	}
}

