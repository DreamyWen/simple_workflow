package com.saith.workflow.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author seth
 * @version 1.0
 * @date 2021/1/11 11:00 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessContext {

    private Map<String, Object> data = new ConcurrentHashMap<>();

    public void addData(String key, Object value) {
        data.put(key, value);
    }

    public void clear() {
        data.clear();
    }
}
