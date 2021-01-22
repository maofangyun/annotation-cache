package com.mfy.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {

	String lockName() default "redisLock";

	int expire() default 60;

	String key() default "lock";

}
