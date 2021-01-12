package com.saith.workflow.service;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Edge {

    @JSONField(name = "src_node")
    String sourceNode;

    @JSONField(name = "dest_node")
    String destNode;

    public static Edge of(DAGNode source, DAGNode dest) {
        return new Edge(String.valueOf(source.getId()), String.valueOf(dest.getId()));
    }
}
