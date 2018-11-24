package cn.tursom.treediagram.usermanage

import cn.tursom.tools.*
import com.google.gson.Gson

/**
 * token的结构为
 * token = head.body.签名
 * 其中
 * head = base64(加密方式)
 * body = base64(TokenData json序列化)
 * 随机数 = 一至十位随机整数，程序启动时确定
 * 秘钥 = md5(随机数)
 * 签名 = 加密方式(head.body.秘钥)
 *
 * 由于秘钥是在启动时随机得出的，故所有token都会在服务器重启后失效
 */

data class TokenData(
		val usr: String?,  //用户名
		val lev: String? = run {
			val user = findUser(usr!!)
			user?.level ?: "user"
		}, //用户权限
		val tim: Long? = System.currentTimeMillis(),  //签发时间
		val exp: Long? = 1000 * 60 * 60 * 24 * 3  //过期时间
) {
	companion object {
		private val gson = Gson() //序列化和反序列化用的Gson库
		private val digestFunctionBase64 = "MD5".base64()  //默认md5加密
		private val secretKey = randomInt(0, 999999999).toString().md5()!!  //加密秘钥
		
		/**
		 * 签发一个token
		 * @param username 用户名
		 * @return 一整个token
		 */
		internal fun getToken(username: String, exp: Long? = 1000 * 60 * 60 * 24 * 3): String {
			val body = "$digestFunctionBase64.${gson.toJson(TokenData(username, exp = exp)).base64()}"
			return "$body.${"$body.$secretKey".md5()}"
		}
		
		/**
		 * 验证一个token
		 *
		 * @param token 需要验证的整个token
		 * @return 验证结果，如果失败返回null，成功则返回一个TokenData对象
		 */
		internal fun parseToken(token: String): TokenData? {
			val data = token.split('.')
			return when {
				data.size != 3 -> null
				"${data[0]}.${data[1]}.$secretKey".digest(data[0].base64decode()) == data[2] -> try {
					gson.fromJson(data[1].base64decode(), TokenData::class.java)
				} catch (e: Exception) {
					null
				}
				else -> null
			}
		}
	}
}