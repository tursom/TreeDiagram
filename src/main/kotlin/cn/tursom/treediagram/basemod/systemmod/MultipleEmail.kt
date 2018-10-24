package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.tools.fromJson
import com.google.gson.Gson
import com.sun.mail.util.MailSSLSocketFactory
import java.io.UnsupportedEncodingException
import java.net.Socket
import java.net.URL
import java.nio.charset.Charset
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Address
import javax.mail.Session
import javax.mail.internet.*

class MultipleEmail : BaseMod() {
	override fun handle(id: Int, message: String): String {
		try {
			val groupEmailData = Gson().fromJson<MultipleEmailData>(message)
			sendMail(groupEmailData)
		} catch (e: Exception) {
			return "false"
		}
		return "true"
	}

	fun sendMail(message: MultipleEmailData) {
		val props = Properties()
//		props["mail.debug"] = "true"  // 开启debug调试
		props["mail.smtp.auth"] = "true"  // 发送服务器需要身份验证
		props["mail.smtp.host"] = message.host  // 设置邮件服务器主机名
		props["mail.transport.protocol"] = "smtps"  // 发送邮件协议名称
		props["mail.smtp.port"] = message.port
		val sf = MailSSLSocketFactory()
		sf.isTrustAllHosts = true
		props["mail.smtp.ssl.enable"] = "true"
		props["mail.smtp.ssl.socketFactory"] = sf


		val session = Session.getInstance(props)
		//发送邮件
		val transport = session.transport
		transport.connect(message.host, message.name, message.password)
		message.to?.forEach {
			//邮件内容部分
			val msg = MimeMessage(session)
			val multipart = MimeMultipart()
			// 添加文本
			val textPart = MimeBodyPart()
			textPart.setText(it.text)
			multipart.addBodyPart(textPart)
			//添加图片
			it.image?.forEach {
				//创建用于保存图片的MimeBodyPart对象，并将它保存到MimeMultipart中
				val gifBodyPart = MimeBodyPart()
				if (it.value.startsWith("http://") or it.value.startsWith("https://")) {
					gifBodyPart.dataHandler = DataHandler(URL(it.value))
				} else {
					val fds = FileDataSource(it.value)//图片所在的目录的绝对路径
					gifBodyPart.dataHandler = DataHandler(fds)
				}
				gifBodyPart.contentID = it.key   //cid的值
				multipart.addBodyPart(gifBodyPart)
			}
			//添加附件
			it.attachment?.forEach { fileName ->
				val adjunct = MimeBodyPart()
				val fileDataSource = FileDataSource(fileName)
				adjunct.dataHandler = DataHandler(fileDataSource)
				adjunct.fileName = changeEncode(fileDataSource.name)
				multipart.addBodyPart(adjunct)
			}
			msg.setContent(multipart)
			//邮件主题
			msg.subject = it.subject
			//邮件发送者
			msg.setFrom(InternetAddress(message.from))
			transport.sendMessage(msg, arrayOf<Address>(InternetAddress(it.to)))
		}
		transport.close()
	}

	/**
	 * 进行base64加密，防止中文乱码
	 */
	private fun changeEncode(str: String): String {
		var string = str
		try {
			string = MimeUtility.encodeText(String(string.toByteArray(), Charset.forName("UTF-8")),
					"UTF-8", "B") // "B"代表Base64
		} catch (e: UnsupportedEncodingException) {
			e.printStackTrace()
		}
		return string
	}
}

data class MultipleEmailData(val host: String?, val port: Int?, val name: String?, val password: String?, val from: String?,
                             val to: Array<MailStructure>?) {

	fun toJson() = Gson().toJson(this)

	fun fromJson(json: String) = Gson().fromJson<MultipleEmailData>(json)

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

fun sendMail(message: MultipleEmailData) {
	val props = Properties()
//		props["mail.debug"] = "true"  // 开启debug调试
	props["mail.smtp.auth"] = "true"  // 发送服务器需要身份验证
	props["mail.smtp.host"] = message.host  // 设置邮件服务器主机名
	props["mail.transport.protocol"] = "smtps"  // 发送邮件协议名称
	props["mail.smtp.port"] = message.port
	val sf = MailSSLSocketFactory()
	sf.isTrustAllHosts = true
	props["mail.smtp.ssl.enable"] = "true"
	props["mail.smtp.ssl.socketFactory"] = sf


	val session = Session.getInstance(props)
	//发送邮件
	val transport = session.transport
	transport.connect(message.host, message.name, message.password)
	message.to?.forEach {
		//邮件内容部分
		val msg = MimeMessage(session)
		val multipart = MimeMultipart()
		// 添加文本
		if (it.html ?: "null" != "null") {
			val htmlBodyPart = MimeBodyPart()
			htmlBodyPart.setContent(it.html, "text/html;charset=UTF-8")
			multipart.addBodyPart(htmlBodyPart)
		} else {
			val textPart = MimeBodyPart()
			textPart.setText(it.text)
			multipart.addBodyPart(textPart)
		}
		//添加图片
		it.image?.forEach {
			//创建用于保存图片的MimeBodyPart对象，并将它保存到MimeMultipart中
			val gifBodyPart = MimeBodyPart()
			if (it.value.startsWith("http://") or it.value.startsWith("https://")) {
				gifBodyPart.dataHandler = DataHandler(URL(it.value))
			} else {
				val fds = FileDataSource(it.value)//图片所在的目录的绝对路径
				gifBodyPart.dataHandler = DataHandler(fds)
			}
			gifBodyPart.contentID = it.key   //cid的值
			multipart.addBodyPart(gifBodyPart)
		}
		//添加附件
		it.attachment?.forEach { fileName ->
			val adjunct = MimeBodyPart()
			val fileDataSource = FileDataSource(fileName)
			adjunct.dataHandler = DataHandler(fileDataSource)
			adjunct.fileName = changeEncode(fileDataSource.name)
			multipart.addBodyPart(adjunct)
		}
		msg.setContent(multipart)
		//邮件主题
		msg.subject = it.subject
		//邮件发送者
		msg.setFrom(InternetAddress(message.from))
		transport.sendMessage(msg, arrayOf<Address>(InternetAddress(it.to)))
	}
	transport.close()
}

/**
 * 进行base64加密，防止中文乱码
 */
private fun changeEncode(str: String): String {
	var string = str
	try {
		string = MimeUtility.encodeText(String(string.toByteArray(), Charset.forName("UTF-8")),
				"UTF-8", "B") // "B"代表Base64
	} catch (e: UnsupportedEncodingException) {
		e.printStackTrace()
	}
	return string
}