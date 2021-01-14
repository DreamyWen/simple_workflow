package com.saith.workflow.service;

import com.saith.workflow.processor.AddProcessor;
import com.saith.workflow.processor.BeginProcessor;
import com.saith.workflow.processor.MultiPlyProcessor;
import com.saith.workflow.processor.Processor;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author seth
 * @version 1.0
 * @date 2021/1/8 6:17 下午
 */
public class DemoTest {

    @Test
    public void testDAGCompute() throws Exception {
        DAGNode node1 = DAGNode.from("1", "1", 1, new AddProcessor(1, 1, ""));
        DAGNode node2 = DAGNode.from("2", "2", 2, new AddProcessor(2, 2, ""));
        DAGNode node3 = DAGNode.from("3", "3", 3, new AddProcessor(3, 3, ""));
        DAGNode node4 = DAGNode.from("4", "4", 4, new MultiPlyProcessor(4, 4, ""));
        DAGNode node5 = DAGNode.from("5", "5", 5, new AddProcessor(5, 5, ""));
        DAGNode node6 = DAGNode.from("6", "6", 6, new AddProcessor(6, 6, ""));
        DAGNode node7 = DAGNode.from("7", "7", 7, new MultiPlyProcessor(7, 7, ""));
        DAGNode node8 = DAGNode.from("8", "8", 8, new AddProcessor(8, 8, ""));
        DAGNode node9 = DAGNode.from("9", "9", 9, new AddProcessor(9, 9, ""));
        DAGNode node10 = DAGNode.from("10", "10", 10, new AddProcessor(10, 10, ""));
        DAGNode node11 = DAGNode.from("11", "11", 11, new AddProcessor(11, 11, ""));
        //每一个processor  进去的是 dataSet 和 context
        DAG dag = new DAG();
        dag.addDependency(node1, node2);
        dag.addDependency(node1, node3);
        dag.addDependency(node1, node6);
        dag.addDependency(node2, node4);
        dag.addDependency(node3, node5);
        dag.addDependency(node4, node7);
        dag.addDependency(node5, node7);
        dag.addDependency(node7, node8);
//        dag.addDependency(node9, node10);
//        dag.addDependency(node10, node2);
//        dag.addDependency(node11, node2);

        try {
            dag.topoSort();
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(Thread.currentThread());
        }
    }

}
