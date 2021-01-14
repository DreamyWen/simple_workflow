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
 * @author zhangjiawen
 * @version 1.0
 * @date 2021/1/8 6:17 下午
 */
//@Slf4j
public class WorkFlowTest {

    @Test
    public void testCompletableFuture() throws Exception {
        // case1: supplyAsync

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Run supplyAsync.");
            return "Return result of Supply Async";
        });

        // case2: thenRun，与supplyAsync同线程
        future.thenRun(new Runnable() {

            @Override
            public void run() {
                System.out.println("Run action.");
            }
        });

        // case2: thenRunAsync，另启动线程执行
        future.thenRunAsync(new Runnable() {

            @Override
            public void run() {
                System.out.println("Run async action.");
            }
        });

        // 主动触发Complete结束方法
        // future.complete("Manual complete value.");
        future.whenComplete((v, e) -> {
            System.out.println("WhenComplete value: " + v);
            System.out.println("WhenComplete exception: " + e);
        });
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            System.out.println("Return result of Run Async.");
        });

        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            return "hello";
        });
        CompletableFuture<String> future4 = CompletableFuture.supplyAsync(() -> {
            return "world";
        });
        CompletableFuture<String> f = future3.thenCombine(future4,
                (x, y) -> x + "-" + y);
        System.out.println(f.get());

    }

    private static Random rand = new Random();
    private static long t = System.currentTimeMillis();

    static int getMoreData() {
        System.out.println("begin to start compute");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("end to start compute. passed " + (System.currentTimeMillis() - t) / 1000 + " seconds");
        return rand.nextInt(1000);
    }

    @Test
    public void testCompletableFutureThen() throws Exception {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(WorkFlowTest::getMoreData);
        Future<Integer> f = future.whenComplete((v, e) -> {
            System.out.println(v);
            System.out.println(e);
        });
        System.out.println(f.get());
//        System.in.read();
    }

    @Test
    public void testCompletableFutureProcessor() throws Exception {
        CompletableFuture<Processor> future1 = CompletableFuture.supplyAsync(BeginProcessor::new);

        Function<Processor, DataSet<Row>> fn1 = x -> {
            DataSet<Row> dataSet = new DataSet<>();
            Row row = new Row();
            row.setData("我是测试1");
            dataSet.setData(Collections.singletonList(row));
            return x.process(dataSet, null, new ProcessContext());
        };

        Function<Processor, Processor> fn2 = x -> {
            DataSet<Row> dataSet = new DataSet<>();
            Row row = new Row();
            row.setData("我是测试2");
            dataSet.setData(Collections.singletonList(row));
            x.process(dataSet, null, new ProcessContext());
            return x;
        };

        Function<Processor, Processor> fn3 = x -> {
            DataSet<Row> dataSet = new DataSet<>();
            Row row = new Row();
            row.setData("我是测试3");
            dataSet.setData(Collections.singletonList(row));
            x.process(dataSet, null, new ProcessContext());
            return x;
        };

        future1.thenApply(fn2).thenApply(fn3);

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            return 100;
        });

//        future.whenComplete((a, b) -> {
//            System.out.println("hello world!");
//            System.out.println(a);
//            System.out.println(b);
//        });

//        future.get();
    }

    @Test
    public void composeVsApply() {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            return 100;
        });
        CompletableFuture<String> f = future.thenCompose(i -> {
            return CompletableFuture.supplyAsync(() -> {
                System.out.println(i * 10);
                return (i * 10) + "";
            });
        });

//        try {
//            System.out.println(f.get()); //1000
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void testPipeLine() throws Exception {
        String out = Pipeline.applyBegin(Data.of("url='hdfs://abc.txt'"))
                .apply(new PrintFunction("A"))
                .apply(new PrintFunction("B"))
//                .apply(new PrintFunction("C"))
                .toWorkFlow();
        System.out.println(out);
    }


    @Test
    public void testDAG() throws Exception {
        DAGNode node1 = DAGNode.from("1", "1", 1, new BeginProcessor(1));
        DAGNode node2 = DAGNode.from("2", "2", 2, new BeginProcessor(2));
        DAGNode node3 = DAGNode.from("3", "3", 3, new BeginProcessor(3));
        DAGNode node4 = DAGNode.from("4", "4", 4, new BeginProcessor(4));
        DAGNode node5 = DAGNode.from("5", "5", 5, new BeginProcessor(5));
        DAGNode node6 = DAGNode.from("6", "6", 6, new BeginProcessor(6));
        DAGNode node7 = DAGNode.from("7", "7", 7, new BeginProcessor(7));
        //每一个processor  进去的是 dataSet 和 context
        DAG dag = new DAG();
        dag.addDependency(node1, node2);
        dag.addDependency(node1, node3);
        dag.addDependency(node1, node6);
        dag.addDependency(node2, node4);
        dag.addDependency(node3, node5);
        dag.addDependency(node4, node7);
        dag.addDependency(node5, node7);

        try {
            dag.topoSort();
            Thread.sleep(100000);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(Thread.currentThread());
        }
    }

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

    @Test
    public void testDAGFuture() throws Exception {
        DAGNode node1 = DAGNode.from("1", "1", 1, new BeginProcessor());
        DAGNode node2 = DAGNode.from("2", "2", 2, new BeginProcessor());
        DAGNode node3 = DAGNode.from("3", "3", 3, new BeginProcessor());
        DAGNode node4 = DAGNode.from("4", "4", 4, new BeginProcessor());
        DAGNode node5 = DAGNode.from("5", "5", 5, new BeginProcessor());
        DAGNode node6 = DAGNode.from("6", "6", 6, new BeginProcessor());
        //每一个processor  进去的是 dataSet 和 context
        DAG dag = new DAG();
        dag.addDependency(node1, node2);
        dag.addDependency(node1, node3);
        dag.addDependency(node1, node6);
        dag.addDependency(node2, node4);
        dag.addDependency(node3, node5);
        dag.addDependency(node4, node5);

        //目标就是把上面的依赖转为下面的completeFuture
        CompletableFuture<DAGNode> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("运行1");
            return node1;
        });
        CompletableFuture<DAGNode> future2 = future1.thenApplyAsync((pre) -> {
            sleep(2, TimeUnit.SECONDS);
            System.out.println("运行2");
            return node2;
        });
        CompletableFuture<DAGNode> future3 = future1.thenApplyAsync((pre) -> {
            sleep(2, TimeUnit.SECONDS);
            System.out.println("运行3");
            return node3;
        });
        CompletableFuture<DAGNode> future6 = future1.thenApplyAsync((pre) -> {
            sleep(2, TimeUnit.SECONDS);
            System.out.println("运行6");
            return node6;
        });
        CompletableFuture<DAGNode> future4 = future2.thenApplyAsync((pre) -> {
            System.out.println("运行4");
            return node4;
        });
        CompletableFuture<DAGNode> future5 = future3.thenApplyAsync((pre) -> {
            System.out.println("运行5");
            return node5;
        });
        CompletableFuture.allOf(future4, future5).thenApply(node -> {
                    System.out.println("运行7");
                    return node6;
                }
        );
        sleep(5, TimeUnit.SECONDS);
    }

    private static void sleep(final int duration, final TimeUnit unit) {
        try {
            unit.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDAG1() throws Exception {
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
