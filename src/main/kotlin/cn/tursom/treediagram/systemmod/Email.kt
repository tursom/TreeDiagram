package cn.tursom.treediagram.systemmod

import cn.tursom.treediagram.BaseMod
import cn.tursom.treediagram.datastruct.EmailData
import cn.tursom.treediagram.usermanage.TokenData
import com.google.gson.Gson
import java.io.Serializable
import java.util.*
import javax.servlet.ServletRequest

/**
 * 用于发送单个邮件的模组
 * 群发邮件请用MultipleEmail和GroupEmail
 *
 * 需要提供的参数为
 *
 * host smtp服务器地址
 * port smtp服务器端口，默认465
 * name 邮箱用户名
 * password 邮箱密码
 * from 发送邮箱
 * to 目标邮箱
 * subject 邮件主题
 * html 邮件主题内容（可为空，为空会使用text）
 * text html为空时的邮件主题内容（html不为空时，此值会被忽略）
 * image 图片（可选）
 * attachment 附件（可选）
 */
class Email : BaseMod() {
	override fun handle(token: TokenData, request: ServletRequest): Serializable? {
		return try {
			//提取邮件信息
			val mailMessage = EmailData(
					request["host"],
					request["port"]?.toInt() ?: 465,
					request["name"],
					request["password"],
					request["from"],
					request["to"],
					request["subject"],
					request["html"],
					request["text"],
					gson.fromJson(request["image"], Image::class.java),
					gson.fromJson(request["attachment"], Array<String>::class.java)
			)
			//发送邮件
			mailMessage.send()
			//要保持返回值的一致性，所以返回true的字符串值
			"true"
		} catch (e: Exception) {
			e.printStackTrace()
			"${e::class.java}: ${e.message}"
		}
	}
	
	companion object {
		private val gson = Gson()
	}
}

class Image : HashMap<String, String>()
