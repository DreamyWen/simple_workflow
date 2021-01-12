package com.saith.workflow.service;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class PrintDto {

    @JSONField(name = "nodes")
    List<DAGNode> dagNodeList;

    @JSONField(name = "edges")
    List<Edge> edgeList;

}
