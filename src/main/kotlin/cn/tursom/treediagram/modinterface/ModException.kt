package cn.tursom.treediagram.modinterface

/**
 * 模组运行过程中出现的异常
 * 其异常的相关信息存放在message里
 */
class ModException(message: String?) : Exception(message)