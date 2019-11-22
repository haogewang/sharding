package com.szhq.iemp.common.resolver;

import com.szhq.iemp.common.constant.enums.ContentSecurityAwayEnum;

import java.lang.annotation.*;

/**
 * 配置开启安全
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ContentSecurity {
    /**
     * 内容加密方式
     * 默认DES
     *
     * @return
     */
    ContentSecurityAwayEnum away() default ContentSecurityAwayEnum.DES;
}
