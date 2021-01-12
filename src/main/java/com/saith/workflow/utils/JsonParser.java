package com.saith.workflow.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.*;

/**
 * @author zhangjiawen
 */
@Data
public class JsonParser {

    private Map<String, Object> pathValue;
    private String json;

    public JsonParser(String json) {
        this.json = json;
        pathValue = new HashMap<>();
        setJsonPaths(json);
    }

    private void setJsonPaths(String json) {
        this.pathValue = new HashMap<>(16);
        JSONObject object = JSONObject.parseObject(json);
        String jsonPath = "$";
        if (object != null) {
            readObject(object, jsonPath);
        }
    }

    private void readObject(JSONObject object, String jsonPath) {
        Iterator<Map.Entry<String, Object>> keysItr = object.entrySet().iterator();
        String parentPath = jsonPath;
        while (keysItr.hasNext()) {
            Map.Entry<String, Object> entry = keysItr.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            jsonPath = parentPath + "." + key;

            if (value instanceof JSONArray) {
                readArray((JSONArray) value, jsonPath);
            } else if (value instanceof JSONObject) {
                readObject((JSONObject) value, jsonPath);
            } else { // is a value
                pathValue.put(jsonPath, value);
            }
        }
    }

    private void readArray(JSONArray array, String jsonPath) {
        String parentPath = jsonPath;
        for (int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            jsonPath = parentPath + "[" + i + "]";

            if (value instanceof JSONArray) {
                readArray((JSONArray) value, jsonPath);
            } else if (value instanceof JSONObject) {
                readObject((JSONObject) value, jsonPath);
            } else { // is a value
                pathValue.put(jsonPath, value);
            }
        }
    }

}