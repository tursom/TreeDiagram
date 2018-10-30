package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.modloader.ModLoader
import cn.tursom.treediagram.usermanage.TokenData
import java.io.Serializable

/**
 * 模组加载模组
 * 用于加载一个模组
 */
class ModLoader : BaseMod() {
	override fun handle(token: TokenData, request: Map<String, Array<String>>): Serializable? {
		val modLoader = ModLoader(request["modData"]!![0], token.usr, false)
		if (!modLoader.load()) throw ModException("mod load error")
		return null
	}
}