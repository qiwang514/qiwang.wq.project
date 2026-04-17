package com.wq.wqspringmvc.annotation;
//用于指定控制器方法映射路径

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
}
