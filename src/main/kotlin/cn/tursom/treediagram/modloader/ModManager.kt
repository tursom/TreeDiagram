package cn.tursom.treediagram.modloader

import cn.tursom.treediagram.modinterface.BaseMod
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import java.util.logging.Logger

object ModManager {
	private val logger = Logger.getLogger("ModManager")!!
	private val systemModMap by lazy {
		val modMap = Hashtable<String, BaseMod>()
		//系统模组列表
		val systemModList = arrayOf(
				cn.tursom.treediagram.basemod.Echo(),
				cn.tursom.treediagram.basemod.Email(),
				cn.tursom.treediagram.basemod.GroupEmail(),
				cn.tursom.treediagram.basemod.MultipleEmail(),
				cn.tursom.treediagram.basemod.ModLoader(),
				cn.tursom.treediagram.basemod.Upload(),
				cn.tursom.treediagram.basemod.MultipleUpload(),
				cn.tursom.treediagram.basemod.GetUploadFileList())
		//加载系统模组
		systemModList.forEach { modObject ->
			//输出日志信息
			logger.info("ModLoader:\nloading mod: ${modObject::class.java.name}")
			//将模组的信息加载到系统中
			modMap[modObject.modName] = modObject
			modMap[modObject.modName.split('.').last()] = modObject
		}
		modMap
	}
	
	private val userModMapMap: Hashtable<String, Hashtable<String, BaseMod>> = Hashtable()
	
	fun getSystemMod(modName: String) = systemModMap[modName]
	
	fun getUserMod(user: String, modName: String) = userModMapMap[user]?.get(modName)
	
	/**
	 * 加载模组
	 * 将模组的注册信息加载进系统中
	 */
	internal fun loadMod(mod: BaseMod) {
		//输出日志信息
		logger.info("ModManager:\nloading mod: ${mod::class.java.name}")
		//调用模组的初始化函数
		mod.init()
		//将模组的信息加载到系统中
		//记得销毁被替代的模组
		systemModMap[mod.modName]?.destroy()
		systemModMap[mod.modName] = mod
		systemModMap[mod.modName.split('.').last()] = mod
	}
	
	/**
	 * 加载模组
	 * 将模组的注册信息加载进系统中
	 */
	fun loadMod(user: String, mod: BaseMod): String {
		//输出日志信息
		logger.info("ModManager:\nloading mod: ${mod::class.java.name}\nuser: $user")
		//调用模组的初始化函数
		mod.init()
		//将模组的信息加载到系统中
		val userModMap = (userModMapMap[user] ?: run {
			val modMap = Hashtable<String, BaseMod>()
			userModMapMap[user] = modMap
			modMap
		})
		//记得销毁被替代的模组
		userModMap[mod.modName]?.destroy()
		userModMap[mod.modName] = mod
		userModMap[mod.modName.split('.').last()] = mod
		return mod.modName
	}
	
	fun loadMod(configData: ClassData, user: String? = null, rootPath: String? = null): Boolean {
		//要加载的类名
		val className: Array<String> = configData.classname!!
		//类加载器
		val myClassLoader: ClassLoader? = try {
			val file = if (rootPath == null) {
				File(configData.path!!)
			} else {
				File(rootPath + configData.path!!)
			}
			//如果文件不存在，抛出一个文件不存在异常
			if (!file.exists()) throw FileNotFoundException()
			val url = file.toURI().toURL()
			URLClassLoader(arrayOf(url), Thread.currentThread().contextClassLoader)
		} catch (e: Exception) {
			//从文件加载模组失败，尝试从网络加载模组
			URLClassLoader(arrayOf(URL(configData.url!!)), Thread.currentThread().contextClassLoader)
		}
		//是否所有的模组都加载成功
		var allSuccessful = true
		className.forEach { className1 ->
			try {
				//获取一个指定模组的对象
				val modClass = myClassLoader!!.loadClass(className1)
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
	
	/**
	 * 卸载模组
	 */
	fun removeMod(user: String, mod: String) {
		//输出日志信息
		logger.info("ModManager:\nremove mod: ${mod::class.java.name}\nuser: $user")
		//找到用户的模组地图
		val userModMap = userModMapMap[user] ?: return
		//找到要卸载的模组
		val modObject = userModMap[mod] ?: return
		//调用卸载方法
		modObject.destroy()
		//删除模组的引用
		userModMap.remove(modObject.modName)
		//删除模组根据简称的引用
		if (modObject === userModMap[modObject.modName.split('.').last()])
			userModMap.remove(modObject.modName.split('.').last())
	}
}