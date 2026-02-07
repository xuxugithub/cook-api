package com.cooking.util;



import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * JSON解析工具类
 */
public class JsonUtils {

    /**
     * 字符串转JSON对象
     */
    public static JSONObject parseObject(String text) {
        try {
            return JSON.parseObject(text);
        } catch (Exception e) {
            throw new RuntimeException("JSON解析失败：" + text, e);
        }
    }

    /**
     * 对象转JSON字符串
     */
    public static String toJsonString(Object obj) {
        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }
}
