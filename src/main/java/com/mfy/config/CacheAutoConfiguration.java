package com.mfy.config;

import com.mfy.advisor.CacheAdvice;
import com.mfy.advisor.CacheAdvisor;
import com.mfy.cache.CacheManager;
import com.mfy.lock.LockManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.mfy.*")
@EnableAspectJAutoProxy
@ConditionalOnClass(value = {CacheManager.class,LockManager.class})
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager cacheManager() {
        return new CacheManager();
    }

    @Bean
    @ConditionalOnMissingBean(LockManager.class)
    public LockManager lockManager() {
        return new LockManager();
    }

	@Bean
	public CacheAdvice cacheAdvice(CacheManager cacheManager, LockManager lockManager, ApplicationContext applicationContext) {
		CacheAdvice cacheAdvice = new CacheAdvice();
		cacheAdvice.setCacheManager(cacheManager);
		cacheAdvice.setLockManager(lockManager);
		cacheAdvice.setApplicationContext(applicationContext);
		return cacheAdvice;
	}

    @Bean
    public CacheAdvisor cacheAdvisor(CacheAdvice cacheAdvice) {
        CacheAdvisor cacheAdvisor = new CacheAdvisor();
        cacheAdvisor.setAdvice(cacheAdvice);
        return cacheAdvisor;
    }

}
