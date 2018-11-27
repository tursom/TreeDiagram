package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.usermanage.TokenData
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import javax.servlet.http.HttpServletRequest


/**
 * 文件上传模组
 * 需要提供两个参数：
 * filename要上传的文件名称
 * file或者file64
 * file与file64的去别在于file是文本文件的原文件内容，file64是base64编码后的文件内容
 * 返回的是上传到服务器的目录
 */
class Upload : BaseMod() {
	override fun handle(token: TokenData, request: HttpServletRequest): Serializable? {
		val uploadPath = getUploadPath(token.usr!!)
		if (!File(uploadPath).exists()) {
			File(uploadPath).mkdirs()
		}
		
		val body =
				ServletFileUpload(DiskFileItemFactory()).parseParameterMap(request)
		body.forEach { name, desc ->
			val uploadFile = File("$uploadPath$name")
			if (!uploadFile.parentFile.exists()) {
				uploadFile.parentFile.mkdirs()
			}
			val outputStream = when (request.getHeader("type") ?: "append") {
				"create" -> {
					if (uploadFile.exists()) throw ModException("file exist")
					FileOutputStream(uploadFile)
				}
				"append" -> {
					FileOutputStream(uploadFile, true)
				}
				else -> throw ModException("unsupported upload type, " +
						"please use \"create\" or \"append\"(default) as an upload type")
			}
			
			outputStream.write(desc[0].inputStream.readAllBytes())
			outputStream.close()
		}
		
		return body.keys.toTypedArray()
	}
	
	companion object {
		val uploadRootPath = "${Upload::class.java.getResource("/").path!!}upload/"
		fun getUploadPath(user: String) = "$uploadRootPath$user/"
	}
}
