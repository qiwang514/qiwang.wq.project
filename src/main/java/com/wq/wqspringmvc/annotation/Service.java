package com.wq.wqspringmvc.annotation;
//用与标识一个service对象 并注入到容器

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}
