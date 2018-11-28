package cn.tursom.treediagram.basemod

import cn.tursom.treediagram.modinterface.BaseMod
import cn.tursom.treediagram.modinterface.ModException
import cn.tursom.treediagram.basemod.Upload.Companion.getUploadPath
import cn.tursom.treediagram.usermanage.TokenData
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import javax.servlet.http.HttpServletRequest


/**
 * 复数文件上传模组
 * 需要提供两个参数：
 * filename要上传的文件名称
 * file或者file64
 * file与file64的去别在于file是文本文件的原文件内容，file64是base64编码后的文件内容
 * 返回的是上传到服务器的目录
 */
class MultipleUpload : BaseMod() {
	override fun handle(token: TokenData, request: HttpServletRequest): Serializable? {
		//确保上传用目录可用
		val uploadPath = getUploadPath(token.usr!!)
		if (!File(uploadPath).exists()) {
			File(uploadPath).mkdirs()
		}
		
		//遍历上传的每一个文件
		val filesData =
				ServletFileUpload(DiskFileItemFactory()).parseParameterMap(request)
		filesData.forEach { name, desc ->
			//建立上传文件，打开文件输出流
			val uploadFile = File("$uploadPath$name")
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
			
			val inputStream = desc[0].inputStream!!
			var byteArray: ByteArray = inputStream.readBytes()
			while (byteArray.isNotEmpty()) {
				//写入文件
				outputStream.write(byteArray)
				byteArray = inputStream.readBytes()
			}
			outputStream.flush()
			outputStream.close()
		}
		//返回上传的文件列表
		return filesData.keys.toTypedArray()
	}
}
