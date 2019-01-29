package cn.tursom.treediagram.datastruct

/**
 * 为短信推送设计的数据类
 *
 * @param phoneNumbers 手机号，带不带国家码具体由服务端模组规定
 * @param message 信息内容，具体由服务端模组规定
 * @param nationCode 国家码，如 86 为中国。如何带国家码具体由服务端模组规定
 */
data class SMSData(val phoneNumbers: String?, val message: String?, val nationCode: String? = "86")