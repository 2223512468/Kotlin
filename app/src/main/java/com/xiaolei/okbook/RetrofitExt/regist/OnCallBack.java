package com.xiaolei.okbook.RetrofitExt.regist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 写这个注解是为了方便callback的调用
 * <br/>-切记切记，必须，这里说的是必须！！是两个参数，第一个是上下文第二个是callback--
 * <br/>-我特么也不知道有什么卵用，先加上，预防万一，反正以后万一有用呢。------------
 * <br/>Created by xiaolei on 2017/5/24.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnCallBack
{
    String value();

    /**
     * 是否拦截下一步？如果为true，则除了onFinally，后续步骤不进行。比如onSuccess
     * @return
     */
    boolean stopNextStep() default true;
}
