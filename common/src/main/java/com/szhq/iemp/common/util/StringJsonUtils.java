package com.szhq.iemp.common.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Slf4j
public class StringJsonUtils {
    /**
     * jsonstring 转换成map
     */
    public static Map<String, Object> jsonStringToMap(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map = mapper.readValue(jsonString, new TypeReference<HashMap<String, Object>>() {
            });
            Set<String> set = map.keySet();
            Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                Object values = "";
                if ("operatorIdList".equals(key)) {
                    values = map.get(key);
                } else {
                    values = map.get(key).toString();
                    values = ((String) values).trim();
                }
                map.put(key, values);
            }
        } catch (JsonParseException e) {
            log.error("e", e);
        } catch (JsonMappingException e) {
            log.error("e", e);
        } catch (IOException e) {
            log.error("e", e);
        }
        return map;
    }
}
