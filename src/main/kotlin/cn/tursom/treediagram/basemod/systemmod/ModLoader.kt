package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.modloader.ModLoader

class ModLoader : BaseMod() {
	override fun handle(id: Int, message: String) =
			try {
				val modLoader = ModLoader(message, false)
				if (modLoader.load()) {
					"true"
				} else {
					"false"
				}
			} catch (e: Exception) {
				e.printStackTrace()
				"false"
			}

//	override fun run() {
//		Thread.sleep(100)
//		val input = System.`in`.bufferedReader()
//		while (true) {
//			Thread.sleep(10)
//			print("模组配置:")
//			val mod = input.readLine()
//			if (mod == "exit") System.exit(0)
//			try {
//				println(mod)
//				println(ConfigManager(mod).getObject(ClassData::class.java))
//				ModLoader(mod)
//			} catch (e: KotlinNullPointerException) {
//				System.err.println("无法加载模组：$mod")
//				e.printStackTrace()
//			} catch (e: Exception) {
//				e.printStackTrace()
//				throw e
//			}
//		}
//	}
}