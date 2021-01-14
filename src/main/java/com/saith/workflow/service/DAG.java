package com.saith.workflow.service;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.lang3.SerializationUtils;
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
 * @author zhangjiawen
 * @date 2021/01/11
 */
@Data
public class DAG {

    public DirectedAcyclicGraph<DAGNode, DefaultEdge> dag;
    Map<String, CompletableFuture<DAGNode>> completableFutureMap = new ConcurrentHashMap<>();

    /***
     *   TODO 存取上下文，需要决定gc的时机
     */
    ProcessContext processContext;

    public DAG() {
        dag = new DirectedAcyclicGraph<>(DefaultEdge.class);
        processContext = new ProcessContext();
    }

    public void addNode(DAGNode node) {
        dag.addVertex(node);
    }

    /***
     *  加依赖，可以此时增加init初始化
     */
    public void addDependency(DAGNode node1, DAGNode node2) {
        if (!dag.containsVertex(node1)) {
            node1.getProcessor().init();
            dag.addVertex(node1);
        }
        if (!dag.containsVertex(node2)) {
            node2.getProcessor().init();
            dag.addVertex(node2);
        }
        dag.addEdge(node1, node2);
    }

    /**
     * 构建completeFuture
     * 先启动依赖的任务，依赖执行完，入度-1
     * TODO 数据如何存，全局一个context 还是一个线程一个context
     * TODO 内存问题，什么时候gc不用的数据，在每次topo排序是 是否需要gc
     * TODO 性能问题，深拷贝 使用clone方式 还是 kyro
     */
    public void topoSort() {
        processContext.clear();

        Iterator<DAGNode> iterator = dag.iterator();

        while (iterator.hasNext()) {
            DAGNode dagNode = iterator.next();
            int inDegree = dag.inDegreeOf(dagNode);

            //入度为0的节点 直接调用supplyAsync, 将Processor Add进来
            if (inDegree == 0) {
                CompletableFuture<DAGNode> nodeFuture = CompletableFuture.supplyAsync(() -> {
                    DataSet<Row> result = dagNode.getProcessor().process(new DataSet<>(), null, processContext);
                    processContext.addData(dagNode.getProcessor().getProcessorKey(), result);
                    return dagNode;
                });
                //TODO 暂时先用id
//                completableFutureMap.put(dagNode.getId().toString(), nodeFuture);
                completableFutureMap.put(dagNode.getProcessor().getProcessorKey(), nodeFuture);
            }
            if (inDegree == 1) {
                //获取父亲节点
                Set<DefaultEdge> incomingEdgeSet = dag.incomingEdgesOf(dagNode);
                DefaultEdge incomingEdge = incomingEdgeSet.stream().findFirst().orElse(new DefaultEdge());
                DAGNode fatherNode = dag.getEdgeSource(incomingEdge);
                //此处获取可能为null
                CompletableFuture<DAGNode> fatherFuture = completableFutureMap.get(fatherNode.getProcessor().getProcessorKey());
                CompletableFuture<DAGNode> curFuture = fatherFuture.thenApplyAsync(curNode -> {
                    //获取上个节点结果
                    DataSet<Row> fatherData = (DataSet<Row>) processContext.getData().get(fatherNode.getProcessor().getProcessorKey());
                    //深拷贝 kyro 方式 或者自己实现clone
                    //TODO 待测试性能 如果子类没有实现Serializable 可能会抛出异常
                    //https://stackoverflow.com/Questions/2156120/java-recommended-solution-for-deep-cloning-copying-an-instance
                    DataSet<Row> fatherDataClone = SerializationUtils.clone(fatherData);
                    //调用该节点过程
                    DataSet<Row> result = dagNode.getProcessor().process(fatherDataClone, null, processContext);
                    processContext.addData(dagNode.getProcessor().getProcessorKey(), result);
                    return curNode;
                });
                //放入future
                completableFutureMap.put(dagNode.getProcessor().getProcessorKey(), curFuture);
            }
            if (inDegree > 1) {
                //如果入度比2大,需要合并前面的节点
                //获取前面的所有节点
                Set<DefaultEdge> incomingEdgeSet = dag.incomingEdgesOf(dagNode);
                List<DAGNode> fatherList = incomingEdgeSet.stream().map(i -> dag.getEdgeSource(i)).collect(Collectors.toList());
                List<CompletableFuture<DAGNode>> futureList = fatherList.stream()
                        .map(i -> completableFutureMap.get(i.getProcessor().getProcessorKey()))
                        .collect(Collectors.toList());
                CompletableFuture<DAGNode> merge = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).thenApplyAsync(
                        curNode -> {
                            //获取上个节点结果
                            Map<String, DataSet<Row>> mutilProcessorDataSet = new HashMap<>(16);
                            fatherList.forEach(i -> {
                                //获取上个节点结果
                                DataSet<Row> fatherData = (DataSet<Row>) processContext.getData().get(i.getProcessor().getProcessorKey());
                                System.out.println("合并时上个节点的数据" + fatherData);
                                //深拷贝
                                DataSet<Row> fatherDataClone = SerializationUtils.clone(fatherData);
                                mutilProcessorDataSet.put(i.getProcessor().getProcessorKey(), fatherDataClone);
                            });
                            DataSet<Row> result = dagNode.getProcessor().process(new DataSet<>(), mutilProcessorDataSet, processContext);
                            processContext.addData(dagNode.getProcessor().getProcessorKey(), result);
                            return dagNode;
                        }
                );
                completableFutureMap.put(dagNode.getProcessor().getProcessorKey(), merge);
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
