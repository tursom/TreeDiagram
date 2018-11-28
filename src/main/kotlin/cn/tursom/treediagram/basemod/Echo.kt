package cn.tursom.treediagram.basemod

import cn.tursom.treediagram.modinterface.BaseMod
import cn.tursom.treediagram.usermanage.TokenData
import java.io.Serializable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 用于实现echo功能的模组
 * 会直接将message参数原样返回
 */
class Echo : BaseMod() {
	override fun handle(token: TokenData, request: HttpServletRequest, response: HttpServletResponse): Serializable? {
		return request["message"]
	}
}