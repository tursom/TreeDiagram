package cn.tursom.treediagram.modloader

import cn.tursom.treediagram.basemod.BaseMod
import java.util.*
import java.util.logging.Logger

object ModManager {
	private val logger = Logger.getLogger("ModManager")!!
	private val systemModMap by lazy {
		val modMap = Hashtable<String, BaseMod>()
		//系统模组列表
		val systemModList = arrayOf(
				cn.tursom.treediagram.basemod.systemmod.Echo(),
				cn.tursom.treediagram.basemod.systemmod.Email(),
				cn.tursom.treediagram.basemod.systemmod.GroupEmail(),
				cn.tursom.treediagram.basemod.systemmod.MultipleEmail(),
				cn.tursom.treediagram.basemod.systemmod.ModLoader(),
				cn.tursom.treediagram.basemod.systemmod.Upload(),
				cn.tursom.treediagram.basemod.systemmod.GetUploadFileList())
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
		if (!systemModMap.contains(mod.modName)) {
			systemModMap[mod.modName] = mod
			systemModMap[mod.modName.split('.').last()] = mod
		}
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
		userModMap[mod.modName] = mod
		userModMap[mod.modName.split('.').last()] = mod
		return mod.modName
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