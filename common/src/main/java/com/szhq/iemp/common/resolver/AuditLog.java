package com.szhq.iemp.common.resolver;

import java.lang.annotation.*;

/**
 * @author wanghao
 * @date 2020/1/2
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

    String value() default "";
}
