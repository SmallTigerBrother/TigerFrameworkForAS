package com.mn.tiger.datastorage.db;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 查询数据库时使用的缓存类
 * 
 * @since 2014年2月18日
 */
public class FindTempCache {
	public FindTempCache() {
	}

	/**
	 * key: sql; value: find result
	 */
	private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<String, Object>();

	private long seq = 0;

	public void put(String sql, Object result) {
		if (sql != null && result != null) {
			cache.put(sql, result);
		}
	}

	public Object get(String sql) {
		return cache.get(sql);
	}

	public void setSeq(long seq) {
		if (this.seq != seq) {
			cache.clear();
			this.seq = seq;
		}
	}
}
