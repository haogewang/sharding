package com.szhq.iemp.common.resolver;

import java.lang.annotation.*;

/**
 * 配置该注解表示从request.attribute内读取对应实体参数值
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ContentSecurityAttribute {
    /**
     * 参数值
     * 对应配置@ContentSecurityAttribute注解的参数名称即可
     *
     * @return
     */
    String value();
}
