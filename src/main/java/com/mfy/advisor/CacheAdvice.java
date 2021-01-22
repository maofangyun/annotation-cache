package com.mfy.advisor;

import com.mfy.cache.Cache;
import com.mfy.cache.CacheManager;
import com.mfy.cache.CacheProperties;
import com.mfy.handler.TypeHandler;
import com.mfy.lock.CacheLock;
import com.mfy.lock.LockManager;
import com.mfy.lock.LockProperties;
import com.mfy.parser.ElParser;
import jodd.util.StringUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodClassKey;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CacheAdvice implements MethodInterceptor {

	private final Logger logger = LoggerFactory.getLogger(CacheAdvice.class);

	private ApplicationContext applicationContext;

	private CacheManager cacheManager;

	private LockManager lockManager;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Class<?> targetClass = invocation.getMethod().getDeclaringClass();
		// 获取原型的方法(非桥接方法)
		Method specificMethod = AopUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);
		MethodClassKey cacheKey = CachePointCut.getCacheKey(specificMethod, targetClass);
		CacheProperties cacheProperties = CachePointCut.ATTRIBUTE_CACHE.get(cacheKey);

		// 获取缓存数据库的key
		String key = getCacheKey(invocation,cacheProperties.getKey());
		// 从缓存数据库中获取值
		String cacheValue = getCacheValue(cacheProperties,key);
		if(cacheValue != null){
			// 序列化成方法的返回值类型
			return handlerType(invocation,cacheValue);
		}

		// 缓存数据库中找不到结果,进入加锁流程
		CacheLock cacheLock = null;
		try{
			LockProperties lockProperties = cacheProperties.getLockProperties();
			// 通过注解的lockName属性获取对应缓存锁
			cacheLock = lockManager.getCacheLock(lockProperties.getLockName());
			// 加锁
			cacheLock.lock(lockProperties.getExpire(), TimeUnit.SECONDS,lockProperties.getKey());
			logger.info("得到锁");
			// 再查一遍缓冲,若其他线程已经将值写入缓存中,则不需要进行后续的操作
			cacheValue = getCacheValue(cacheProperties,key);
			if(cacheValue != null){
				// 序列化成方法的返回值类型
				return handlerType(invocation,cacheValue);
			}
			// 调用被代理的方法
			Object result = invocation.proceed();
			// 将返回值写入缓存中
			String[] cacheNames = cacheProperties.getCacheNames();
			for(String name : cacheNames){
				Cache cache = cacheManager.getCache(name);
				cache.put(key,result,cacheProperties.getExpire());
			}
			return result;
		} finally {
			if(cacheLock != null){
				logger.info("释放锁");
				cacheLock.unlock();
			}
		}
	}

	/**
	 * 处理返回的结果,序列化成返回值的类型
	 * */
	private Object handlerType(MethodInvocation invocation, String cacheValue) {
		Map<String, TypeHandler> types = applicationContext.getBeansOfType(TypeHandler.class);
		for (Map.Entry<String, TypeHandler> entry : types.entrySet()) {
			//根据调用的方法的返回值类型，找到该类型的处理类
			if(entry.getValue().support(invocation.getMethod().getGenericReturnType())) {
				return entry.getValue().handler(cacheValue,invocation.getMethod().getGenericReturnType());
			}
		}
		// 找不到解析器,直接返回原结果,并去掉由于fastjson序列化,导致的字符串前后存在的双引号
		return cacheValue.replace("\"","");
	}

	/**
	 * 获取缓存数据库中的值
	 * */
	private String getCacheValue(CacheProperties cacheProperties, String key) {
		String[] cacheNames = cacheProperties.getCacheNames();
		for (String cacheName : cacheNames) {
			Cache cache = cacheManager.getCache(cacheName);
			Object value = cache.get(key);
			if (!StringUtil.isBlank((String)value)) {
				return (String) value;
			}
		}
		return null;
	}

	/**
	 * 生成缓存数据库的key,以传入的key+方法入参名称+方法入参值混合
	 * */
	private String getCacheKey(MethodInvocation invocation,String key) {
		Object[] arguments = invocation.getArguments();
		String[] parameterNames = new DefaultParameterNameDiscoverer().getParameterNames(invocation.getMethod());
		if(StringUtil.isBlank(key)){
			key = invocation.getMethod().getName();
		}
		String s = ElParser.getKey(key, parameterNames, arguments);
		return s ==null?key:s;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void setLockManager(LockManager lockManager) {
		this.lockManager = lockManager;
	}

}
