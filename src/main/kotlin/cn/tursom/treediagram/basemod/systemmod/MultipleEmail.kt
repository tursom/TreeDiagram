package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.tools.base64
import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.tools.fromJson
import cn.tursom.treediagram.usermanage.TokenData
import com.google.gson.Gson
import com.sun.mail.util.MailSSLSocketFactory
import java.io.Serializable
import java.net.URL
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Address
import javax.mail.Session
import javax.mail.internet.*
import javax.servlet.ServletRequest

class MultipleEmail : BaseMod() {
	override fun handle(token: TokenData, request: ServletRequest): Serializable? {
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
	message.to?.forEach { structure ->
		//邮件内容部分
		val msg = MimeMessage(session)
		val multipart = MimeMultipart()
		// 添加文本
		if (structure.html ?: "null" != "null") {
			val htmlBodyPart = MimeBodyPart()
			htmlBodyPart.setContent(structure.html, "text/html;charset=UTF-8")
			multipart.addBodyPart(htmlBodyPart)
		} else {
			val textPart = MimeBodyPart()
			textPart.setText(structure.text)
			multipart.addBodyPart(textPart)
		}
		//添加图片
		structure.image?.forEach {
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
		structure.attachment?.forEach { fileName ->
			val adjunct = MimeBodyPart()
			val fileDataSource = FileDataSource(fileName)
			adjunct.dataHandler = DataHandler(fileDataSource)
			adjunct.fileName = fileDataSource.name.base64()
			multipart.addBodyPart(adjunct)
		}
		msg.setContent(multipart)
		//邮件主题
		msg.subject = structure.subject
		//邮件发送者
		msg.setFrom(InternetAddress(message.from))
		transport.sendMessage(msg, arrayOf<Address>(InternetAddress(structure.to)))
	}
	transport.close()
}
