
package com.guoquan.store.operation.log.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Description Json工具类
 * @Date 2021/5/31 16:11
 * @Author wangLuLu
 * @Version 1.0
 */

public class OpeJsonHelper {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public OpeJsonHelper() {
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static <T> T parseToObject(InputStream is, Class<T> toClass) {
        try {
            return objectMapper.readValue(is, toClass);
        } catch (Exception var3) {
            var3.printStackTrace();
            throw new RuntimeException(var3);
        }
    }

    public static <T> T parseToObject(byte[] b, int offset, int len, Class<T> valueType) {
        try {
            return objectMapper.readValue(b, offset, len, valueType);
        } catch (Exception var5) {
            var5.printStackTrace();
            throw new RuntimeException(var5);
        }
    }

    public static <T> T parseToObject(String json, Class<T> toClass) {
        try {
            return objectMapper.readValue(json, toClass);
        } catch (Exception var3) {
            var3.printStackTrace();
            throw new RuntimeException(var3);
        }
    }

    public static <T> T parseToList(String json, Class<?> collectionClass, Class<?>... elementClasses) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
            return objectMapper.readValue(json, javaType);
        } catch (Exception var4) {
            var4.printStackTrace();
            throw new RuntimeException(var4);
        }
    }

    public static <T> T parseToObject(String json, TypeReference<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception var3) {
            var3.printStackTrace();
            throw new RuntimeException(var3);
        }
    }

    public static Map parseToMap(String json) {
        Map<String, Object> map = (Map)parseToObject(json, Map.class);
        Iterator iterator = map.entrySet().iterator();

        while(iterator.hasNext()) {
            Entry<String, Object> entry = (Entry)iterator.next();
            if (entry.getValue() instanceof List) {
                List<Object> valueList = (List)entry.getValue();
                if (valueList != null && valueList.size() == 1 && valueList.get(0) instanceof String && StringUtils.isBlank((String)valueList.get(0))) {
                    iterator.remove();
                }
            }
        }

        return map;
    }

    public static Map parseToMapStrStr(String json) {
        return (Map)parseToObject(json, new TypeReference<Map<String, String>>() {
        });
    }

    public static Map parseToMap(byte[] b) {
        return b != null && b.length != 0 ? (Map)parseToObject(b, 0, b.length, Map.class) : null;
    }

    public static Map parseToMap(InputStream is) {
        return (Map)parseToObject(is, Map.class);
    }

    public static String parseToJson(Object o) {
        if (o == null) {
            return null;
        } else {
            try {
                return objectMapper.writeValueAsString(o);
            } catch (Exception var2) {
                var2.printStackTrace();
                throw new RuntimeException(var2);
            }
        }
    }

    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
