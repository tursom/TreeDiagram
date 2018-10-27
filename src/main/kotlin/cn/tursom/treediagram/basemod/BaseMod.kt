package cn.tursom.treediagram.basemod

import cn.tursom.treediagram.usermanage.TokenData
import java.lang.Exception


abstract class BaseMod {
	val id: Int = Companion.id
	
	open val functionName: String
		get() = this::class.java.name
	
	abstract fun handle(token: TokenData, message: String?): Any?
	
	class ModException(message: String?) : Exception(message)
	
	companion object {
		private var id: Int = 0
			get() {
				synchronized(field) {
					field++
				}
				return field
			}
	}
}

