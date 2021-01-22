package com.mfy.advisor;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

public class CacheAdvisor extends AbstractBeanFactoryPointcutAdvisor {

	private CachePointCut pointCut = new CachePointCut();

	@Override
	public Pointcut getPointcut() {
		return pointCut;
	}
}
