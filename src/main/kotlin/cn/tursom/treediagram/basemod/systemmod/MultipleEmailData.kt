package cn.tursom.treediagram.basemod.systemmod

import java.util.*

data class MultipleEmailData(val host: String?, val port: Int?, val name: String?, val password: String?, val from: String?,
                             val to: Array<MailStructure>?) {
	fun send() = sendMail(this)
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		
		other as MultipleEmailData
		
		if (host != other.host) return false
		if (port != other.port) return false
		if (name != other.name) return false
		if (password != other.password) return false
		if (from != other.from) return false
		if (!Arrays.equals(to, other.to)) return false
		
		return true
	}
	
	override fun hashCode(): Int {
		var result = host?.hashCode() ?: 0
		result = 31 * result + (port ?: 0)
		result = 31 * result + (name?.hashCode() ?: 0)
		result = 31 * result + (password?.hashCode() ?: 0)
		result = 31 * result + (from?.hashCode() ?: 0)
		result = 31 * result + (to?.let { Arrays.hashCode(it) } ?: 0)
		return result
	}
}

data class MailStructure(val to: String?, val subject: String?, val html: String?, val text: String? = null,
                         val image: Map<String, String>? = null, val attachment: Array<String>? = null) {
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		
		other as MailStructure
		
		if (to != other.to) return false
		if (subject != other.subject) return false
		if (html != other.html) return false
		if (text != other.text) return false
		if (image != other.image) return false
		if (!Arrays.equals(attachment, other.attachment)) return false
		
		return true
	}
	
	override fun hashCode(): Int {
		var result = to?.hashCode() ?: 0
		result = 31 * result + (subject?.hashCode() ?: 0)
		result = 31 * result + (html?.hashCode() ?: 0)
		result = 31 * result + (text?.hashCode() ?: 0)
		result = 31 * result + (image?.hashCode() ?: 0)
		result = 31 * result + (attachment?.let { Arrays.hashCode(it) } ?: 0)
		return result
	}
}