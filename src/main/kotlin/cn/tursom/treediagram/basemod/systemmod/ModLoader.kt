package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.modloader.ModLoader
import cn.tursom.treediagram.usermanage.TokenData
import java.io.Serializable
import javax.servlet.http.HttpServletRequest

/**
 * 模组加载模组
 * 用于加载一个模组
 */
class ModLoader : BaseMod() {
	override fun handle(token: TokenData, request: HttpServletRequest): Serializable? {
		val modLoader = ModLoader(request["modData"], token.usr, Upload.getUploadPath(token.usr!!), false)
		if (!modLoader.load()) throw ModException("mod load error")
		return null
	}
}