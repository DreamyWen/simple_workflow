package com.saith.workflow.service;

/**
 * @author zhangjiawen
 * @version 1.0
 * @date 2021/1/11 5:18 下午
 */

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * dag
 *
 * @author saith
 * @date 2021/01/11
 */
@Data
public class DAG {

    public DirectedAcyclicGraph<DAGNode, DefaultEdge> dag;
    Map<String, CompletableFuture<DAGNode>> completableFutureMap = new ConcurrentHashMap<>();

    ProcessContext processContext;

    public DAG() {
        dag = new DirectedAcyclicGraph<>(DefaultEdge.class);
        processContext = new ProcessContext();
    }

    public void addNode(DAGNode node) {
        dag.addVertex(node);
    }

    public void addDependency(DAGNode node1, DAGNode node2) {
        if (!dag.containsVertex(node1)) {
            dag.addVertex(node1);
        }
        if (!dag.containsVertex(node2)) {
            dag.addVertex(node2);
        }
        dag.addEdge(node1, node2);
    }

    /**
     * 构建completeFuture
     * 先启动依赖的任务，依赖执行完，入度-1
     * TODO 数据如何存，全局一个context 还是一个线程一个context
     */
    public void topoSort() {
        Iterator<DAGNode> iterator = dag.iterator();

        while (iterator.hasNext()) {
            DAGNode dagNode = iterator.next();
            int inDegree = dag.inDegreeOf(dagNode);
            int outDegree = dag.outDegreeOf(dagNode);

            Set<DAGNode> neighbor = Graphs.neighborSetOf(dag, dagNode);
            //入度为0的节点 直接调用supplyAsync, 将Processor Add进来
            if (inDegree == 0) {
                CompletableFuture<DAGNode> nodeFuture = CompletableFuture.supplyAsync(() -> {
                    DataSet<Row> result = dagNode.getProcessor().process(new DataSet<>(), processContext);
                    processContext.addData(dagNode.getProcessor().getProcessorKey(), result);
                    System.out.println("执行节点" + dagNode);
                    return dagNode;
                });
                //暂时先用id
                completableFutureMap.put(dagNode.getId().toString(), nodeFuture);
//                System.out.println("放入map " + dagNode.getId().toString() + nodeFuture);
            }
            if (inDegree == 1) {
                //获取父亲节点
                Set<DefaultEdge> incomingEdgeSet = dag.incomingEdgesOf(dagNode);
                DefaultEdge incomingEdge = incomingEdgeSet.stream().findFirst().orElse(new DefaultEdge());
                DAGNode fatherNode = dag.getEdgeSource(incomingEdge);
                System.out.println("获取父亲=" + fatherNode.getId().toString());
                //此处获取可能为null
                CompletableFuture<DAGNode> fatherFuture = completableFutureMap.get(fatherNode.getId().toString());
                CompletableFuture<DAGNode> curFuture = fatherFuture.thenApplyAsync(curNode -> {
                    //获取上个节点结果
                    DataSet<Row> fatherData = (DataSet<Row>) processContext.getData().get(fatherNode.getProcessor().getProcessorKey());
                    //调用该节点过程
                    DataSet<Row> result = dagNode.getProcessor().process(fatherData, processContext);
                    //merge结果
                    DataSet<Row> mergeResult = fatherData.merge(result);
                    processContext.addData(dagNode.getProcessor().getProcessorKey(), mergeResult);
                    return curNode;
                });
                completableFutureMap.put(dagNode.getId().toString(), curFuture);
            }
            if (inDegree > 1) {
                //如果入度比2大,需要合并前面的节点
                //获取前面的所有节点
                Set<DefaultEdge> incomingEdgeSet = dag.incomingEdgesOf(dagNode);
                List<DAGNode> fatherList = incomingEdgeSet.stream().map(i -> dag.getEdgeSource(i)).collect(Collectors.toList());
                List<CompletableFuture<DAGNode>> futureList = fatherList.stream()
                        .map(i -> completableFutureMap.get(i.getId().toString()))
                        .collect(Collectors.toList());
                CompletableFuture<Void> merge = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).thenApplyAsync(
                        curNode -> {
                            //上有节点的全部数据
                            List<DAGNode> nodeList = futureList.stream().map(i -> i.getNow(new DAGNode())).collect(Collectors.toList());
                            DataSet<Row> mergeResult = nodeList.stream().map(i -> {
                                //获取上个节点结果
                                DataSet<Row> fatherData = (DataSet<Row>) processContext.getData().get(i.getProcessor().getProcessorKey());
                                return fatherData;
                            }).reduce(DataSet::merge).orElse(new DataSet<>());
                            ProcessContext processContext = new ProcessContext();
                            processContext.addData(dagNode.getProcessor().getProcessorKey(), mergeResult);
                            return curNode;
                        }
                );
            }
//            Set<DAGNode> ancestorSet = dag.getAncestors(dagNode);
//            Set<DAGNode> dependencySet = dag.getDescendants(dagNode);
//            System.out.println(String.format(Thread.currentThread() + "执行到 %s 临边%s",
//                    dagNode.getName(), neighbor));
//            System.out.println(String.format("执行到 %s 入度 %d 出度 %d 祖先%s 依赖%s ",
//                    dagNode.getName(), inDegree, outDegree, ancestorSet, dependencySet));
//            System.out.println(completableFutureMap);
        }
    }

    public void run() {
    }

    public String toWorkFlow() {
        List<DAGNode> dagNodeList = new ArrayList<>();
        List<Edge> edgeList = new ArrayList<>();
        var ref = new Object() {
            DAGNode prevNode = null;
        };
        AtomicInteger counter = new AtomicInteger(1);
        dag.iterator().forEachRemaining(i -> {
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
