package com.saith.workflow.service;

import com.saith.workflow.processor.Processor;
import lombok.*;
import lombok.Data;

import javax.naming.Context;

/**
 * @author saith
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
//    ProcessContext context;

//    public static DAGNode from(String name, String type, Integer id, Processor processor, ProcessContext context) {
//        return new DAGNode(name, type, id, processor, context);
//    }

    public static DAGNode from(String name, String type, Integer id, Processor processor) {
        return new DAGNode(name, type, id, processor);
    }

}
