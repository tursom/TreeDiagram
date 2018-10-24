package cn.tursom.treediagram.modloader

import java.util.*

data class ClassData(val url: String?, val path: String?, val classname: Array<String>?) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as ClassData

		if (url != other.url) return false
		if (path != other.path) return false
		if (!Arrays.equals(classname, other.classname)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = url?.hashCode() ?: 0
		result = 31 * result + (path?.hashCode() ?: 0)
		result = 31 * result + (classname?.let { Arrays.hashCode(it) } ?: 0)
		return result
	}
}