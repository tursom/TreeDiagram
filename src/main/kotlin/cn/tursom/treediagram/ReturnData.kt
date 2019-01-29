package cn.tursom.treediagram

/**
 * 模组调用的返回值
 * @param state 是否成功调用模组
 * @param result 模组的返回值，结构由模组自定义，亦可以没有返回值
 */
data class ReturnData(val state: Boolean, val result: Any?)