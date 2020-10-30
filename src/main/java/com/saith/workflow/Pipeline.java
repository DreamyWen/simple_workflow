package com.saith.workflow;

import com.alibaba.fastjson.JSON;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Pipeline {

    private final DirectedAcyclicGraph<DAGNode, String> dag;
    private DAGNode prev;

    public Pipeline() {
        dag = new DirectedAcyclicGraph<>(String.class);
    }

    public Pipeline apply(DAGNode node) {
        applyDep(prev, node);
        prev = node;
        return this;
    }

    private Pipeline applyBeginNode() {
        prev = new PBegin();
        dag.addVertex(prev);
        return this;
    }

    private Pipeline applyDep(DAGNode node1, DAGNode node2) {
//        dag.addVertex(node1);
        dag.addVertex(node2);
        dag.addEdge(node1, node2);
        return this;
    }

    public static Pipeline applyBegin(DAGNode node) {
        Pipeline p = new Pipeline();
        p.applyBeginNode();
        p.apply(node);
        return p;
    }

    public String toWorkFlow() {
        List<DAGNode> dagNodeList = new ArrayList<>();
        List<Edge> edgeList = new ArrayList<>();
        var ref = new Object() {
            DAGNode prevNode = null;
        };
        AtomicInteger counter = new AtomicInteger(1);
        dag.iterator().forEachRemaining(i-> {
            if (Objects.equals(i.getName(), "start")) {
                return;
            }
            i.setId(counter.getAndIncrement());
            dagNodeList.add(i);
            if (ref.prevNode != null) {
                edgeList.add(Edge.of(ref.prevNode, i));
            }
            ref.prevNode = i;
        });

        return JSON.toJSONString(PrintDto.builder().dagNodeList(dagNodeList).edgeList(edgeList).build());
    }
}
