@file:Suppress("unused")

package cn.tursom.tools

import com.google.gson.Gson
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Created by Tursom Ulefits on 2017/8/8.
 * Author: Tursom K. Ulefits
 * email: tursom@foxmail.com c
 */

/**
 * CPU数量
 * 如果有超线程技术，则是超线程后的CPU数量
 */
val cpuNumber = Runtime.getRuntime().availableProcessors()

fun ByteArray.md5(): ByteArray? {
	return try {
		//获取md5加密对象
		val instance = MessageDigest.getInstance("MD5")
		//加密，返回字节数组
		instance.digest(this)
	} catch (e: NoSuchAlgorithmException) {
		e.printStackTrace()
		null
	}
}

fun String.md5(): String? {
	return toByteArray().md5()?.toHexString()
}

fun ByteArray.sha256(): ByteArray? {
	return try {
		//获取md5加密对象
		val instance = MessageDigest.getInstance("SHA-256")
		//加密，返回字节数组
		instance.digest(this)
	} catch (e: NoSuchAlgorithmException) {
		e.printStackTrace()
		null
	}
}

fun String.sha256(): String? {
	return toByteArray().sha256()?.toHexString()
}

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

fun ByteArray.toUTF8String() = String(this, Charsets.UTF_8)

fun String.base64() = this.toByteArray().base64().toUTF8String()

fun ByteArray.base64(): ByteArray {
	return Base64.getEncoder().encode(this)
}

fun String.base64decode() = Base64.getDecoder().decode(this).toUTF8String()

fun ByteArray.base64decode(): ByteArray = Base64.getDecoder().decode(this)

fun String.digest(type: String) = toByteArray().digest(type)?.toHexString()

fun ByteArray.digest(type: String) = try {
	//获取加密对象
	val instance = MessageDigest.getInstance(type)
	//加密，返回字节数组
	instance.digest(this)
} catch (e: NoSuchAlgorithmException) {
	e.printStackTrace()
	null
}

fun randomInt(min: Int, max: Int) = Random().nextInt(max - min + 1) + min

fun getTAG(cls: Class<*>): String {
	return cls.name.split(".").last().dropLast(10)
}

inline fun <reified T : Any> Gson.fromJson(json: String): T {
	return fromJson(json, T::class.java)
}
