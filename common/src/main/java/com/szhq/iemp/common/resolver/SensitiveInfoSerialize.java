package com.szhq.iemp.common.resolver;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.szhq.iemp.common.constant.enums.TypeEnum;
import com.szhq.iemp.common.util.MaskUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

/**
 * 脱敏
 * @author wanghao
 * @date 2019/11/11
 */
@Slf4j
public class SensitiveInfoSerialize extends JsonSerializer<String> implements ContextualSerializer {

    private TypeEnum type;

    public SensitiveInfoSerialize() {
    }

    public SensitiveInfoSerialize(final TypeEnum type) {
        this.type = type;
    }

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        log.info("value:" + value);
        switch (this.type) {
            case ID_NUMBER: {
                jsonGenerator.writeString(MaskUtils.maskIDCardNo(value));
                break;
            }
            case PHONE: {
                jsonGenerator.writeString(MaskUtils.maskPhone(value));
                break;
            }
            case HOME: {
                jsonGenerator.writeString(MaskUtils.maskHome(value));
                break;
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        // 为空直接跳过
        if (beanProperty != null) {
            /// 非 String 类直接跳过
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                DesensitizedAnnotation sensitiveInfo = beanProperty.getAnnotation(DesensitizedAnnotation.class);
                if (sensitiveInfo == null) {
                    sensitiveInfo = beanProperty.getContextAnnotation(DesensitizedAnnotation.class);
                }
                // 如果能得到注解,就将注解的value传入SensitiveInfoSerialize
                if (sensitiveInfo != null) {
                    return new SensitiveInfoSerialize(sensitiveInfo.type());
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }

}
