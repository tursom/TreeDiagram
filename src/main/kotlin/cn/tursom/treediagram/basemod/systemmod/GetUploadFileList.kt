package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.usermanage.TokenData
import java.io.File
import java.io.Serializable
import javax.servlet.ServletRequest

/**
 * 获取上传的文件的列表
 */
class GetUploadFileList : BaseMod() {
	override fun handle(token: TokenData, request: ServletRequest): Serializable? {
		val uploadPath = "${Upload.uploadRootPath}${token.usr}/"
		val uploadDir = File(uploadPath)
		val fileList = ArrayList<String>()
		uploadDir.listFiles()?.forEach {
			fileList.add(it.path.split('/').last())
		}
		return fileList
	}
}
