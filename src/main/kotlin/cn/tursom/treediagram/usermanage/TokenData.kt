package cn.tursom.treediagram.usermanage

import cn.tursom.tools.*
import com.google.gson.Gson

data class TokenData(
		val usr: String,  //用户名
		val tim: Long? = System.currentTimeMillis(),  //签发时间
		val exp: Long? = 1000 * 60 * 60 * 24 * 3  //过期时间
) {
	companion object {
		private val gson = Gson()
		private val md5Base64 = "MD5".base64()
		private val secretKey = randomInt(1000000000, 999999999).toString().md5()!!
		fun getToken(username: String): String {
			val body = "$md5Base64.${gson.toJson(TokenData(username)).base64()}"
			return "$body.${"$body.$secretKey".md5()}"
		}
		
		fun paraseToken(token: String): TokenData? {
			val data = token.split('.')
			if (data.size != 3) return null
			return if ("${data[0]}.${data[1]}.$secretKey".digest(data[0].base64decode()) == data[2]) {
				gson.fromJson(data[1], TokenData::class.java)
			} else {
				null
			}
		}
	}
}