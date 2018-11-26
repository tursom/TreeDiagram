package cn.tursom.treediagram

import cn.tursom.treediagram.usermanage.TokenData
import java.io.File
import java.io.Serializable
import java.lang.Exception
import javax.servlet.ServletRequest

/**
 * TreeDiagram的所有模组的基类
 * 系统模组与用户模组都是这个类的子类
 */
abstract class BaseMod {
	/**
	 * 模组名
	 * 是调用模组时的依据
	 * 默认为本模组的类的全名
	 */
	open val modName: String
		/**
		 * 获取模组名
		 * 重载这个函数以自定义模组名
		 */
		get() = this::class.java.name
	
	/**
	 * 模组私有目录
	 * 在调用的时候会自动创建目录，不必担心目录不存在的问题
	 * 如果有模组想储存文件请尽量使用这个目录
	 */
	val modPath by lazy {
		val path = "${BaseMod::class.java.getResource("/").path!!}${this::class.java.name}/"
		val dir = File(path)
		if (!dir.exists()) dir.mkdirs()
		path
	}
	
	/**
	 * 当模组被初始化时被调用
	 */
	open fun init() {}
	
	/**
	 * 处理模组调用请求
	 * @param token 解析过后的用户token
	 * @param request 用户通过get或者post提交的数据
	 * @return 一个用于表示json数据的对象或者null
	 */
	abstract fun handle(token: TokenData, request: ServletRequest): Serializable?
	
	/**
	 * 当模组生命周期结束时被调用
	 */
	open fun destroy() {}
	
	/**
	 * 模组运行过程中出现的异常
	 * 其异常的相关信息存放在message里
	 */
	class ModException(message: String?) : Exception(message)
	
	/**
	 * 方便获取ServletRequest里面的数据
	 * 使得子类中可以直接使用request[ 参数名 ]的形式来获取数据
	 */
	operator fun ServletRequest.get(key: String): String? = this.getParameter(key)
}

