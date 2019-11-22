package com.szhq.iemp.common.resolver;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.szhq.iemp.common.constant.enums.TypeEnum;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * 脱敏注解
 * @author wanghao
 * @date 2019/11/8
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveInfoSerialize.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface DesensitizedAnnotation {

    /*脱敏数据类型*/
    TypeEnum type();
    /*判断注解是否生效，暂时没有用到*/
    String isEffectiveMethod() default "";
}
