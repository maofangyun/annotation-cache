package com.mfy.advisor;
import com.mfy.annotation.EasyCache;
import com.mfy.annotation.Lock;
import com.mfy.cache.CacheProperties;
import com.mfy.lock.LockProperties;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.MethodClassKey;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachePointCut extends StaticMethodMatcherPointcut {

	public static final ConcurrentMap<MethodClassKey, CacheProperties> ATTRIBUTE_CACHE = new ConcurrentHashMap<>();

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		MethodClassKey key = getCacheKey(method,targetClass);
		CacheProperties cacheProperties = ATTRIBUTE_CACHE.get(key);
		if(cacheProperties != null){
			return true;
		} else {
			cacheProperties = new CacheProperties();
		}
		Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
		if(AnnotatedElementUtils.hasAnnotation(targetClass, EasyCache.class)
				|| AnnotatedElementUtils.hasAnnotation(specificMethod,EasyCache.class)){
			EasyCache annotation = AnnotationUtils.getAnnotation(specificMethod, EasyCache.class);
			cacheProperties.setCacheNames(annotation.cacheNames());
			cacheProperties.setMethod(specificMethod);
			cacheProperties.setExpire(annotation.expire());
			cacheProperties.setKey(annotation.key());
			LockProperties lockProperties = new LockProperties();
			Lock lock = annotation.lock();
			lockProperties.setKey(lock.key());
			lockProperties.setLockName(lock.lockName());
			lockProperties.setExpire(lock.expire());
			cacheProperties.setLockProperties(lockProperties);
			ATTRIBUTE_CACHE.putIfAbsent(key,cacheProperties);
			return true;
		}
		return false;
	}

	public static MethodClassKey getCacheKey(Method method, Class<?> targetClass) {
		return new MethodClassKey(method, targetClass);
	}

}
