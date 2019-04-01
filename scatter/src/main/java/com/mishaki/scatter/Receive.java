package com.mishaki.scatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Receive {
    /**
     * 默认在当前线程执行
     */
    ThreadMode threadMode() default ThreadMode.CURRENT;

    /**
     * 执行优先级
     */
    int priority() default 0;

    /**
     * 执行的时候,可以根据这个tag发射到对应的方法
     */
    String tag() default "";
}