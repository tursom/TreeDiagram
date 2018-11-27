package cn.tursom.treediagram.systemmod

import cn.tursom.treediagram.BaseMod
import cn.tursom.treediagram.usermanage.TokenData
import java.io.Serializable
import javax.servlet.http.HttpServletRequest

/**
 * 用于实现echo功能的模组
 * 会直接将message参数原样返回
 */
class Echo : BaseMod() {
	override fun handle(token: TokenData, request: HttpServletRequest): Serializable? {
		return request["message"]
	}
}