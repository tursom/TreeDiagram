package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.modloader.ModLoader
import cn.tursom.treediagram.usermanage.TokenData
import cn.tursom.treediagram.usermanage.findUser
import java.io.Serializable
import javax.servlet.http.HttpServletRequest

/**
 * 模组加载模组
 * 用于加载一个模组
 *
 * 需要提供的参数有：
 * mod 要加载的模组的信息，结构为json序列化后的ClassData数据
 * system 可选，是否加入系统模组，需要admin权限
 *
 * 本模组会根据提供的信息自动寻找模组并加载
 * 模组加载的根目录为使用Upload上传的根目录
 */
class ModLoader : BaseMod() {
	override fun handle(token: TokenData, request: HttpServletRequest): Serializable? {
		println(request["modData"])
		val modLoader = if (request["system"] != "true") {
			ModLoader(request["modData"]
					?: throw ModException("no mod get"), token.usr, Upload.getUploadPath(token.usr!!), false)
		} else {
			if (findUser(token.usr!!)?.level != "admin") throw ModException("user not admin")
			ModLoader(request["modData"] ?: throw ModException("no mod get"), null, Upload.getUploadPath(token.usr), false)
		}
		if (!modLoader.load()) throw ModException("mod load error")
		return null
	}
}