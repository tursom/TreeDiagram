package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.usermanage.TokenData
import com.google.gson.Gson
import java.io.Serializable
import javax.servlet.http.HttpServletRequest

/**
 * 用于群发邮件的模组
 * 为每个人发送内容相同的邮件
 */
class GroupEmail : BaseMod() {
	override fun handle(token: TokenData, request: HttpServletRequest): Serializable? {
		try {
			val groupEmailData = GroupEmailData(
					request["host"],
					request["port"]?.toInt(),
					request["name"],
					request["password"],
					request["from"],
					gson.fromJson(request["to"], Array<String>::class.java),
					request["subject"],
					request["html"],
					request["text"],
					gson.fromJson(request["image"], Image::class.java),
					gson.fromJson(request["attachment"], Array<String>::class.java)
			)
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
