package cn.tursom.treediagram.basemod.systemmod

import cn.tursom.treediagram.basemod.BaseMod
import cn.tursom.treediagram.usermanage.TokenData
import java.io.Serializable

/**
 * 用于实现echo功能的模组
 * 会直接将message参数原样返回
 */
class Echo : BaseMod() {
	override fun handle(token: TokenData, request: Map<String, Array<String>>): Serializable? {
		return request["message"]
	}
}