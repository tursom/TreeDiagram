package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.modloader.ModLoader

class Println : BaseMod() {
	override fun handle(id: Int, message: String): String {
		println("Println: $id: ${ModLoader.modMap[id]}: $message")
		return "true"
	}
}