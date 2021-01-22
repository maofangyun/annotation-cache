package com.mfy.cache;

import com.mfy.lock.LockProperties;

import java.lang.reflect.Method;

public class CacheProperties {

	private String[] cacheNames;

	private int expire;

	private String key;

	private Method method;

	private LockProperties lockProperties;

	public String[] getCacheNames() {
		return cacheNames;
	}

	public void setCacheNames(String[] cacheNames) {
		this.cacheNames = cacheNames;
	}

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public LockProperties getLockProperties() {
		return lockProperties;
	}

	public void setLockProperties(LockProperties lockProperties) {
		this.lockProperties = lockProperties;
	}
}
