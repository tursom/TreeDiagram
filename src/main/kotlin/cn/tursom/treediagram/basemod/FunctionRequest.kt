package cn.tursom.treediagram.basemod

import com.google.gson.Gson

data class FunctionRequest(val type: String?, val function: String?, val message: String?) {
	fun toJson(): String? {
		return Gson().toJson(this)
	}

	companion object {
		fun fromJson(message: String): FunctionRequest? {
			return Gson().fromJson(message, FunctionRequest::class.java)
		}
	}
}