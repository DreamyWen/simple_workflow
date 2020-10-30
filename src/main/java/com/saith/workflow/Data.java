package com.saith.workflow;

public class Data {

    public static DAGNode of(String path) {
        if (path == null || path.isEmpty()) {
            return DAGNode.builder().type("error").build();
        }
        return DAGNode.builder().name(path).type("data").build();
    }

}
