package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.tools.base64decode
import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.usermanage.TokenData
import java.io.File
import java.io.Serializable
import javax.servlet.ServletRequest


/**
 * 文件上传模组
 * 需要提供两个参数：
 * filename要上传的文件名称
 * file或者file64
 * file与file64的去别在于file是文本文件的原文件内容，file64是base64编码后的文件内容
 * 返回的是上传到服务器的目录
 */
class Upload : BaseMod() {
	override fun handle(token: TokenData, request: ServletRequest): Serializable? {
		val fileName = request["filename"] ?: throw ModException("cant get file name")
		val uploadPath = "${getUploadPath(token.usr!!)}$fileName"
		val uploadFile = File(uploadPath)
		if (!uploadFile.parentFile.exists()) {
			uploadFile.parentFile.mkdirs()
		}
		if (uploadFile.exists()) {
			throw ModException("file exist")
		} else {
			uploadFile.createNewFile()
		}
		val file = request["file"]?.toByteArray()
				?: request["file64"]?.toByteArray()?.base64decode()
				?: throw ModException("cant get file")
		uploadFile.writeBytes(file)
		return fileName
	}
	
	companion object {
		val uploadRootPath = "${Upload::class.java.getResource("/").path!!}upload/"
		fun getUploadPath(user: String) = "$uploadRootPath$user/"
	}
}
