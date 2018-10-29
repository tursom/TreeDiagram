package cn.tursom.treediagram.basemod

import cn.tursom.treediagram.usermanage.TokenData
import java.lang.Exception

/**
 * 模组基类
 */
abstract class BaseMod {
	/**
	 * 模组名
	 * 是调用模组时的依据
	 */
	open val modName: String
		/**
		 * 获取模组名
		 * 重载这个函数以自定义模组名
		 */
		get() = this::class.java.name
	
	/**
	 * 处理模组调用请求
	 * @param token 解析过后的用户token
	 * @param request 用户通过get或者post提交的数据
	 */
	abstract fun handle(token: TokenData, request: Map<String, Array<String>>): Any?
	
	/**
	 * 模组运行过程中出现的异常
	 */
	class ModException(message: String?) : Exception(message)
}

