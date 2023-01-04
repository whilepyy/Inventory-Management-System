package com.example.dakudemo.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * @author chh
 * @date 2022/2/26 17:34
 */
public class JsonCovertUtils {
    public static <T> String objToJson(T obj) throws JsonProcessingException {
        return JSON.toJSONString(obj);
    }

    public static <T> T jsonToObj(String json,Class<T> clazz) throws IOException {
        return JSON.parseObject(json,clazz);
    }
}