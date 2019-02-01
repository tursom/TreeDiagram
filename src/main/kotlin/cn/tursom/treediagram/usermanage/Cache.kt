package cn.tursom.treediagram.usermanage

class Cache<T>(val maxSize: Int) {
	private val cacheList = ArrayList<Pair<Long, T>>(maxSize)
	private var p = 0
	fun add(value: T) {
		add(System.currentTimeMillis(), value)
	}
	
	private fun add(time: Long, value: T) {
		synchronized(this) {
			if (cacheList.size < maxSize) {
				cacheList.add(Pair(time, value))
			} else {
				cacheList[p++] = Pair(time, value)
				if (p == maxSize) p = 0
			}
			Unit
		}
	}
	
	fun copy(): Cache<T> {
		val cache = Cache<T>(maxSize)
		cache.p = p
		cacheList.forEach {
			cache.cacheList.add(it)
		}
		return cache
	}
	
	fun forEach(func: (T) -> Unit) {
		cacheList.forEach {
			func(it.second)
		}
	}
	
	fun forEach(func: (Long, T) -> Unit) {
		cacheList.forEach {
			func(it.first, it.second)
		}
	}
}