package cn.tursom.treediagram.basemod

import cn.tursom.treediagram.modinterface.BaseMod
import cn.tursom.treediagram.modinterface.ModException
import cn.tursom.treediagram.usermanage.TokenData
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 文件上传模组
 * 需要提供两个参数：
 * filename要上传的文件名称
 * file或者file64
 * file与file64的去别在于file是文本文件的原文件内容，file64是base64编码后的文件内容
 * 返回的是上传到服务器的目录
 */
class Upload : BaseMod() {
	override fun handle(token: TokenData, request: HttpServletRequest, response: HttpServletResponse): Serializable? {
		//确保上传用目录可用
		val uploadPath = getUploadPath(token.usr!!)
		if (!File(uploadPath).exists()) {
			File(uploadPath).mkdirs()
		}
		
		val filename = request.getHeader("filename")
				?: throw ModException("filename not found")
		val file = File("$uploadPath$filename")
		
		
		val outputStream = when (request.getHeader("type") ?: "append") {
			"create" -> {
				if (file.exists()) throw ModException("file exist")
				else FileOutputStream(file)
			}
			"append" -> {
				FileOutputStream(file, true)
			}
			"delete" -> {
				file.delete()
				return "file \"$filename\" deleted"
			}
			else -> throw ModException("unsupported upload type, " +
					"please use \"create\" or \"append\"(default) as an upload type")
		}
		
		val inputStream = request.inputStream
		var byteArray: ByteArray = inputStream.readBytes()
		while (byteArray.isNotEmpty()) {
			//写入文件
			outputStream.write(byteArray)
			byteArray = inputStream.readBytes()
		}
		outputStream.flush()
		outputStream.close()
		
		response.setHeader("filename", filename)
		//返回上传的文件名
		return filename
	}
	
	companion object {
		@JvmStatic
		val uploadRootPath = "${MultipleUpload::class.java.getResource("/").path!!}upload/"
		
		@JvmStatic
		fun getUploadPath(user: String) = "$uploadRootPath$user/"
	}
}