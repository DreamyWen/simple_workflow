package com.saith.workflow.service;

import com.saith.workflow.processor.Processor;
import lombok.Data;
import lombok.*;

/**
 * dagnode
 *
 * @author saith
 * @date 2021/01/12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DAGNode {

    String name;
    String type;
    Integer id;
    Processor processor;

    public static DAGNode from(String name, String type, Integer id, Processor processor) {
        return new DAGNode(name, type, id, processor);
    }

}
