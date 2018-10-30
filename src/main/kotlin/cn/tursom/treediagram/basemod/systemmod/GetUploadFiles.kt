package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.usermanage.TokenData
import java.io.File
import java.io.Serializable

/**
 * 获取上传的文件的列表
 */
class GetUploadFiles : BaseMod() {
	override fun handle(token: TokenData, request: Map<String, Array<String>>): Serializable? {
		val uploadPath = "${Upload.uploadRootPath}${token.usr}/"
		val uploadDir = File(uploadPath)
		val fileList = ArrayList<String>()
		uploadDir.listFiles()?.forEach {
			fileList.add(it.path)
		}
		return fileList
	}
}
