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

fun String.toHexString() = toByteArray().toHexString()

fun ByteArray.toHexString(): String? {
	val sb = StringBuilder()
	forEach {
		//获取低八位有效值+
		val i: Int = it.toInt() and 0xff
		//将整数转化为16进制
		var hexString = Integer.toHexString(i)
		if (hexString.length < 2) {
			//如果是一位的话，补0
			hexString = "0$hexString"
		}
		sb.append(hexString)
	}
	return sb.toString()
}