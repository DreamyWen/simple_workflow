package com.saith.workflow.service;

import com.saith.workflow.processor.BeginProcessor;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.testng.annotations.Test;

import java.util.Set;

/**
 * @author seth
 * @version 1.0
 * @date 2021/1/8 6:17 下午
 */
public class DAGTest {

    @Test
    public void testDAG() throws Exception {
        DirectedAcyclicGraph<DAGNode, String> dag = new DirectedAcyclicGraph<>(String.class);

        DAGNode node1 = DAGNode.from("1", "1", 1, new BeginProcessor());
        DAGNode node2 = DAGNode.from("2", "2", 2, new BeginProcessor());
        DAGNode node3 = DAGNode.from("3", "3", 3, new BeginProcessor());
        DAGNode node4 = DAGNode.from("4", "4", 4, new BeginProcessor());
        DAGNode node5 = DAGNode.from("5", "5", 5, new BeginProcessor());
        DAGNode node6 = DAGNode.from("6", "6", 6, new BeginProcessor());
        //每一个processor  进去的是 dataSet 和 context
        dag.addVertex(node1);
        dag.addVertex(node2);
        dag.addVertex(node3);
        dag.addVertex(node4);
        dag.addVertex(node5);
        dag.addVertex(node6);

        dag.addEdge(node1, node2, "node1->node2");
        dag.addEdge(node1, node3, "node1->node3");
        dag.addEdge(node1, node6, "node1->node6");

        Set<DAGNode> dagNodeSet = dag.getDescendants(node1);
        System.out.println(dagNodeSet);
    }


}
