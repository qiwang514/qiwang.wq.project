package com.wq.wqspringmvc.annotation;

import java.lang.annotation.*;

//标注在目标方法的参数上  表示对应的http请求的参数
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}
