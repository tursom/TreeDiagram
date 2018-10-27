package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.usermanage.TokenData

class Echo : BaseMod() {
	override fun handle(token: TokenData, message: String?): Any? {
		println("Echo: $message")
		return message
	}
}