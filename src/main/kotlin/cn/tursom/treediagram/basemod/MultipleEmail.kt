package cn.tursom.treediagram.basemod

import cn.tursom.tools.fromJson
import cn.tursom.treediagram.modinterface.BaseMod
import cn.tursom.treediagram.datastruct.MultipleEmailData
import cn.tursom.treediagram.usermanage.TokenData
import com.google.gson.Gson
import java.io.Serializable
import javax.servlet.http.HttpServletRequest

class MultipleEmail : BaseMod() {
	override fun handle(token: TokenData, request: HttpServletRequest): Serializable? {
		try {
			val groupEmailData = gson.fromJson<MultipleEmailData>(request["message"]!!)
			groupEmailData.send()
		} catch (e: Exception) {
			return "${e::class.java}: ${e.message}"
		}
		return "true"
	}
	
	companion object {
		private val gson = Gson()
	}
}
