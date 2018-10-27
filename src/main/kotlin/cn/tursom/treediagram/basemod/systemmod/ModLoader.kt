package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.modloader.ModLoader
import cn.tursom.treediagram.usermanage.TokenData

class ModLoader : BaseMod() {
	override fun handle(token: TokenData, message: String?): Any? {
		return if (token.lev == "admin") {
			try {
				val modLoader = ModLoader(message!!, false)
				if (modLoader.load()) {
					"true"
				} else {
					"mod loading error"
				}
			} catch (e: Exception) {
				"${e::class.java}: ${e.message}"
			}
		} else {
			"you are not admin"
		}
	}
}