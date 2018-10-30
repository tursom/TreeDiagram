package cn.tursom.treediagram.basemod.systemmod

import java.util.*

/**
 * 用于发送一个邮件的所有信息
 *
 * @param host smtp服务器地址
 * @param port smtp服务器端口，默认465
 * @param name 邮箱用户名
 * @param password 邮箱密码
 * @param from 发送邮箱
 * @param to 目标邮箱
 * @param subject 邮件主题
 * @param html 邮件主题内容
 * @param text html为空时的邮件主题内容
 * @param image 图片
 * @param attachment 附件
 */
data class EmailData(val host: String?, val port: Int?, val name: String?, val password: String?, val from: String?,
                     val to: String?, val subject: String?, val html: String?, val text: String? = null,
                     val image: Map<String, String>? = null, val attachment: Array<String>? = null) {
	/**
	 * 自动生成的比较函数
	 */
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		
		other as EmailData
		
		if (host != other.host) return false
		if (port != other.port) return false
		if (name != other.name) return false
		if (password != other.password) return false
		if (from != other.from) return false
		if (to != other.to) return false
		if (subject != other.subject) return false
		if (html != other.html) return false
		if (text != other.text) return false
		if (image != other.image) return false
		if (!Arrays.equals(attachment, other.attachment)) return false
		
		return true
	}
	
	/**
	 * 自动生成的哈希函数
	 */
	override fun hashCode(): Int {
		var result = host?.hashCode() ?: 0
		result = 31 * result + (port ?: 0)
		result = 31 * result + (name?.hashCode() ?: 0)
		result = 31 * result + (password?.hashCode() ?: 0)
		result = 31 * result + (from?.hashCode() ?: 0)
		result = 31 * result + (to?.hashCode() ?: 0)
		result = 31 * result + (subject?.hashCode() ?: 0)
		result = 31 * result + (html?.hashCode() ?: 0)
		result = 31 * result + (text?.hashCode() ?: 0)
		result = 31 * result + (image?.hashCode() ?: 0)
		result = 31 * result + (attachment?.let { Arrays.hashCode(it) } ?: 0)
		return result
	}
}