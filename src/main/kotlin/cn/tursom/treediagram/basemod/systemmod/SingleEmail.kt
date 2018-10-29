package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.tools.base64
import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.usermanage.TokenData
import com.google.gson.Gson
import com.sun.mail.util.MailSSLSocketFactory
import java.net.URL
import java.util.*
import java.util.logging.Logger
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Address
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

/**
 * 用于发送单个邮件的模组
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
class SingleEmail : BaseMod() {
	override fun handle(token: TokenData, request: Map<String, Array<String>>): Any? {
		return try {
			//提取邮件信息
			val mailMessage = EmailData(
					request["host"]?.get(0),
					request["port"]?.get(0)?.toInt() ?: 465,
					request["name"]?.get(0),
					request["password"]?.get(0),
					request["from"]?.get(0),
					request["to"]?.get(0),
					request["subject"]?.get(0),
					request["html"]?.get(0),
					request["text"]?.get(0),
					gson.fromJson(request["image"]?.get(0), Image::class.java),
					gson.fromJson(request["attachment"]?.get(0), Array<String>::class.java)
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
	 * 发送邮件
	 */
	fun send() = sendMail(this)
	
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

class Image : HashMap<String, String>()

fun sendMail(message: EmailData) {
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
	//邮件内容部分
	val msg = MimeMessage(session)
	val multipart = MimeMultipart()
	// 添加文本
	if (message.html ?: "null" != "null") {
		val htmlBodyPart = MimeBodyPart()
		htmlBodyPart.setContent(message.html, "text/html;charset=UTF-8")
		multipart.addBodyPart(htmlBodyPart)
	} else {
		val textPart = MimeBodyPart()
		textPart.setText(message.text)
		multipart.addBodyPart(textPart)
	}
	//添加图片
	message.image?.forEach {
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
	message.attachment?.forEach { fileName ->
		val adjunct = MimeBodyPart()
		val fileDataSource = FileDataSource(fileName)
		adjunct.dataHandler = DataHandler(fileDataSource)
//		adjunct.fileName = changeEncode(fileDataSource.name)
		adjunct.fileName = fileDataSource.name.base64()
		multipart.addBodyPart(adjunct)
	}
	msg.setContent(multipart)
	//邮件主题
	msg.subject = message.subject
	//邮件发送者
	msg.setFrom(InternetAddress(message.from))
	//发送邮件
	val transport = session.transport
	transport.connect(message.host, message.name, message.password)
	
	transport.sendMessage(msg, arrayOf<Address>(InternetAddress(message.to)))
	transport.close()
}
