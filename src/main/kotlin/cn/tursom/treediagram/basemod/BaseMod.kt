package cn.tursom.treediagram.basemod


open class BaseMod {
	val id: Int = Companion.id
	
	open val functionName: String = this::class.java.name
	open fun handle(id: Int, message: String): String {
		return "success"
	}
	
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

